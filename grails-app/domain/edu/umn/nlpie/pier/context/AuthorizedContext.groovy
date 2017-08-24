package edu.umn.nlpie.pier.context

import edu.umn.nlpie.pier.api.CorpusMetadata;
import edu.umn.nlpie.pier.ui.CorpusType


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

	
	/**
	 * 
	 * @return List of annotated CorpusType instances. Annotations are transient fields: a flag indicating the CorpusType instance 
	 * is searchable for this user and an Elastic Type instance from the environment specific cluster configuration detailing cluster, 
	 * index, and type details for the CorpusType instance
	 */
	def annotatedCorpusTypes() {
		//TODO restore this  
		def corpusTypes = CorpusType.findAllByEnabled(true)
		//def corpusTypes = CorpusType.findAllByEnabledAndName(true,'Surgical Pathology Reports')
		corpusTypes.each { ct ->
			//check for corpus document sets associated with this auth context
			def availableSearchContext = SearchContext.findByCorpusTypeAndStatusAndRequestId(ct.name,"Completed",this.requestId)
			if ( availableSearchContext ) {	
				ct.metadata = new CorpusMetadata(ct)
			} else { 
				ct.metadata = new CorpusMetadata( searchable:false, tooltip:"Excludes ${ct.name}" )
			}
			//address case of whole corpus contexts like 'Clinical Notes' and 'Surgical Pathology Reports'
			if ( this.label==ct.name ) {	
				ct.metadata = new CorpusMetadata(ct)
				ct.metadata.filtered = false
			}
				
		}
		corpusTypes
	}

}
