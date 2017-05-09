package edu.umn.nlpie.pier.marshaller

import javax.annotation.PostConstruct

import edu.umn.nlpie.pier.request.AuthorizedContext
import grails.converters.JSON

class JsonMarshallerRegistrar {
	
	@PostConstruct
	void registerMarshallers() {
		JSON.registerObjectMarshaller(AuthorizedContext) { c ->
			[
				//"id": c.id,
				"requestId": c.requestId,
				"label": c.label,
				"filterValue": c.filterValue,
				"description": c.description?:"description unavailable",
				"username": c.username,
				"hasClinicalNotes": c.hasClinicalNotes(),
				"hasMicrobilogyNotes": c.hasMicrobiologyNotes()
			]
		}
		println "registered AuthorizedContext json marshaller"
	}

}
