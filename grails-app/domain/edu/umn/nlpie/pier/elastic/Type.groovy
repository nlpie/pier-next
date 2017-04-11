package edu.umn.nlpie.pier.elastic

import java.util.Date;

import edu.umn.nlpie.pier.ui.Ontology


class Type {

    static mapping = {
		
    }
	
	static constraints = {
    	typeName( unique:'index')
    	description(nullable:true)
	}
	
    String typeName
	String description
	
	Date dateCreated
	Date lastUpdated
	
	
	static hasMany = [ fields:Field ]
	static belongsTo = [ index:Index ]
	
	@Override
	String toString() {
		return typeName ?: ""
	}
	
	def getProperties() {
		fields
	}
	
}

//Index
	//Type types
		//Property properties