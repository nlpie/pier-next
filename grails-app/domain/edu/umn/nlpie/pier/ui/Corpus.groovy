package edu.umn.nlpie.pier.ui

import edu.umn.nlpie.pier.api.CorpusMetadata
import edu.umn.nlpie.pier.elastic.Index
import grails.util.Environment
import groovy.transform.InheritConstructors

/**
 * @author (rmcewan) 
 * 
 * Corpus qualifies the Type class
 * Instances of the class represent the types of notes/text supported by NLP-PIER
 *
 */
@InheritConstructors
class Corpus {
	
    static mapping = { }
	
	static constraints = {
    	name unique:true
		description()
		enabled()
		glyph nullable:true
		
	}
	
	static transients = [ 'metadata' ]
	static hasMany = [ indexes:Index ]
	
	String name
	String description
	Boolean enabled = false
	String glyph
	
	//transients
	CorpusMetadata metadata	

	Date dateCreated
	Date lastUpdated
	
	String toString() {
		name
	}
	
	//returns the index for this env - assumption is that there is only 1 index per Corpus in the current environment
	//strictly speaking, the domain model permits more than one index
	//TODO - put a constraint on the system to enforce only one index per env and Corpus, maybe refactor logic of this method
	def getIndex() {
		/*def corpora = Corpus.availableCorpora	//this will return all enabled corpora for this env
		def thisCorpus = corpora.find { it.id==this.id }	
		thisCorpus.indexes[0]*/
		this.indexes[0]
	}
	
	/**
	 *
	 * @return string name of field containing analyzed, searchable, text for this type
	 * It is assumed there is only one searchable field per type [as of May 2017]
	 */
	String getSearchableField() {
		def field = fields.find { it.defaultSearchField==true }
		(field.fieldName)?:null
	}
	
	String getContextFilterField() {
		def field = fields.find { it.contextFilterField==true }
		(field.fieldName)?:null
	}
	
	String getCuiField() {
		//similar to searchable field, look into putting cui field default on Field class
	}
	
	//TODO is this method used?
	static getAvailableCorpora() {
		def env = Environment.current.toString()	// != Environment.PRODUCTION
		def corps = Corpus.executeQuery(
			"select ct from Corpus ct join ct.indexes i where ct.enabled=? and i.environment=? and i.status=? order by ct.name desc", [ true, env, 'Available']
		)
		corps.each { c ->
			def indexes = c.indexes
			println "corpus: ${c}, indexes:${indexes.size()}"
		}
		corps
	}
	
}