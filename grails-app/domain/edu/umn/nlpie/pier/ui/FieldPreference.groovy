package edu.umn.nlpie.pier.ui

import edu.umn.nlpie.pier.elastic.Field
import edu.umn.nlpie.pier.springsecurity.User
import grails.util.Environment

//@Resource(uri='/fieldPreference', formats=['json'])
class FieldPreference {

	static constraints = {
		field( unique:'user' )
		user()
		label()
		displayOrder()
		aggregate()
		export()
		ontology( nullable:true )
		numberOfFilterOptions()
		applicationDefault validator: { val, obj ->
				if ( (obj.user.username!="nlppier" && val=="on") || (obj.user.username=="nlppier" && val=="") ) return false
			}
		
	}
	
	static belongsTo = [ field:Field ]
	
    User user
	String label
    Integer displayOrder = 10			//p
    Boolean aggregate = true			//p
	Boolean computeDistinct = false		//ui
	Ontology ontology
	Integer	numberOfFilterOptions = 5	//p
	Boolean export = false		//p
	Boolean applicationDefault = false
	
	Date dateCreated
	Date lastUpdated
	
	String toString() {
		if ( id==null ) return ""
		"${label}"
	}
	
	//TODO - this method still necessary?
	/*static List preferencesByIndexAndUser( index, user ) {
		def grailsEnv = Environment.current.toString()	// != Environment.PRODUCTION
		def prefs = FieldPreference.executeQuery(
						'select fp from FieldPreference fp where user=? and fp.field.type.index=?', [ index,user ]
					)
		
	}*/
	
}