
package edu.umn.nlpie.pier.context

import edu.umn.nlpie.pier.api.CorpusMetadata
import edu.umn.nlpie.pier.ui.Corpus


abstract class AbstractPierContext {
	
	Long requestId
	String label
	String description
	String corpusName
	Boolean filteredContext
	Long contextFilterValue
	
	def getRequest() {
		Request.find(requestId)
	}
	
	String getLabel() {
		this.label.trim()
	}
	
	String getFilterValue() {
		this.filterValue.trim()
	}

	def corpus() {
		def corpus
		def corpora = Corpus.searchableCorpora
		corpora.each { c ->
			if ( c.name==this.corpusName ) {
				c.metadata = new CorpusMetadata(c)
				corpus = c
			}
		}
		corpus
	}

}
