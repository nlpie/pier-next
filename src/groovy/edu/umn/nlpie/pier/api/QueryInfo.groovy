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
class QueryInfo {
	
	QueryInfo(CorpusType ct) {
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
	
}