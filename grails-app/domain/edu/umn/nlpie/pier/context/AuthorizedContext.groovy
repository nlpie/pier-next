package edu.umn.nlpie.pier.context

import grails.converters.JSON

import javax.annotation.PostConstruct


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
	
	def hasClinicalNotes() {
		def count = RequestSet.countByRequestIdAndIsNoteSetAndStatus(requestId,true,'Completed')
		return (count==1) ? true : false
	}
	
	def hasMicrobiologyNotes() {
		def has = RequestSet.countByRequestIdAndIsMicrobiologySetAndStatus(requestId,true,'Completed')
		if ( label.startsWith("Melton-MeauxG-Req00277") ) return true
		return (count==1) ? true : false
	}
}
