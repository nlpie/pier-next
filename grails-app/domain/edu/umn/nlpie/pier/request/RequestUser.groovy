package edu.umn.nlpie.pier.request

import grails.util.Environment

class RequestUser {

    static constraints = {
		icsRequest()
		username()
		firstName()
		lastName()
		email()
		createdAt()
		updatedAt()
    }
	
	static mapping = {
		datasource 'notes'
		table name: "ics_note_request_user", schema: "notes"	//view in notes schema
		version false
	}
	
	String userId
	String username
	String firstName
	String lastName
	String email
	Long requestId
	String icsRequest
	Long requestSetId
	String setName
	Date createdAt
	Date updatedAt
	
	String getFullName() {
		"${firstName} ${lastName}"
	}
	
	def getRequest() {
		Request.find(requestId)
	}
}
