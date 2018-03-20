package edu.umn.nlpie.pier.api

import edu.umn.nlpie.pier.api.exception.HttpMethodNotAllowedException
import edu.umn.nlpie.pier.api.exception.PierApiException
import edu.umn.nlpie.pier.elastic.Index
import edu.umn.nlpie.pier.springsecurity.User
import edu.umn.nlpie.pier.ui.FieldPreference
import edu.umn.nlpie.pier.ui.Ontology
import grails.converters.JSON
import grails.util.Environment

class SettingsController {//extends RestfulController {
	
	static responseFormats = ['json']
	
	//TODO refactor to superclass
	private exceptionResponse(Exception e) {
		def msg = e.message.replace('\n',' ')	//\n causes problems when client parses returned JSON
		respond ( e, status:e.status )
		//render(status: e.status, text: '{"message":"'+ msg +'"}', contentType: "application/json") as JSON
	}
	
	def index() {}
	
	def poc() {
		//lookup user
		//verify user has access to these settings
		try {
			if ( request.method!="GET" ) throw new HttpMethodNotAllowedException(message:"issue GET instead")
			def user = User.findByUsername("rmcewan")
			def indexes = Index.findAllByStatus("Available")
			def m = [:]
			indexes.each { index ->
				def ontologies = Ontology.list()
				def o = [:]
				ontologies.each { ontology ->
					o.put( ontology.name, null )
					def fieldPreferences = FieldPreference.executeQuery(
						'from FieldPreference fp where user=? and fp.field.type.index=? and fp.ontology=? and field.aggregatable=? order by fp.label', 
						[ user,index,ontology,true ], [ readOnly:true ]
					)
					def p = [:]
					fieldPreferences.each { fp ->
						p.put( fp.label, fp)
					}
					o[ontology.name] = p
				}
				m.put( index.commonName, o)
			}
			//respond m as JSON
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
	
	def update() {
		//lookup user
		//verify user has access to these settings
		try {
			if ( request.method!="POST" ) throw new HttpMethodNotAllowedException(message:"issue POST instead")
			
		} catch (PierApiException e) {
			println "pier exception"
			exceptionResponse(e)
		} catch (Exception e) {
			println "reg exception"
			e.printStackTrace()
			exceptionResponse( new PierApiException(message:e.message) )
		}
	}
	
	def env() {
		def env = Environment.current
		//env.properties.each { println "${it}: ${it.getClass().getName()}" }
		//println env.current.toString()
		//respond Environment.current.toString()
		def prefs = FieldPreference.executeQuery(
			'select fp from FieldPreference fp where user=? and fp.field.type.index=?', [ index,user ]
		)
		println prefs
	}
	
}
