package edu.umn.nlpie.pier.request


class AuthorizedContext {

    static constraints = {
		requestId()
		label()
		filterValue()
		username()
		description()
    }
	
	static mapping = {
		datasource 'notes'
		table name: "authorized_context_by_user", schema: "notes"	//view in notes schema
		version false
	}
	
    Long requestId
	String label
	String filterValue
	String username
	Long userId
	String description
	
	def getRequest() {
		Request.find(requestId)
	}
	
	def hasClinicalNotes() {
		def count = RequestSet.countByRequestIdAndStatus(requestId,'Completed')
		return (count==1) ? true : false
	}
	
	def hasMicrobiologyNotes() {
		def has = RequestSet.countByRequestIdAndStatus(requestId,'Completed')
		if ( label.startsWith("Melton-MeauxG-Req00277") ) return true
		return (count==1) ? true : false
	}
}
