package edu.umn.nlpie.pier.audit




class Bucket {

    static mapping = {
		//table name:'`bucket`'
		key column:'`key`', index:'key_index'
		keyAsString index:'key_string_index'
	}
	
	static constraints = {
		keyAsString nullable:true
		key nullable:true
	}
	
	static belongsTo = [ distinctCount:DistinctCount ]
	
	String keyAsString	
	String key
	Integer docCount
	
	/*String toString() {
		"${(keyAsString) ? docCount : key}"
	}*/
	
}