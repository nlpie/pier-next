package edu.umn.nlpie.pier.audit

import groovy.json.JsonSlurper

class SearchRegistration {

    static constraints = {
		
    }
	
	static mapping = {
    	//version false
		uuid column:'uuid', index:'uuid_idx'
	}
	
	static hasMany = [ "queries":Query ]
	
	//TODO add override/inherit constructor that takes request.JSON, looks up user, tests for necesseary JSON properties and throws exceptions. this will simplify the controllers/services
	
	String username = "objdefault"
	String authorizedContext
	String uuid = UUID.randomUUID().toString()

	Date dateCreated
	Date lastUpdated

}