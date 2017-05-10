package edu.umn.nlpie.pier.context


class SearchContextClinicalNote {

    static constraints = {

    }
	
	static mapping = {
		datasource 'notes'
		table name: "search_context_clinical_note", schema: "notes"
		version false
	}
	
	Long requestSetIdsId
	Long requestId
	Long requestSetId
	String requestName
	String noteId
	Long noteIdAsNumber
	
}
