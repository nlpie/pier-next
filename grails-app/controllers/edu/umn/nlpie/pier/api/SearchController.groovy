package edu.umn.nlpie.pier.api

import org.apache.commons.lang.StringEscapeUtils

import edu.umn.nlpie.pier.api.exception.BadElasticRequestException
import edu.umn.nlpie.pier.api.exception.HttpMethodNotAllowedException
import edu.umn.nlpie.pier.api.exception.PierApiException
import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import grails.plugins.rest.client.RestBuilder
import grails.validation.ValidationException
import groovy.json.JsonBuilder
import javax.servlet.http.Cookie


@Secured(["ROLE_USER"])
class SearchController {//extends RestfulController {
	
	static responseFormats = ['json']
	//static allowedMethods = ["elastic": "GET", "find": "POST"]
	
	def elasticService
	def searchService
	def auditService
	def springSecurityService
	
	//TODO refactor to superclass
	private respondWithException(Exception e) {
		def msg = e.message.replace('\n',' ')	//\n causes problems when client parses returned JSON
		respond ( e, status:e.status )
		//render(status: e.status, text: '{"message":"'+ msg +'"}', contentType: "application/json") as JSON
	}
	
	def bookmark() {
		if ( request.JSON?.bookmark ) {
			//println "setting bookmark cookie"
			//set cookie
			println request.JSON?.bookmark
			Cookie cookie = new Cookie("bookmark",request.JSON.bookmark)
			cookie.maxAge = 100
			cookie.path = "/${grailsApplication.metadata['app.name']}/"
			response.addCookie( cookie )
		}
	}
	
	def index() { }	//default search view
	
	//def search() { }	//deprecated 
	
	def elastic() {
		//println request.JSON.toString(2)
		def postBody = request.JSON
		def elasticResponse
		try {
			if ( request.method!="POST" ) throw new HttpMethodNotAllowedException(message:"issue GET instead")
			//TODO sanity check on request.JSON - needs query, url, searchRequest.id, etc
			//println postBody.query.toString(2)
			elasticResponse = elasticService.search( postBody.url, postBody.query )
			//println elasticResponse.json.toString(2)
			def status = elasticResponse.status
			if ( status==400 ) {
				throw new BadElasticRequestException( message:"Malformed query - check your syntax" )
			}
			if ( status==500 ) {
				throw new PierApiException( message:elasticResponse.json.error.root_cause.reason )
			}

			def query = auditService.logQueryAndResponse( request, postBody, elasticResponse )
			elasticResponse.json.query = query
			respond elasticResponse.json 
		} catch( PierApiException e) {
			auditService.logException ( request, postBody, elasticResponse, e )
			respondWithException(e)
		} catch( ValidationException e) {
			auditService.logException ( request, postBody, elasticResponse, e )
			respondWithException( new PierApiException( postBody, elasticResponse, e ) )
		} catch( Exception e) {
			auditService.logException ( request, postBody, elasticResponse, e )
			respondWithException( new PierApiException( message:e.message ) ) 
		} finally {
		
		}
		/*500 error from elastic when trying deep paging
		{
			"error": {
			  "root_cause": [
				{
				  "type": "query_phase_execution_exception",
				  "reason": "Result window is too large, from + size must be less than or equal to: [10000] but was [510000]. See the scroll api for a more efficient way to request large data sets. This limit can be set by changing the [index.max_result_window] index level parameter."
				}
			  ],
			  "type": "search_phase_execution_exception",
			  "reason": "all shards failed",
			  "phase": "query",
			  "grouped": true,
			  "failed_shards": [
				{
				  "shard": 0,
				  "index": "notes_v3",
				  "node": "YXHHd9aqSN-S9Pu33CIUXQ",
				  "reason": {
					"type": "query_phase_execution_exception",
					"reason": "Result window is too large, from + size must be less than or equal to: [10000] but was [510000]. See the scroll api for a more efficient way to request large data sets. This limit can be set by changing the [index.max_result_window] index level parameter."
				  }
				}
			  ]
			},
			"status": 500
		  }
		  */
	}
	
