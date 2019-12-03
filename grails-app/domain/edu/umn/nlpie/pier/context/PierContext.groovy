package edu.umn.nlpie.pier.context

class PierContext {

    static constraints = {
    }
	
	static mapping = {
		datasource 'notes'
		table name: "pier_context", schema: "notes"	//view in notes schema
		corpusName column: 'corpus'
		contextFilterValue column: 'filter_value'
		contextFilterExpr column: 'filter_expr'
		version false
	}
	
	/*--request_set_id (needed to construct table name of notes to go after
	 --request_id
	 --label (info)
	 --filter_value
	 --filter_expr
	 --corpus
	 --note_status*/
 
	Long requestSetId
	Long requestId
	String label
	String description
	String corpusName
	Long contextFilterValue
	String contextFilterExpr
	String noteStatus
 
	def getRequest() {
		Request.find(requestId)
	}
 
	String getLabel() {
		this.label.trim()
	}
 
	String getContextFilterValue() {
		this.contextFilterValue.trim()
	}
}
