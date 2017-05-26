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
 * Collection of FieldPreference instances associated with a Type (CorpusType).
 * Organized by ontology, sorted by display order
 *
 */
@InheritConstructors
class QueryFilters {
	
	QueryFilters(CorpusType ct) {
		def type = Type.find("from Type as t where t.corpusType.id=? and environment=? and t.index.status=?", [ ct.id, Environment.current.name, 'Available' ])
		this.populate(type)
	}
	
	QueryFilters(String corpusTypeId) {
		def type = Type.find("from Type as t where t.corpusType.id=? and environment=? and t.index.status=?", [ corpusTypeId.toLong(), Environment.current.name, 'Available' ])
		this.populate(type)
	}
	
	def defaultFilters = [:]
	def userFilters = [:]
	
	def populate(type) {
		//def type = Type.find("from Type as t where t.corpusType.id=? and environment=? and t.index.status=?", [ 1.toLong(), Environment.current.name, 'Available' ])
		def preferences = FieldPreference.where{ field.type.id==type.id && applicationDefault==true }.list()
		//prefs.each {
			//println "${it.label} ${it.ontology.name}"
		//}
		def ontologies = preferences.collect{ it.ontology }.unique()
		ontologies.each { o ->
			def prefsByOntology = preferences.findAll{ it.ontology.id==o.id && it.displayAsFilter==true }
			defaultFilters.put(o.name, prefsByOntology)
		}
		
	}
	
}
