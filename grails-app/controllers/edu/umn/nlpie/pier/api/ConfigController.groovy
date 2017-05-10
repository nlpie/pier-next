package edu.umn.nlpie.pier.api

import edu.umn.nlpie.pier.PierUtils
import edu.umn.nlpie.pier.ConfigService
import edu.umn.nlpie.pier.api.exception.*
import edu.umn.nlpie.pier.elastic.Cluster
import edu.umn.nlpie.pier.elastic.Index
import edu.umn.nlpie.pier.springsecurity.User
import grails.converters.JSON

class ConfigController {
    
	ConfigService configService
	
	static responseFormats = ['json']
	//static allowedMethods = [get: "GET", "find": "POST"]
		
	
	
	def settings() { 
		def json = configService.defaultPreferences as JSON
		json.prettyPrint = true
		json.render response
		//respond configService.defaultUiDataElementSettings
	}
	
	def clone() {
		configService.clonePreferences(User.findByUsername("rmcewan"))
	}
	
	def data() {
		configService.data()
		configService.clonePreferences(User.findByUsername("rmcewan"))
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
	
	def authorizedContexts() {
		//with spring security in place can restrict the set of requests based on user allowed to invoke this method
		def contexts = configService.authorizedContexts
		JSON.use ('authorized.context') {
			respond contexts
		}
	}
	
	def find() {
		//curl -XPOST -H "Content-Type: application/json" -d '{ "shape" : "POLYGON((-106.69 39.2,-106.77 40.8,-106.54 41.46,-106.08 41.99,-105.45 42.3,-104.75 42.35,-104.08 42.12,-103.55 41.66,-103.24 41.03,-103.11 39.07,-103.34 38.41,-103.8 37.88,-104.78 37.51,-105.8 37.75,-106.51 38.51,-106.69 39.2))" }' http://localhost:8080/api/guide/find
		//path query reutrns guide.id, guide.title, guide.shape, guide.publisher, abstract, audience 
		//indiv guide request returns the whole guide; request body contains array of field guide ids 
		try {
			if ( request.method!="POST" ) throw new HttpMethodNotAllowedException(error:"${request.method} request not allowed, issue POST instead")
			def p = request.JSON
			if ( !p ) throw new BadRequestException(error:"empty POST request body")
			if ( !p.shape ) throw new BadRequestException(error:"shape attribute missing in POST request body")
			def shape = p.shape
			println "Guide: ${shape}"
						
			def results = fieldGuideService.guidesByGeometry(shape)
			println "Guide spatial matches: ${results.size()}"
				
			JSON.use ('FieldGuideLite') {
				respond results
			}	
		} catch (PierApiException e) {
			render(status: e.status, text: '{"message":"'+ e.message +'"}') as JSON
		} catch (Exception e) {
			def apiEx = new PierApiException(error:e.message)
			render(status: apiEx.status, text: '{"message":"'+ apiEx.message +'"}') as JSON
		}
	}
	
	def all() {
		try {
			if ( request.method!="GET" ) throw new HttpMethodNotAllowedException(error:"${request.method} method not allowed, issue GET instead")
			JSON.use ('FieldGuideLite') {
				respond fieldGuideService.allGuides()
			}
		} catch (PierApiException e) {
			render(status: e.status, text: '{"message":"'+ e.message +'"}') as JSON
		} catch (Exception e) {
			def apiEx = new PierApiException(error:e.message)
			render(status: apiEx.status, text: '{"message":"'+ apiEx.message +'"}') as JSON
		} 
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