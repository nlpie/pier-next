package edu.umn.nlpie.pier.context

import java.util.regex.Pattern.Ctype;

import edu.umn.nlpie.pier.elastic.Type
import edu.umn.nlpie.pier.ui.CorpusType
import grails.util.Environment


class AuthorizedContext {

    static constraints = {
		requestId()
		label()
		filterValue()
		username()
		description()
    }
	
	static mapping = {
		datasource 'notes'
		table name: "authorized_context_by_user", schema: "notes"	//view in notes schema
		version false
	}
	
    Long requestId
	String label
	String filterValue
	String username
	Long userId
	String description
	
	def getRequest() {
		Request.find(requestId)
	}
	
	String getLabel() {
		this.label.trim()
	}
	String getFilterValue() {
		this.filterValue.trim()
	}
	
	/*Index searchableClinicalNotesIndex() {
		def count = RequestSet.countByRequestIdAndIsNoteSetAndStatus(requestId,true,'Completed')
		//return (count==1) ? true : false
		return (count==1) ? Index.currentClinicalNotesIndex() : null
	}
	
	Index searchableMicrobiologyNotesIndex() {
		def count = RequestSet.countByRequestIdAndIsMicrobiologySetAndStatus(requestId,true,'Completed')
		if ( label.startsWith("Melton-MeauxG-Req00277") ) count=1
		//return (count==1) ? true : false
		return (count==1) ? Index.currentMicrobiologyNotesIndex() : null
	}*/
	
	/**
	 * 
	 * @return List of annotated CorpusType instances. Annotations are transient fields: a flag indicating the CorpusType instance is searchable for this user and an Elastic Type instance from the environment specific cluster configuration detailing cluster, index, and type details for the CorpusType instance
	 */
	def annotatedCorpusTypes() {
		def corpusTypes = CorpusType.findAllByEnabled(true)
		corpusTypes.each { ct ->
			//SearchContext.findAllByCorpusTypeAndStatusAndRequestId(ct.name,"Completed",this.requestId).each { ct.searchable=true }
			def type = Type.find("from Type as t where t.corpusType.id=? and environment=? and t.index.status=?", [ ct.id, Environment.current.name, 'Available' ])
			//t.type = ty
			//Type.where { corpusType.id in ( CorpusType.findAllByEnabled(true).collect{it.id} ) && environment==env }.list()
			if ( type ) {
				println "found ${type.typeName}"
				ct.searchable = true
				//ct.type = type
				ct.url = "${type.index.cluster.uri}/${type.index.indexName}/${type.typeName}/_search"
				ct.defaultSearchField = type.searchableField
			} else { 
				ct.searchable = false
			}
		}
		//Type.where { corpusType.id in ( CorpusType.findAllByEnabled(true).collect{it.id} ) && environment==Environment.current.name }.list()
		corpusTypes
	}

}
