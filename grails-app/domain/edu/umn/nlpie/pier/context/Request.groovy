package edu.umn.nlpie.pier.context

import grails.converters.JSON


class Request {

    static constraints = {
		icsRequest()
		status()
		createdAt()
		updatedAt()
    }
	
	static mapping = {
		datasource 'notes'
		table name: "ics_note_request_pier_next", schema: "notes"
		version false
	}
	
	String icsRequest
	String description
	String noteSet
	Long requestSetId
	Boolean configureUsers
	String status
	Date startDate
	Date endDate
	Date createdAt
	Date updatedAt
	
	
	
	def getEpicNoteIds() {
		Request.executeQuery( "select rn.epicNoteId from RequestNote rn where rn.requestSetId=?", [requestSetId], [fetchSize:1000] )	//returns ArrayList
	}
	
	
	
}
