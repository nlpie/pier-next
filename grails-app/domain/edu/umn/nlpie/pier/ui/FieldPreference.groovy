package edu.umn.nlpie.pier.ui

import edu.umn.nlpie.pier.elastic.Field
import edu.umn.nlpie.pier.springsecurity.User


class FieldPreference {

	static constraints = {
		field()
		//field(unique:'user')
		user()
		label()
		queryable()
		displayOrder()
		displayAsFilter()
		/*ontology validator: { val, obj, errors ->
			if ( !(Ontology.findAllByIndex(obj.property.type.index).collect{it.id}.contains(val)) ) errors.rejectValue('ontology', 'Ontology must be associated with the parent corpus of the field being edited')
		}*/
		ontology( nullable:true )
		numberOfFilterOptions()
		includeInExport()
		applicationDefault validator: { val, obj ->
				if ( (obj.user.username!="nlppier" && val=="on") || (obj.user.username=="nlppier" && val=="") ) return false
			}
		
	}
	
	static belongsTo = [ field:Field ]
	
	//{id: 1, key:'setting', field:'setting', label:'Setting', use:['query','alldata','nonotes'], 
	//	query:true, facetChoice:true, facet:true, facetSize:10, downloadChoice:true, download:true, ontology:'Epic' },
	
    //Field field
    User user
	String label
    boolean queryable = true
    Integer displayOrder = 10
    boolean displayAsFilter = true
	Ontology ontology
	Integer	numberOfFilterOptions = 10
	boolean includeInExport = false
	boolean applicationDefault = false
	
	Date dateCreated
	Date lastUpdated
	
	String toString() {
		if ( id==null ) return ""
		( id!=null && applicationDefault ) ? "${label} (DEFAULT)" : "${label} (${user.username})"
	}
	
}