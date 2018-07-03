package edu.umn.nlpie.pier.api

import edu.umn.nlpie.pier.api.exception.HttpMethodNotAllowedException
import edu.umn.nlpie.pier.api.exception.PierApiException
import grails.plugin.springsecurity.annotation.Secured

@Secured(["ROLE_USER"])
class UmlsController {//extends RestfulController {
	
	static responseFormats = ['json']
	
	def umlsService
	
	//TODO refactor to superclass
	private exceptionResponse(Exception e) {
		def msg = e.message.replace('\n',' ')	//\n causes problems when client parses returned JSON
		respond ( e, status:e.status )
		//render(status: e.status, text: '{"message":"'+ msg +'"}', contentType: "application/json") as JSON
	}
	
	def string() {
		//TODO verify user has access to corpus/index
		try {
			//println params.id
			if ( request.method!="GET" ) throw new HttpMethodNotAllowedException(message:"issue GET instead")
			def umlsEntry = umlsService.umlsStringFromIndex( params.id )
			respond umlsEntry.json// as JSON
		} catch (PierApiException e) {
			println "pier exception"
			exceptionResponse(e)
		} catch (Exception e) {
			println "umls lookup exception"
			e.printStackTrace()
			exceptionResponse( new PierApiException( message:e.message) )
		}
	}
	
}
