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
		//println "looking for Type with corpusType.id:${ct.id} env:${Environment.current.name} status:Available "
		def type = Type.find("from Type as t where t.corpusType.id=? and environment=? and t.index.status=?", [ ct.id, Environment.current.name, 'Available' ])
		this.searchable = true
		this.filtered = true	//most of the time corpus with be assoc with a restricted/filtered search/auth context
		this.url = "${type.index.cluster.uri}/${type.index.indexName}/${type.typeName}/_search"
		this.scrollUrl = "${type.index.cluster.uri}/_search/scroll"
		this.defaultSearchField = type.searchableField
		this.contextFilterField = type.contextFilterField
		this.tooltip = "Includes ${ct.name}"
	}
	
	Boolean searchable 
	Boolean filtered
	String url 
	String scrollUrl
	String defaultSearchField 
	String contextFilterField
	String tooltip
	Map aggregations = [:]	//placeholder to be filled by subsequent, client-side initiated request; alias for default or userConfigured filter set
	Map appliedFilters = [:]	//placeholder for user-specified filters to be used in a search of this corpus
	
	static transients = [ 'aggregations', 'appliedFilters' ]
	
}
