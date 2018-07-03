package edu.umn.nlpie.pier.api

import edu.umn.nlpie.pier.api.exception.HttpMethodNotAllowedException
import edu.umn.nlpie.pier.api.exception.PierApiException
import grails.plugin.springsecurity.annotation.Secured



@Secured(["ROLE_USER"])
class AuditController {//extends RestfulController {
	
	static responseFormats = ['json']
	
	def auditService
	
	//TODO refactor to superclass
	private exceptionResponse(Exception e) {
		def msg = e.message.replace('\n',' ')	//\n causes problems when client parses returned JSON
		respond ( e, status:e.status )
		//render(status: e.status, text: '{"message":"'+ msg +'"}', contentType: "application/json") as JSON
	}
	
	//clients must register before issuing a search request
	def register() {
		//lookup user
		//verify user has access to corpus/index
		try {
			if ( request.method!="POST" ) throw new HttpMethodNotAllowedException(message:"issue POST instead")
			respond auditService.register(request.JSON)
		} catch (PierApiException e) {
			println "pier exception"
			exceptionResponse(e)
		} catch (Exception e) {
			println "reg exception"
			e.printStackTrace()
			exceptionResponse( new PierApiException(message:e.message) )
		}
	}
	
}
