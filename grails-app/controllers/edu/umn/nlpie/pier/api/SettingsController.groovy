package edu.umn.nlpie.pier.api

import edu.umn.nlpie.pier.api.exception.HttpMethodNotAllowedException
import edu.umn.nlpie.pier.api.exception.PierApiException
import edu.umn.nlpie.pier.elastic.Index
import edu.umn.nlpie.pier.springsecurity.User
import edu.umn.nlpie.pier.ui.FieldPreference
import edu.umn.nlpie.pier.ui.Ontology
import grails.converters.JSON
import grails.transaction.Transactional
import grails.util.Environment

class SettingsController {//extends RestfulController {
	
	def settingsService 
	static responseFormats = ['json']
	
	//TODO refactor to superclass
	private exceptionResponse(Exception e) {
		def msg = e.message.replace('\n',' ')	//\n causes problems when client parses returned JSON
		respond ( e, status:e.status )
		//render(status: e.status, text: '{"message":"'+ msg +'"}', contentType: "application/json") as JSON
	}
	
	def index() {}
	
	def preferences() {
		try {
			if ( request.method!="GET" ) throw new HttpMethodNotAllowedException(message:"issue GET instead")
			def m = settingsService.preferences()
			JSON.use("fieldpreference") {
				respond m
			}
		} catch (PierApiException e) {
			println "pier exception"
			exceptionResponse(e)
		} catch (Exception e) {
			println "reg exception"
			e.printStackTrace()
			exceptionResponse( new PierApiException(message:e.message) )
		}
	}
	
	def corpusAggregations() {
		try {
			if ( request.method!="GET" ) throw new HttpMethodNotAllowedException(message:"issue GET instead")
			def corpusId = params.id
			def f = settingsService.corpusAggregations(corpusId)
			JSON.use("fieldpreference") {
				respond f
			}
		} catch (PierApiException e) {
			println "pier exception"
			exceptionResponse(e)
		} catch (Exception e) {
			println "reg exception"
			e.printStackTrace()
			exceptionResponse( new PierApiException(message:e.message) )
		}
	}
	
	@Transactional
	def update() {
		//lookup user
		//verify user has access to these settings
		try {
			if ( request.method!="POST" ) throw new HttpMethodNotAllowedException(message:"issue POST instead")
			settingsService.updatePreference( request.JSON )
			respond FieldPreference.get(request.JSON.id.toLong())
		} catch (PierApiException e) {
			println "pier exception"
			exceptionResponse(e)
		} catch (Exception e) {
			println "reg exception"
			e.printStackTrace()
			exceptionResponse( new PierApiException(message:e.message) )
		}
	}
	
	@Transactional
	def saveQuery() {
		//lookup user
		//verify user has access to these settings
		try {
			if ( request.method!="GET" ) throw new HttpMethodNotAllowedException(message:"issue POST instead")
			respond settingsService.saveQuery( params.id )
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
