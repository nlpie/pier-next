package edu.umn.nlpie.pier.ui

import edu.umn.nlpie.pier.api.QueryFilters
import edu.umn.nlpie.pier.api.QueryInfo

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
	
	static transients = [ 'queryInfo', 'queryFilters' ]
	
	String name
	String description
	Boolean enabled = false
	String glyph
	
	//transients
	QueryInfo queryInfo
	QueryFilters queryFilters

	Date dateCreated
	Date lastUpdated
	
	String toString() {
		name
	}
	
}