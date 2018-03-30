package edu.umn.nlpie.pier.elastic

import edu.umn.nlpie.pier.ui.Corpus
import grails.util.Environment


class Index {
	
	static mapping = {
		table "`index`"
    }
	
	static constraints = {
    	indexName unique:'cluster'
		commonName()
		description()
		environment inList: ['DEVELOPMENT', 'TEST', 'PRODUCTION', 'DEPRECATED']
		status inList:['Available', 'Unavailable', 'In Progress'], nullable:false
		alias (nullable:true)
	}
	
    Cluster cluster
	String indexName
	String alias
	String commonName
	String description
	String status = "In Progress"
	Integer numberOfShards
	Integer numberOfReplicas
	String environment
	
	Date dateCreated
	Date lastUpdated
	
	static belongsTo = [ cluster:Cluster ]
	static hasOne = [ type:Type ]
	
	@Override
	String toString() {
		commonName ?: ""
	}
	
	void setType(type){
		this.type = type
		type.index = this
	}
	
	static getAvailableIndexes() {
		def env = Environment.current.toString()	//eg, PRODUCTION
		Index.findAllByEnvironmentAndStatus(env,'Available')
	}
	
}

//Index
	//Type type
		//Property properties