	def export() {
		//https://stackoverflow.com/questions/48839084/prevent-angular-http-post-request-timeout
		def postBody = request.JSON
		def payload = postBody.payload
		def fieldMetadata = postBody.fieldMetadata
		payload.sort = ["_doc"]
		def elasticResponse
		
		def downloadFileName = payload.corpus.replace(" ","") << "__"
		downloadFileName << payload.query.query.bool.must.query_string.query[0].replace(" ","_")

		try {
			if ( request.method!="POST" ) throw new HttpMethodNotAllowedException(message:"issue GET instead")
			elasticResponse = elasticService.search( payload.url+"?scroll=5m", payload.query )
//println elasticResponse.json.toString(2)
			def status = elasticResponse.status
			if ( status==400 ) {
				throw new BadElasticRequestException( message:"Malformed query - check your syntax" )
			}
			if ( status==500 ) {
				throw new PierApiException( message:elasticResponse.json.error.root_cause.reason )
			}
			
			def query = auditService.logQueryAndResponse( request, payload, elasticResponse )
			
			//https://stackoverflow.com/questions/23055103/grails-how-to-force-the-browser-to-download-a-file
			response.setContentType("APPLICATION/OCTET-STREAM")
			response.setHeader("Content-Disposition", "Attachment;Filename=${downloadFileName}.csv")
			
			def data = response.outputStream
			def columns = fieldMetadata.columns.sort()
			def fields = fieldMetadata.fields.sort()
			data << fields.join(",") << "\n"	//change to PIPE DEL?
println "${downloadFileName} initial scroll hits:${elasticResponse.json.hits.hits.size()} , took:${elasticResponse.json.took}"
			elasticResponse.json.hits.hits.each { hit ->
				fields.each { f ->
					def val = StringEscapeUtils.escapeCsv( hit.fields[f] ? hit.fields[f][0].toString() : "null" )
					if (f=="mrn") val = "mrn:"+val.toString()
					data << val
					if ( f!=fields[fields.length()-1] ) {
						data << "," //change to PIPE DEL?
					} else {
						data << "\n"
					}
				}
			}
			data.flush()
			
			def scrollId = elasticResponse.json._scroll_id
			def scroll = true
			while ( scroll ) {
				def sp = new ScrollPayload( scroll_id:scrollId, scroll:"5m" )
				elasticResponse = elasticService.scroll( payload.scrollUrl, sp )
println "\t${downloadFileName} additional scroll hits:${elasticResponse.json.hits.hits.size()} , took:${elasticResponse.json.took}"
				elasticResponse.json.hits.hits.each { hit ->
				fields.each { f ->
					def val = StringEscapeUtils.escapeCsv( hit.fields[f] ? hit.fields[f][0].toString() : "null" )
					if (f=="mrn") val = "mrn:"+val.toString()
					data << val
					if ( f!=fields[fields.length()-1] ) {
						data << ","
					} else {
						data << "\n"
						}
					}
				}
				scroll = elasticResponse.json.hits.hits.size()>0 ? true : false
				scrollId = elasticResponse.json._scroll_id
				data.flush()
			}
			data.close()
		} catch( PierApiException e) {
			auditService.logException ( request, payload, elasticResponse, e )
			respondWithException(e)
		} catch( ValidationException e) {
			auditService.logException ( request, payload, elasticResponse, e )
			respondWithException( new PierApiException( payload, elasticResponse, e ) )
		} catch( Exception e) {
			auditService.logException ( request, payload, elasticResponse, e )
			respondWithException( new PierApiException( message:e.message ) )
			//e.printStackTrace()
		} finally {
			//data.close()
		}
	}
	
	def bucketCount() {
		//println request.JSON.toString(2)
		def postBody = request.JSON
		def elasticResponse
		try {
			if ( request.method!="POST" ) throw new HttpMethodNotAllowedException(message:"issue GET instead")
			//TODO sanity check on request.JSON - needs query, url, searchRequest.id, etc
			elasticResponse = elasticService.search( postBody.url, postBody.query )
			def status = elasticResponse.status
			//println "distinct: ${status} ${elasticResponse.json.toString(2)}"
			
			if ( status==400 ) {
				throw new BadElasticRequestException( message:"Malformed query - check your syntax" )
			}
			def distinctCount = searchService.logBucketCountInfo( postBody, elasticResponse )
			//respond elasticResponse.json
			respond distinctCount
		} catch( PierApiException e) {
			//auditService.logException ( postBody, elasticResponse, e )
			respondWithException(e)
		} catch( ValidationException e) {
			//auditService.logException ( postBody, e )
			e.printStackTrace()
			respondWithException( new PierApiException( message:e.message ) )
		} catch( Exception e) {
			//auditService.logException ( postBody, elasticResponse, e )
			respondWithException( new PierApiException( message:e.message ) )
		} finally {
		
		}
	}
	
	def scrollCount() {
		def postBody = request.JSON
		def elasticResponse
		//try {
			if ( request.method!="POST" ) throw new HttpMethodNotAllowedException(message:"issue GET instead")
			def scrollInitiationUrl = postBody.url + "?scroll=1m"
			//TODO sanity check on request.JSON - needs query, url, searchRequest.id, etc
			elasticResponse = elasticService.search( scrollInitiationUrl, postBody.query )
			def status = elasticResponse.status
			//println "distinct: ${status} ${elasticResponse.json.toString(2)}"
			
			if ( status==400 ) {
				throw new BadElasticRequestException( message:"Malformed query - check your syntax" )
			}
			def scrollCount = searchService.logScrollCountInfo( postBody, elasticResponse )
			//respond elasticResponse.json
			respond scrollCount
		/*} catch( PierApiException e) {
			println "SCROLL COUNT ERROR"
			println e.toString()
			//auditService.logException ( postBody, elasticResponse, e )
			respondWithException(e)
		} catch( ValidationException e) {
			//auditService.logException ( postBody, e )
			e.printStackTrace()
			respondWithException( new PierApiException( message:e.message ) )
		} catch( Exception e) {
			//auditService.logException ( postBody, elasticResponse, e )
			respondWithException( new PierApiException( message:e.message ) )
		} finally {
		
		}*/
	}
	
	def historySummary() {
		def jsonBody = request.JSON
		//TODO put exception handling in place
		respond searchService.searchHistory( jsonBody.excludeMostRecent )	//projection 
	}
	
	def savedQueriesByContext() {
		//TODO put exception handling in place
		respond searchService.savedQueriesByContext( request.JSON.authorizedContext )
	}
	
	def savedQueriesByUserExcludingContext() {
		//TODO put exception handling in place
		respond searchService.savedQueriesByUserExcludingContext( request.JSON.authorizedContext )
	}
	
	def recentQuery() {
		JSON.use ('recent.query') {
			respond searchService.recentQuery(params.id)
		}
	}
	
	def related() {
		def jsonBody = request.JSON
		respond elasticService.fetchRelated(jsonBody.url, jsonBody.term).json
	}
	
}
