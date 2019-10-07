package edu.umn.nlpie.pier.cdr

class NoteMeta implements Serializable {

    static constraints = {
    }
	
	static mapping = {
		datasource 'notes'
		table name: "DT_NOTE_META", schema: "RDC_DT"	//table of clinical note metadata in RDC_DT schema
		version false
		id composite:['noteMetaId']
	}
	
	String noteMetaId
	Long noteId
	Long noteVersionId
	String status
	String indexingStatus
	Integer noteLength
	Long serviceId
	Date serviceDate
}
