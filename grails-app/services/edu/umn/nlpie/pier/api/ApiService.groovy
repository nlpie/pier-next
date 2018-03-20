package edu.umn.nlpie.pier.api

import grails.transaction.Transactional

@Transactional
class ApiService {

    def grailsApplication
	
	def app() {
		grailsApplication.config.each {
			println it
		}
    }
	
	def requestedUri(r) {
		def port = ( r.serverPort!=80 || r.serverPort!=443 ) ? ":${r.serverPort}" : ""
		"${r.scheme}://${r.serverName}${port}${r.forwardURI}"
	}
}
