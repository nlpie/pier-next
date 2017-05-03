package edu.umn.nlpie.pier.api

import grails.converters.JSON
import grails.rest.RestfulController

class SearchController {//extends RestfulController {
	
	//static scaffold=true
	def elasticService

    def index() { }
	
	def search() { }
	
	def d3() { }
	
	def json() {
		params.each { println it }
	}
	
	def execute() { 
		//lookup user
		//verify user has access to index
		//execute query
		def index = params.id
		println request.method
		println index
		println request.JSON
		def map = ["good":"job"]
		render(status: 200, text: '{"good":"job"}', contentType: "application/json") as JSON
	}
	
}
