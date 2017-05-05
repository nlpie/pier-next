package edu.umn.nlpie.pier.request


class RequestSet {

    static constraints = {
		id()
		requestId()
		isNoteSet()
		isMicrobiologySet()
		status()
    }
	
	static mapping = {
		datasource 'notes'
		table name: "request_sets", schema: "metadata"	//view in notes schema
		isMicrobiologySet column: "is_patient_set"
		version false
	}
	
    Long id
	Long requestId
	boolean isNoteSet
    boolean isMicrobiologySet
	String status
	
	//Date createdAt
	//Date updatedAt

}
