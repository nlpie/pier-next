package edu.umn.nlpie.pier.audit


class DistinctCount {

    static mapping = {
		//table name:'`distinct_count`'
		terms sqlType: "text"
		query sqlType: "mediumtext"
	}
	
	static constraints = {
    	countType inList:[ 'BUCKET', 'CARDINALITY', 'SCROLL' ]	
		bucketCount nullable:true
		scrollCount nullable:true
		cardinalityEstimate nullable:true	
	}
	
	static hasMany = [ buckets:Bucket, scrollValues:ScrollValue ]
	
	//passed from client
	String countType
	String query	//body of request sent to elastic
	String terms
	String label
	String uuid
	
	//derived properties
	Integer bucketCount
	Integer scrollCount
	
	//returned from elastic
	Integer hits
	Integer cardinalityEstimate
	Integer took
	Integer httpStatus
	Boolean timedOut
	
	//collections
	//ArrayList scrollValues = Collections.synchronizedList( new ArrayList() )
	
	Date dateCreated
	Date lastUpdated
	
	Integer getCount() {
		( scrollCount ) ?: bucketCount
	}
	
	String toString() {
		"${label} (${(bucketCount) ? bucketCount : cardinalityEstimate})"
	}
	
}