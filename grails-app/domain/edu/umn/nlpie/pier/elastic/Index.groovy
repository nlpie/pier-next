package edu.umn.nlpie.pier.elastic

import edu.umn.nlpie.pier.ui.ConceptualSearch
import edu.umn.nlpie.pier.ui.SemanticRelatednessSearch


class Index {

    static mapping = {
		table "`index`"
    }
	
	static constraints = {
    	indexName unique:'cluster'
		commonName()
		description()
		status inList:['Searchable','Disabled','In Progress'], nullable:true
		alias (nullable:true)
		conceptualSearch (nullable:true)
		semanticRelatednessSearch (nullable:true)
	}
	
    Cluster cluster
	String indexName
	String alias
	String commonName
	String description
	String status = "In Progess"
	Integer numberOfShards
	Integer numberOfReplicas
	ConceptualSearch conceptualSearch
	SemanticRelatednessSearch semanticRelatednessSearch
	
	Date dateCreated
	Date lastUpdated
	
	static belongsTo = [ cluster:Cluster ]
	static hasMany = [ types:Type ]
	static hasOne = [ conceptualSearch:ConceptualSearch ]
	
	@Override
	String toString() {
		commonName ?: ""
	}
	
}

//Index
	//Type types
		//Property properties