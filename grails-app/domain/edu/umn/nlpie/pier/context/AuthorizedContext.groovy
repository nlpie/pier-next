package edu.umn.nlpie.pier.context

import edu.umn.nlpie.pier.elastic.Index


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
	
	String getLabel() {
		this.label.trim()
	}
	String getFilterValue() {
		this.filterValue.trim()
	}
	
	Index searchableClinicalNotesIndex() {
		def count = RequestSet.countByRequestIdAndIsNoteSetAndStatus(requestId,true,'Completed')
		//return (count==1) ? true : false
		return (count==1) ? Index.currentClinicalNotesIndex() : null
	}
	
	Index searchableMicrobiologyNotesIndex() {
		def count = RequestSet.countByRequestIdAndIsMicrobiologySetAndStatus(requestId,true,'Completed')
		if ( label.startsWith("Melton-MeauxG-Req00277") ) count=1
		//return (count==1) ? true : false
		return (count==1) ? Index.currentMicrobiologyNotesIndex() : null
	}
	
	def searchableIndexes() {
		Index.findAllByStatus("Searchable",[sort:"commonName"])
	}
}
