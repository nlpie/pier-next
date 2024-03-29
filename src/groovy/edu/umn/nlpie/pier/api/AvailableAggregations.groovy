/**
 * 
 */
package edu.umn.nlpie.pier.api

import edu.umn.nlpie.pier.elastic.Type
import edu.umn.nlpie.pier.ui.Corpus
import edu.umn.nlpie.pier.ui.FieldPreference
import grails.util.Environment
import groovy.transform.InheritConstructors

/**
 * 
 * @author ${name:git_config(user.name)} and ${email:git_config(user.email)} (rmcewan) 
 * Collection of user-specific FieldPreference instances associated with a Type (Corpus)
 * Organized by ontology, sorted by display order. 
 * Scope of filter set is: 
 * 		default - application level default, 
 * 		user-configured - user-specified set of available  filters (usu subset of default set, or could be the same),
 * 		applied - user-selected filters for a specific search of a corpus
 * 
 */
@InheritConstructors
@Deprecated
class AvailableAggregations {
//TODO is this class used? looks like no
	
	AvailableAggregations(Corpus ct) {
		def type = Type.find("from Type as t where t.corpus.id=? and environment=? and t.index.status=?", [ ct.id, Environment.current.name, 'Searchable' ])
		this.populate(type)
	}
	AvailableAggregations(String corpusId) {
		def type = Type.find("from Type as t where t.corpus.id=? and environment=? and t.index.status=?", [ corpusId.toLong(), Environment.current.name, 'Searchable' ])
		this.populate(type)
		//println "AGGS: ${this.aggregations}"
	}
	
	private defaultAggregations = [:]
	private userConfiguredAggregations = [:]
	def aggregations = [:]
	
	//TODO refactor to pull either default set or user-configured set if configured
	def populate(type) {
		//def type = Type.find("from Type as t where t.corpus.id=? and environment=? and t.index.status=?", [ 1.toLong(), Environment.current.name, 'Searchable' ])
		def preferences = FieldPreference.where{ field.type.id==type.id && applicationDefault==true }.list()
		def ontologies = preferences.collect{ it.ontology }.sort { it.name }.unique()
		ontologies.each { o ->
			def prefsByOntology = preferences.findAll{ it.ontology.id==o.id && it.aggregate==true }
			if ( prefsByOntology.size>0 ) {
				aggregations.put(o.name, prefsByOntology)
			}
		}
		
	}
	
}
