package edu.umn.nlpie.pier.elastic.datatype

class LongDataType extends DataType {

    /*static mapping = {
		
    }
	
	static constraints = {
    	
	}*/
	
	String type = "long"
	
	String toString() {
		"${type}"
	}
	
}