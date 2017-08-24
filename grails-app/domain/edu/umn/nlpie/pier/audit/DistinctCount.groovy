package edu.umn.nlpie.pier.audit


class DistinctCount {

    static mapping = {
		//table name:'`distinct_count`'
		query sqlType: "mediumtext"
	}
	
	static constraints = {
    	countType inList:[ 'bucket', 'cardinality' ]		
	}
	
	static hasMany = [ buckets:Bucket ]
	static belongsTo = [ registration:SearchRegistration ]
	
	//passed from client
	String countType = "bucket"
	String query	//body of request sent to elastic
	String terms
	String label
	
	//derived properties
	Integer size
	
	//returned from elastic
	Integer hits
	Integer took
	Integer httpStatus
	Boolean timedOut
	
	Date dateCreated
	Date lastUpdated
	
	String toString() {
		"${label} (${size})"
	}
	
}