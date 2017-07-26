/**
 * 
 */
package edu.umn.nlpie.pier.api

import java.util.Collection;

import edu.umn.nlpie.pier.elastic.Field
import edu.umn.nlpie.pier.elastic.Type
import edu.umn.nlpie.pier.ui.CorpusType
import grails.util.Environment
import groovy.transform.InheritConstructors


@InheritConstructors
class CorpusMetadata {
	
	CorpusMetadata(CorpusType ct) {
		//this constructor causes issues when type is not found
		def type = Type.find("from Type as t where t.corpusType.id=? and environment=? and t.index.status=?", [ ct.id, Environment.current.name, 'Available' ])
		this.searchable = true
		this.url = "${type.index.cluster.uri}/${type.index.indexName}/${type.typeName}/_search"
		this.defaultSearchField = type.searchableField
		this.contextFilterField = type.contextFilterField
		this.tooltip = "Includes ${ct.name}"
	}
	
	Boolean searchable 
	String url 
	String defaultSearchField 
	String contextFilterField
	String tooltip
	Map aggregations = [:]	//placeholder to be filled by subsequent, client-side initiated request; alias for default or userConfigured filter set
	Map appliedFilters = [:]	//placeholder for user-specified filters to be used in a search of this corpus
	
	static transients = [ 'aggregations', 'appliedFilters' ]
	
}
