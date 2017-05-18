package edu.umn.nlpie.pier.context


class SearchContext {

    static constraints = {

    }
	
	static mapping = {
		datasource 'notes'
		table name: "search_context", schema: "notes"
		version false
	}
	
	Long requestSetId
	Long requestId
	String requestName
	//Boolean isNoteSet
	//Boolean isMicrobiologySet
	String description
	String status
	String corpusType
	
}
