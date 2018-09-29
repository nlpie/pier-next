package edu.umn.nlpie.pier.audit

import java.util.Date;

class UserAuthenticationEvent {

    static constraints = {
    }
	
	static mapping = {
		eventInfo sqlType: "mediumtext"
	}
	
	String username
	String roles
	String remoteAddress
	String session
	String eventInfo
	
	Date dateCreated
	Date lastUpdated 
}
