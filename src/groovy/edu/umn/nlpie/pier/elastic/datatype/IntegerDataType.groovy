package edu.umn.nlpie.pier.elastic.datatype

class IntegerDataType extends DataType {

    /*static mapping = {
		
    }
	
	static constraints = {
    	
	}*/
	
	String type = "integer"
	
	String toString() {
		"${type}"
	}
	
}