package edu.umn.nlpie.pier.elastic.datatype

class DatetimeDataType extends DataType {

    /*static mapping = {
		
    }
	
	static constraints = {
    	
	}*/
	
	String type = "date"
	String format = "YYYY-MM-dd HH:mm"
	
	String toString() {
		"${type}, optional time"
	}
	
}