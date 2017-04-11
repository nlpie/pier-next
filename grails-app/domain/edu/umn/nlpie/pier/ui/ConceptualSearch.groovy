package edu.umn.nlpie.pier.ui

import edu.umn.nlpie.pier.elastic.Field
import edu.umn.nlpie.pier.elastic.Index


class ConceptualSearch {

    static mapping = { }
	
	static constraints = {
    	name unique:true
		fieldContainingCuis validator: { val, obj, errors ->
			if ( !(Field.findAllByTypeInList(obj.index.types).collect{it.id}.contains(val)) ) errors.rejectValue('index', 'field is not associated with the chosen index ')
		}
		description()
	}
	
	//static belongsTo = [ index:Index ]
	
	String name
	Index index
	Field fieldContainingCuis
	String description
	
	Date dateCreated
	Date lastUpdated
	
	String toString() {
		name
	}
	
}