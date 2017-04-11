package edu.umn.nlpie.pier.request

import grails.util.Environment

class RequestNote {

    static constraints = {

    }
	
	static mapping = {
		datasource 'notes'
		table name: "ics_note_request_note", schema: "notes"
		//epicNoteId column: "string_value_id"
		//epicNoteIdAsNumber column: "number_value_id"
		version false
	}
	
	Long requestId
	String icsRequest
	Long requestSetId
	String setName
	String epicNoteId
	Long epicNoteIdAsNumber
	Date createdAt
	Date updatedAt
	
}
