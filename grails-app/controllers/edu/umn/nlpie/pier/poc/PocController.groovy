package edu.umn.nlpie.pier.poc

import edu.umn.nlpie.pier.request.AuthorizedContext

class PocController {

    def index() { 
		AuthorizedContext.findAll().each {
			println "${it.label} clinical ${it.hasClinicalNotes()}"
			println "${it.label} microbio ${it.hasMicrobiologyNotes()}"
			println "---"
			//println "${it.label} \t\t\t\t\t\t\t ${ ( (it.hasClinicalNotes()==it.hasMicrobiologyNotes()) && it.hasMicrobiologyNotes() ) }"
		}
	}
}
