package edu.umn.nlpie.pier.audit

import groovy.json.JsonSlurper

class Query {

    static constraints = {
		hits nullable:true
		took nullable:true
		exceptionMessage nullable:true
		httpStatus nullable:true
		timedOut nullable:true
		label nullable:true
		hashCodedQuery nullable:true
    }
	
	static mapping = {
		query sqlType: "mediumtext"
		exceptionMessage type: 'text'
		terms sqlType: 'text'
		//version false
	}
	
	static belongsTo = [ registration:SearchRegistration ]	//client passes in queryLog.id
	//TODO add override/inherit constructor that takes request.JSON, looks up user, tests for necesseary JSON properties and throws exceptions. this will simplify the controllers/services
	
	//passed from client
	String url
	String corpus
	String type
	String query	//body of request sent to elastic
	Integer hashCodedQuery
	String terms
	
	//returned from elastic
	Integer hits
	Integer took
	Integer httpStatus
	Boolean timedOut
	
	//possible exception
	String exceptionMessage
	
	//[async] derived values
	String label
	
	Date dateCreated
	Date lastUpdated
	
	static String createLabel (Query q) {
		def trunc = 40
		def json = new JsonSlurper().parseText(q.query)
		def s = new StringBuffer()
		def userInput = json.query.bool.must.query_string.query[0]	//nodelist - only 1, so just get it
	//println "userinput: ${userInput}"
		if ( userInput.length()>trunc ) {
			userInput = userInput.substring(0, trunc)
			s << userInput << "..."
		} else {
			s << userInput
		}
		return s
	}
}
