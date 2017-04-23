package edu.umn.nlpie.pier.ui

import java.util.Date;


class SemanticRelatednessSearch {

    static mapping = { }
	
	static constraints = {
    	name unique:true
		description()
	}
	
	String name
	String description
	
	Date dateCreated
	Date lastUpdated
	
	String toString() {
		name
	}
	
}