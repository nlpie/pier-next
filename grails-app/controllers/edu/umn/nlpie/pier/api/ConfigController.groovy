package edu.umn.nlpie.pier.api

import edu.umn.nlpie.pier.ConfigService
import edu.umn.nlpie.pier.PierUtils
import edu.umn.nlpie.pier.api.exception.*
import edu.umn.nlpie.pier.elastic.Index
import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

@Secured(["ROLE_USER"])
class ConfigController {
    
	ConfigService configService
	def apiService
	
	static responseFormats = ['json']
	//static allowedMethods = [get: "GET", "find": "POST"]
		
	//keep
	def authorizedContexts() {
		def contexts = configService.authorizedContexts
		JSON.use ('authorized.context') {
			respond contexts
		}
	}
	
	def authorizedContextByLabel() {
		JSON.use ('authorized.context') {
			//println request.JSON
			respond configService.authorizedContextByLabel(request.JSON.label)
		}
	}
	
	//TODO refactor to superclass
	private renderException(Exception e) {
		render(status: e.status, text: '{"message":"'+ e.message +'"}', contentType: "application/json") as JSON
	}

	def mapping() {
		JSON.use ('index.mapping') {
			def json = Index.get(1) as JSON
			json.prettyPrint = true
			json.render response
		}
	}
	
	def reveng() {
		def index = Index.findByIndexName('notes_v2')
		println index.cluster
		configService.reverseEngineerDomainInstancesFromIndexMapping(index)
	}
	
	def under() {
		PierUtils.toSnakeCase("CamelCaseToUnderscore")
		PierUtils.underscoreToCamelCase("underscore_to_camel_case")
		PierUtils.labelFromUnderscore("label_from_underscore")
	}

	def index() {
		def s = """
				<pre>
				
				
				NLP-PIER API Usage
				
				Response Types
				Unsupported HTTP methods (anything but GET or POST) return a 405 response and a message.
				Invalid GET or POST requests return a 400 response and a JSON message detailing the reason.
				Server side errors return a 500 response and an error message, though it's likely cryptic. Best to contact the developer.
				Valid GET requests for which nothing is found return a 404 response and simple message stating nothing was found.
				Valid POST requests for which no data are found return a 200 response and an empty JSON array/object.
				???? Requests matching one or more guides return a 200 response with a JSON array containing the matching guide(s), abbreviated or complete as appropriate.
				
				
				Valid Examples
				
				Return array of all abbreviated guides:
				curl -XGET ${apiService.requestedUri(request)}/all
				Not very useful after first set of guides; consider creating guide groups instead
				
				</pre>
				
				"""
				render s
	}
}