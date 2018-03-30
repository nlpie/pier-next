package edu.umn.nlpie.pier.elastic

import edu.umn.nlpie.pier.ui.Corpus



/**
 * 
 * @author (rmcewan) 
 * 
 * Type is the searchable unit in elastic.
 * An Index can contain multiple searchable types, i.e, searchable corpora.
 * An Index lives on a Cluster
 * 
 * elastic supports the /note,surgpath/note,surgpath/_search syntax implying: 
 * search types note and surgpath in the notes and surgpath indices (but they must be on the same cluster for this to work)
 * see https://www.elastic.co/guide/en/elasticsearch/guide/current/multi-index-multi-type.html
 * 
 */
class Type {

    static mapping = {
		
    }
	
	static constraints = {
    	typeName( unique:'index')
    	description(nullable:true)
	}
	
    String typeName
	String description
	
	/**
	 * Type.environment is the environment scope for that type (dev, test, prod). 
	 * A test scope can live on a prod cluster, e.g.
	 * Values map to grails.util.Environment.current.name
	 */
	
	Date dateCreated
	Date lastUpdated
	
	
	static hasMany = [ fields:Field ]
	static belongsTo = [ index:Index ]
	
	@Override
	String toString() {
		return typeName ?: ""
	}
	
	/**
	 * alias for fields property
	 * @return collection of associated Field objects
	 */
	def getProperties() {
		fields
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
	
}

//Index
	//Type types
		//Property properties
