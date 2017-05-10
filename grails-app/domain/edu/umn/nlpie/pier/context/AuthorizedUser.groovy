package edu.umn.nlpie.pier.context

import grails.util.Environment

class AuthorizedUser {

    static constraints = {
		userId()
		username()
		firstName()
		lastName()
		email()
    }
	
	static mapping = {
		datasource 'notes'
		table name: "authorized_user", schema: "notes"	//view in notes schema
		version false
	}
	
    Long userId
	String username
	String firstName
	String lastName
	String email
	
	String getFullName() {
		"${firstName} ${lastName}"
	}

}
