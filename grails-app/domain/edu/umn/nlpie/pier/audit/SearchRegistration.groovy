package edu.umn.nlpie.pier.audit

import groovy.json.JsonSlurper

class SearchRegistration {

    static constraints = {
		//abbr nullable:true
    }
	
	static mapping = {
    	version false
		uuid column:'uuid', index:'uuid_idx'
		//abbreviation column:'`abbreviation`'
	}
	
	static hasMany = [ "queries":Query ]
	//static transients = [ 'label' ]
	
	//TODO add override/inherit constructor that takes request.JSON, looks up user, tests for necesseary JSON properties and throws exceptions. this will simplify the controllers/services
	
	String username = "objdefault"
	String authorizedContext
	String uuid = UUID.randomUUID().toString()
	
	String label = 'label'

	Date dateCreated
	Date lastUpdated
	
	/*def getLabel() {
		if ( !queries ) return "n/a"
    	def q = Query.findByUuidAndType( this.uuid,"document" )
		if ( !q || !q.query ) return "n/a"
		def slurper = new JsonSlurper()
		def json = slurper.parseText(q.query)
		def a = new StringBuffer()
		a << json.query.query.bool.must.query_string.query
		println "abbr: ${a}"
		return a
	}*/
}
