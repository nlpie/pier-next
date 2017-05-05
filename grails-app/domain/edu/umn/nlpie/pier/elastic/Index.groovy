package edu.umn.nlpie.pier.elastic

import org.codehaus.groovy.grails.web.converters.configuration.DefaultConverterConfiguration

import edu.umn.nlpie.pier.ui.ConceptualSearch
import edu.umn.nlpie.pier.ui.SemanticRelatednessSearch
import grails.converters.JSON


class Index {
	
	static mapping = {
		table "`index`"
    }
	
	static constraints = {
    	indexName unique:'cluster'
		commonName()
		description()
		status inList:['Searchable', 'Disabled', 'In Progress'], nullable:false
		corpusType inList:['clinic notes', 'pathology notes', 'microbiology notes', 'imaging notes'], nullable:false, unique:'status'
		alias (nullable:true)
		conceptualSearch (nullable:true)
		semanticRelatednessSearch (nullable:true)
	}
	
	static marshaller = {
		JSON.registerObjectMarshaller(Index) { i ->
			[ 
				"id": i.id,
				"name": i.indexName,
				"commonName": i.commonName,
				"alias": i.alias,
				"cluser": i.cluster
			]
		}
	}
	
    Cluster cluster
	String indexName
	String alias
	String commonName
	String description
	String status = "In Progess"
	String corpusType = "clinic notes"
	Integer numberOfShards
	Integer numberOfReplicas
	ConceptualSearch conceptualSearch
	SemanticRelatednessSearch semanticRelatednessSearch
	
	Date dateCreated
	Date lastUpdated
	
	static belongsTo = [ cluster:Cluster ]
	static hasMany = [ types:Type ]
	static hasOne = [ conceptualSearch:ConceptualSearch ]
	
	static searchableCorpora() {
		
	}
	static currentClinicNotesIndex() {
		Index.findByCorpusTypeAndStatus("clinical notes","Searchable")
	}
	static currentPathologyNotesIndex() {
		Index.findByCorpusTypeAndStatus("pathology notes","Searchable")
	}
	static currentMicrobiologyNotesIndex() {
		Index.findByCorpusTypeAndStatus("microbiology notes","Searchable")
	}
	static currentImagingNotesIndex() {
		Index.findByCorpusTypeAndStatus("imaging notes","Searchable")
	}
	
	@Override
	String toString() {
		commonName ?: ""
	}
	
}

//Index
	//Type types
		//Property properties