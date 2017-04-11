package edu.umn.nlpie.pier.elastic.datatype

class DateDataType extends DataType {

    /*static mapping = {
		
    }
	
	static constraints = {
    	
	}*/
	
	String type = "date"
	String format = "YYYY-MM-dd"
	
	String toString() {
		"${type}, ${format}"
	}
	
}