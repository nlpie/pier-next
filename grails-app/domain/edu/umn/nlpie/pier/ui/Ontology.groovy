package edu.umn.nlpie.pier.ui

class Ontology {

    static mapping = { }
	
	static constraints = {
    	name unique:true
		description()
	}
	
	static transients = [ 'fieldPreferences' ]
	
	String name
	String description
	
	Date dateCreated
	Date lastUpdated
	
	ArrayList fieldPreferences = new ArrayList()	//compostion varies according to context in which it is called/populated, but is generally user- and index-specific

	String toString() {
		name
	}
	
	
}