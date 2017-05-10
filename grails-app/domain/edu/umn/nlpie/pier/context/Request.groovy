package edu.umn.nlpie.pier.context

import grails.converters.JSON


class Request {

    static constraints = {
		icsRequest()
		status()
		createdAt()
		updatedAt()
    }
	
	static marshaller = {
		JSON.registerObjectMarshaller(Request) { r ->
			def parties = [
		            ["id":12 , "name":"Ar", "privateField": "a"],
		            ["id":9 , "name":"Sr", "privateField": "b"]
		    ]
		    def toRender = parties.collect { party->
		        ["partyId": party.id, "partyName":party.name]
		    }
			def result = ["partyTo" : toRender]
			
			type.fields.each { f ->
				propertiesMap.put(f.fieldName, f.dataType)
			}
			[ 
				"label": r.icsRequest,
				"filterName": r.icsRequest,
				"description": r.description,
				"searchableCorpora": Request.searchableCorpora(r.icsRequest) 
			]
		}
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
