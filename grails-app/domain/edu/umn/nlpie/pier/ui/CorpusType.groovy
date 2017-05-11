package edu.umn.nlpie.pier.ui

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
	}
	
	String name
	String description
	
	Date dateCreated
	Date lastUpdated
	
	String toString() {
		name
	}
	
}