package edu.umn.nlpie.pier.elastic

import grails.converters.JSON


class Cluster {

    static mapping = {
		table "`cluster`"
    }
	
	static constraints = {
    	clusterName unique:'uri'
    	uri url:true
		description()
		environment inList:['DEV','TEST','In PROD'], nullable:true
	}
	
    String clusterName
	String uri
	String commonName
	String description
	String environment = "TEST"
	
	Date dateCreated
	Date lastUpdated
	
	static hasMany = [ indexes:Index ]
	
	@Override
	String toString() {
		commonName ?: ""
	}
	
}

//Cluster
	//Index
		//Type types
			//Property properties (Fields fields)