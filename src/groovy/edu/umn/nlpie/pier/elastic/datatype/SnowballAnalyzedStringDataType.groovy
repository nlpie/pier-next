package edu.umn.nlpie.pier.elastic.datatype

class SnowballAnalyzedStringDataType extends DataType {

    /*static mapping = {
		
    }
	
	static constraints = {
    	
	}*/
	
	String type = "string"
	String analyzer = "snowball"
	
	String toString() {
		"${type}, ${analyzer}"
	}
	
}