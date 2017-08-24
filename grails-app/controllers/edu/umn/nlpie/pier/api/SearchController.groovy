package edu.umn.nlpie.pier.api

import edu.umn.nlpie.pier.api.exception.BadElasticRequestException
import edu.umn.nlpie.pier.api.exception.HttpMethodNotAllowedException
import edu.umn.nlpie.pier.api.exception.PierApiException
import edu.umn.nlpie.pier.audit.Query
import edu.umn.nlpie.pier.audit.SearchRegistration
import grails.converters.JSON
import grails.validation.ValidationException

class SearchController {//extends RestfulController {
	
	static responseFormats = ['json']
	//static allowedMethods = ["elastic": "GET", "find": "POST"]
	
	def elasticService
	def searchService
	def auditService
	
	//TODO refactor to superclass
	private respondWithException(Exception e) {
		def msg = e.message.replace('\n',' ')	//\n causes problems when client parses returned JSON
		respond ( e, status:e.status )
		//render(status: e.status, text: '{"message":"'+ msg +'"}', contentType: "application/json") as JSON
	}
	
	def search() { }	//default search view 
	
	def elastic() {
		//println request.JSON.toString(2)
		def postBody = request.JSON
		def elasticResponse
		try {
			if ( request.method!="POST" ) throw new HttpMethodNotAllowedException(message:"issue GET instead")
			//TODO sanity check on request.JSON - needs query, url, searchRequest.id, etc
			//println postBody.toString(2)
			elasticResponse = elasticService.search( postBody.url, postBody.query )
			def status = elasticResponse.status
			if ( status==400 ) {
				throw new BadElasticRequestException( message:"Malformed query - check your syntax" )
			}
			auditService.logQueryAndResponse( postBody, elasticResponse )
			respond elasticResponse.json 
		} catch( PierApiException e) {
			auditService.logException ( postBody, elasticResponse, e )
			respondWithException(e)
		} catch( ValidationException e) {
			auditService.logException ( postBody, e )
			respondWithException( new PierApiException( postBody, elasticResponse, e ) )
		} catch( Exception e) {
			auditService.logException ( postBody, elasticResponse, e )
			respondWithException( new PierApiException( message:e.message ) ) 
		} finally {
		
		}
	}
	
	def distinct() {
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
			def distinctCount = searchService.logDistinctCountInfo( postBody, elasticResponse )
			//respond elasticResponse.json
			respond distinctCount
		/ catch( PierApiException e) {
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
	
	/*
	SELECT 'serotonin', dc.id as distinct_count_id, dc.registration_id, dc.terms, dc.label,
	(select size from distinct_count where terms='serotonin' and label=dc.label) as correct_spelling_count,
	(select count(*) from bucket where distinct_count_id=dc.id) as potential_marginal_count,
	(select count(*) from bucket where distinct_count_id=dc.id
		and `key` not in (
			select `key` from bucket where distinct_count_id=(
				select id from distinct_count where terms='serotonin' and label=dc.label
			)
		)
	) as actual_marginal_count
	FROM notes_next.distinct_count dc
	where dc.label in ('Mrn', 'Note id')
	and terms like '% NOT serotonin'
	
	'serotonin'
	'seborrheic'
	'paroxysmal'
	'basilar'
	'chalazion'
	'chlamydia'
	'ascites'
	
	cirrhosis

	*/
	
	def historySummary() {
		def jsonBody = request.JSON
		//TODO put exception handling in place
		respond searchService.searchHistory( jsonBody.excludeMostRecent )	//projection 
	}
	
	def registeredSearch() {
		respond searchService.registeredSearch(params.id)
	}
	
}
