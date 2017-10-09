package edu.umn.nlpie.pier.audit

class ScrollValue {

    static mapping = {
		//key column:'`key`', index:'scroll_key_index'
		value sqlType: 'MEDIUMTEXT'
	}
	
	static constraints = {
		//value unique: 'distinctCount'
	}
	
	static belongsTo = [ distinctCount:DistinctCount ]
	
	//String key
	String value
	DistinctCount distinctCount
	
}