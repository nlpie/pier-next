package edu.umn.nlpie.pier.api

import edu.umn.nlpie.pier.api.exception.HttpMethodNotAllowedException
import edu.umn.nlpie.pier.api.exception.PierApiException
import edu.umn.nlpie.pier.ui.Corpus
import edu.umn.nlpie.pier.ui.FieldPreference
import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional

//@Secured(["ROLE_USER"])
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
	
	def corpora() {
		try {
			if ( request.method!="GET" ) throw new HttpMethodNotAllowedException(message:"issue GET instead")
			respond Corpus.searchableCorpora.sort { it.name }
		} catch (PierApiException e) {
			println "pier exception"
			exceptionResponse(e)
		} catch (Exception e) {
			println "reg exception"
			e.printStackTrace()
			exceptionResponse( new PierApiException(message:e.message) )
		}
	}
	
	def corpusPreferences() {
		println "corpus prefs ${new Date()}"
		try {
			if ( request.method!="GET" ) throw new HttpMethodNotAllowedException(message:"issue GET instead")
			def corpusId = params.id
			def m = settingsService.corpusPreferences( corpusId )
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
			def ca = settingsService.corpusAggregations( corpusId )
			JSON.use("fieldpreference") {
				respond ca
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
	
	def corpusExports() {
		try {
			if ( request.method!="GET" ) throw new HttpMethodNotAllowedException(message:"issue GET instead")
			def corpusId = params.id
			respond settingsService.corpusExports( corpusId )
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
			def uuid = params.id
			respond settingsService.saveQuery( uuid )
		} catch (PierApiException e) {
			println "pier exception"
			exceptionResponse(e)
		} catch (Exception e) {
			println "some exception"
			e.printStackTrace()
			exceptionResponse( new PierApiException(message:e.message) )
		}
	}
	
}
