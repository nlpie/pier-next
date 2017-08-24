package edu.umn.nlpie.pier.ui

import edu.umn.nlpie.pier.api.CorpusMetadata
import edu.umn.nlpie.pier.api.AvailableAggregations
import edu.umn.nlpie.pier.api.QueryInfo

/**
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
	
	static transients = [ 'metadata' ]
	
	String name
	String description
	Boolean enabled = false
	String glyph
	
	//transients
	//QueryInfo queryInfo
	//DocumentFilters documentFilters
	CorpusMetadata metadata

	Date dateCreated
	Date lastUpdated
	
	String toString() {
		name
	}
	
}