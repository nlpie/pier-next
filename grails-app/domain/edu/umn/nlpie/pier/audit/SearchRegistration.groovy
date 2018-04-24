package edu.umn.nlpie.pier.audit

class SearchRegistration {

    static constraints = {

    }
	
	static mapping = {
    	version false
		uuid column:'uuid', index:'uuid_idx'
	}
	
	static hasMany = [ queries:Query, counts:DistinctCount ]
	
	//TODO add override/inherit constructor that takes request.JSON, looks up user, tests for necesseary JSON properties and throws exceptions. this will simplify the controllers/services
	
	String username = "objdefault"
	String authorizedContext
	String searchType
	String initialUserInput
	String uuid = UUID.randomUUID().toString()

	
	Date dateCreated
	Date lastUpdated
	
	def getDocsQuery() {
		Query.findBySearchRegistrationAndType(this,"DocumentQuery")
	}
	def getAggsQuery() {
		Query.findBySearchRegistrationAndType(this,"AggregateQuery")
	}
	def getCountQueries() {
		DistinctCount.findAllBySearchRegistrationInList(this)
	}
	def getCountQueries( List registrationList ) {
		DistinctCount.findAllBySearchRegistrationInList(this)
	}

}