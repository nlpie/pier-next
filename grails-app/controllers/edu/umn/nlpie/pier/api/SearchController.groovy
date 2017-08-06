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
	
	def historySummary() {
		def jsonBody = request.JSON
		//TODO put exception handling in place
		respond searchService.searchHistory( jsonBody.excludeMostRecent )	//projection 
	}
	
	def registeredSearch() {
		respond searchService.registeredSearch(params.id)
	}
	
}
