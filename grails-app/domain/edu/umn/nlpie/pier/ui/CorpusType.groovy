package edu.umn.nlpie.pier.ui

import edu.umn.nlpie.pier.QueryInfo
import edu.umn.nlpie.pier.elastic.Type

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
	
	static transients = ['queryInfo']
	
	String name
	String description
	Boolean enabled = false
	String glyph
	
	//transients
	QueryInfo queryInfo

	Date dateCreated
	Date lastUpdated
	
	String toString() {
		name
	}
	
}