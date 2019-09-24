package edu.umn.nlpie.pier.api

import edu.umn.nlpie.pier.api.exception.HttpMethodNotAllowedException
import edu.umn.nlpie.pier.api.exception.PierApiException
import grails.converters.JSON


class AuthCheckController {
	
	static responseFormats = ['json']
	
	def userService
	
	def index() {
		//println "auth check"
		//respond new Exception("bad joojoo"), 500
		def respObj = new GenericRestResponseObject()
		if ( userService.loggedIn() ) {
			//respond new Exception("bad"), controller:'search', view:'index'
			respObj.message = "Logged in"
			respond ( respObj, status:200 )
		} else {
			respObj.message = "Expired authentication"
			respond ( respObj, status:419 )
		}
	}
	
}
