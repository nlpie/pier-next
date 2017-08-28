package edu.umn.nlpie.pier.audit


class DistinctCount {

    static mapping = {
		//table name:'`distinct_count`'
		query sqlType: "mediumtext"
	}
	
	static constraints = {
    	countType inList:[ 'bucket', 'cardinality', 'combined' ]	
		bucketCount nullable:true
		cardinalityEstimate nullable:true	
	}
	
	static hasMany = [ buckets:Bucket ]
	static belongsTo = [ registration:SearchRegistration ]
	
	//passed from client
	String countType = "combined"
	String query	//body of request sent to elastic
	String terms
	String label
	
	//derived properties
	Integer bucketCount
	
	//returned from elastic
	Integer hits
	Integer cardinalityEstimate
	Integer took
	Integer httpStatus
	Boolean timedOut
	
	Date dateCreated
	Date lastUpdated
	
	String toString() {
		"${label} (${(bucketCount) ? bucketCount : cardinalityEstimate})"
	}
	
}