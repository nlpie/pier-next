package edu.umn.nlpie.pier.ui

import edu.umn.nlpie.pier.elastic.Type
import grails.util.Environment

/**
 * 
 * @author (rmcewan) 
 * 
 * CorpusType qualifies the Type class
 * Instances of the class represent the types of notes/text supported by NLP-PIER
 *
 */
class CorpusType {

    static mapping = { }
	
	static constraints = {
    	name unique:true
		description()
		enabled()
		glyph nullable:true
		
	}
	
	static transients = [ 'searchable', 'type', 'url', 'defaultSearchField' ]
	
	String name
	String description
	Boolean enabled = false
	String glyph
	
	//transients
	//TODO: move these to their own Groovy value object?
	Boolean searchable
	String url
	String defaultSearchField
	
	Type type

	Date dateCreated
	Date lastUpdated
	
	String toString() {
		name
	}
	
}