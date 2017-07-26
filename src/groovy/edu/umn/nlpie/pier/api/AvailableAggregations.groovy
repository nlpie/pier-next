/**
 * 
 */
package edu.umn.nlpie.pier.api

import edu.umn.nlpie.pier.elastic.Type
import edu.umn.nlpie.pier.ui.CorpusType
import edu.umn.nlpie.pier.ui.FieldPreference
import grails.util.Environment
import groovy.transform.InheritConstructors

/**
 * 
 * @author ${name:git_config(user.name)} and ${email:git_config(user.email)} (rmcewan) 
 * Collection of user-specific FieldPreference instances associated with a Type (CorpusType)
 * Organized by ontology, sorted by display order. 
 * Scope of filter set is: 
 * 		default - application level default, 
 * 		user-configured - user-specified set of available  filters (usu subset of default set, or could be the same),
 * 		applied - user-selected filters for a specific search of a corpus
 * 
 */
@InheritConstructors
class AvailableAggregations {
	
	AvailableAggregations(CorpusType ct) {
		def type = Type.find("from Type as t where t.corpusType.id=? and environment=? and t.index.status=?", [ ct.id, Environment.current.name, 'Available' ])
		this.populate(type)
	}
	
	AvailableAggregations(String corpusTypeId) {
		def type = Type.find("from Type as t where t.corpusType.id=? and environment=? and t.index.status=?", [ corpusTypeId.toLong(), Environment.current.name, 'Available' ])
		this.populate(type)
		println this.aggregations.toString()
	}
	
	private defaultAggregations = [:]
	private userConfiguredAggregations = [:]
	def aggregations = [:]
	
	//TODO refactor to pull either default set or user-configured set if configured
	def populate(type) {
		//def type = Type.find("from Type as t where t.corpusType.id=? and environment=? and t.index.status=?", [ 1.toLong(), Environment.current.name, 'Available' ])
		def preferences = FieldPreference.where{ field.type.id==type.id && applicationDefault==true }.list()
		def ontologies = preferences.collect{ it.ontology }.unique()
		ontologies.each { o ->
			def prefsByOntology = preferences.findAll{ it.ontology.id==o.id && it.displayAsFilter==true }
			aggregations.put(o.name, prefsByOntology)
		}
		
	}
	
}
