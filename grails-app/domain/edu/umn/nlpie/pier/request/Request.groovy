package edu.umn.nlpie.pier.request

import grails.util.Environment

class Request {

    static constraints = {
		icsRequest()
		status()
		createdAt()
		updatedAt()
    }
	
	static mapping = {
		datasource 'notes'
		table name: "ics_note_request", schema: "notes"
		version false
	}
	
	String icsRequest
	String description
	String setName
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
	
	/*def getRequestNoteCount() {
		return RequestNote.countByIcsRequest(icsRequest)
	}*/
	
}
