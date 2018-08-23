package edu.umn.nlpie.pier.elastic

import edu.umn.nlpie.pier.ui.Corpus
import grails.util.Environment


class Index {
	
	static mapping = {
		table "`index`"
    }
	
	static constraints = {
    	indexName unique:'cluster'
		description()
		environment inList: ['DEVELOPMENT', 'TEST', 'PRODUCTION', 'DEPRECATED','CUSTOM','development','test','production','fvdev','fvtest']
		status inList:['Searchable', 'Unavailable', 'In Progress', 'Functional'], nullable:false
		isTermExpansionIndex validator: { val, obj, errors ->
			if ( val && obj.status!='Functional' ) {
				errors.rejectValue('isTermExpansionIndex', val.toString(), "Status must be set to 'Functional' for an expansion index")
			}
		}
		isConceptLookupIndex validator: { val, obj, errors ->
			if ( val && obj.status!='Functional' ) {
				errors.rejectValue('isConceptLookupIndex', val.toString(), "Status must be set to 'Functional' for a concept lookup index")
			}
		}
		//commonName()
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
	Boolean isTermExpansionIndex = false
	Boolean isConceptLookupIndex = false
	
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
	
	static getSearchableIndexes() {
		def env = Environment.current.name.toString()	//eg, production, fvdev
		Index.findAllByEnvironmentAndStatus(env,'Searchable')
	}
	
	def beforeValidate() {
		if ( isTermExpansionIndex && isConceptLookupIndex ) {
			errors.rejectValue('isTermExpansionIndex', isTermExpansionIndex.toString(), "Index cannot be both an expansion AND a concept lookup index")
			return
		}
		if ( isTermExpansionIndex ) {
			def existingExpansionIndex = Index.findByEnvironmentAndStatusAndIsTermExpansionIndex(environment,'Functional',true)
			if ( existingExpansionIndex && existingExpansionIndex.id!=this.id ) {
				errors.rejectValue('isTermExpansionIndex', isTermExpansionIndex.toString(), "Environment [${environment}] already has a term expansion index, ${existingExpansionIndex.indexName}")
			}
			return
		}
		if ( isConceptLookupIndex ) {
			def index = Index.findByEnvironmentAndStatusAndIsConceptLookupIndex(environment,'Functional',true)
			if ( index && index.id!=this.id ) {
				errors.rejectValue('isConceptLookupIndex', isConceptLookupIndex.toString(), "Environment [${environment}] already has a concept lookup index, ${index.indexName}")
			}
			return
		}
	}
	
}

//Index
	//Type type
		//Property properties