package edu.umn.nlpie.pier.audit



class DistinctCount {

    static mapping = {
		//table name:'`distinct_count`'
	}
	
	static constraints = {
    	countType inList:[ 'bucket', 'cardinality' ]
	}
	
	static belongsTo = [ "registration":SearchRegistration ]
	static hasMany = [ "buckets":Bucket ]
	
	//passed from client
	String countType = "bucket"
	String query	//body of request sent to elastic
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