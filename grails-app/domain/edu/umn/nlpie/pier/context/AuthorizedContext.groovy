package edu.umn.nlpie.pier.context

import edu.umn.nlpie.pier.api.CorpusMetadata;
import edu.umn.nlpie.pier.ui.Corpus


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
	String filterValue	//filter value used in ES for note sets
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
	 * @return List of annotated Corpus instances. Annotations are transient fields: a flag indicating the Corpus instance 
	 * is searchable for this user and an Elastic Type instance from the environment specific cluster configuration detailing cluster, 
	 * index, and type details for the Corpus instance
	 */
	def annotatedCorpora() {  
		def corpora = Corpus.searchableCorpora
		corpora.each { ct ->
			//check for document sets associated with this auth context and corpus
			def availableSearchContext = SearchContext.findByCorpusAndStatusAndRequestId(ct.name,"Completed",this.requestId)
			if ( availableSearchContext ) {	
				ct.metadata = new CorpusMetadata(ct)
			} else { 
				ct.metadata = new CorpusMetadata( searchable:false, tooltip:"Excludes ${ct.name}" )
			}
			//address case of whole corpus contexts like 'Clinical Notes' and 'Surgical Pathology Reports'
			if ( this.label==ct.name ) {
				println "THIS GOT CALLED"	
				ct.metadata = new CorpusMetadata(ct)
				ct.metadata.filtered = false
			}
				
		}
		corpora
	}

}
