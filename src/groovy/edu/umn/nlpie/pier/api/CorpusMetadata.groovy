/**
 * 
 */
package edu.umn.nlpie.pier.api

import edu.umn.nlpie.pier.ui.Corpus
import groovy.transform.InheritConstructors


@InheritConstructors
class CorpusMetadata {
	
	CorpusMetadata(Corpus ct) {
		//this constructor causes issues when type is not found
		//println "looking for Type with corpus.id:${ct.id} env:${Environment.current.name} status:Searchable "
		def index = ct.index
		this.searchable = true
		this.filtered = true	//most of the time corpus will be assoc with a restricted/filtered search/auth context
		this.url = "${index.cluster.uri}/${index.indexName}/${index.type.typeName}/_search"
		this.scrollUrl = "${index.cluster.uri}/_search/scroll"
		this.defaultSearchField = index.type.searchableField
		this.contextFilterField = index.type.contextFilterField
		this.tooltip = "Includes ${ct.name}"
	}
	
	Boolean searchable 
	Boolean filtered
	String url 
	String scrollUrl
	String defaultSearchField 
	String contextFilterField
	String tooltip
	
}
