package edu.umn.nlpie.pier.api

import grails.converters.JSON
import grails.rest.RestfulController
import grails.plugins.rest.client.RestBuilder

class SearchController {//extends RestfulController {

	def elasticService

    def index() { }
	
	def search() { }
	
	def d3() { }
	
	def json() {
		params.each { println it }
	}
	
	def elastic() { 
		//lookup user
		//verify user has access to index
		//execute query
		def url = request.JSON.url
		def query = request.JSON.elasticQuery
		def rest = new RestBuilder()
		println query.toString(2)
		def esResponse = rest.post(url) { json query.toString() }
		//println esResponse.json.toString(2)
		render(status: 200, text:esResponse.json, contentType: "application/json") as JSON
	}
	
}
