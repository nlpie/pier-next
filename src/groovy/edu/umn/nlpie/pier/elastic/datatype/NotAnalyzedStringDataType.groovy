package edu.umn.nlpie.pier.elastic.datatype

class NotAnalyzedStringDataType extends DataType {

    /*static mapping = {
		
    }
	
	static constraints = {
    	
	}*/
	
	String type = "string"
	String index = "not_analyzed"
	
	String toString() {
		"${type}, ${index}"
	}
	
}