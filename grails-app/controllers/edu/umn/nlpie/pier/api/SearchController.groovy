package edu.umn.nlpie.pier.api

import edu.umn.nlpie.pier.api.exception.BadElasticRequestException
import edu.umn.nlpie.pier.api.exception.HttpMethodNotAllowedException
import edu.umn.nlpie.pier.api.exception.PierApiException
import edu.umn.nlpie.pier.audit.Query
import edu.umn.nlpie.pier.audit.SearchRegistration
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
	
	def search() { }
	
	def postBody() {
		params.each { println it }
	}
	
	def elastic() {
		//println request.JSON.toString(2)
		
		//TODO refactor this to elasticservice and auditservice
		try {
			//if ( request.method!="POST" ) throw new HttpMethodNotAllowedException(message:"issue GET instead")
			def postBody = request.JSON
			println postBody.toString(2)
			auditService.record(postBody)
			def elasticResponse = elasticService.search( postBody.url, postBody.query )
			def status = elasticResponse.status
			if ( status==400 ) {
				//println elasticResponse.json.toString(2)
				throw new BadElasticRequestException( "Malformed query - check your syntax" )
			}
			render elasticResponse.json 
		} catch( PierApiException e) {
			respondWithException(e)
		} catch( ValidationException e) {
			respondWithException( new PierApiException( message:e.message ) )
		} catch( Exception e) {
			respondWithException( new PierApiException( message:e.message ) ) 
		}
	}
	
}
