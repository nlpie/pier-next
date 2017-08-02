package edu.umn.nlpie.pier.audit

class Query {

    static constraints = {
		cohort nullable:true
		cohortSize nullable:true
		hits nullable:true
		took nullable:true
    }
	
	static mapping = {
		query type: 'text'
		version false
	}
	
	static belongsTo = [ searchRegistration:SearchRegistration ]	//client passes in queryLog.id
	
	//TODO add override/inherit constructor that takes request.JSON, looks up user, tests for necesseary JSON properties and throws exceptions. this will simplify the controllers/services
	
	//passed from client
	String url
	String type 
	String query
	
	//returned from elastic
	Integer hits
	Integer took
	
	//async derived values
	String cohort
	Integer cohortSize
	
	Date dateCreated
	Date lastUpdated
}
