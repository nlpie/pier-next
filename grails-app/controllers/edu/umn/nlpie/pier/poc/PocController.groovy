package edu.umn.nlpie.pier.poc

import edu.umn.nlpie.pier.context.AuthorizedContext

class PocController {

    def index() { 
		println AuthorizedContext.count()
		AuthorizedContext.findAllByUserIdIsNotNull(sort:"label").each {
			println "${it.label} clinical ${it.username} ${it.hasClinicalNotes()}"
			println "${it.label} microbio ${it.hasMicrobiologyNotes()}"
			println "---"
			//println "${it.label} \t\t\t\t\t\t\t ${ ( (it.hasClinicalNotes()==it.hasMicrobiologyNotes()) && it.hasMicrobiologyNotes() ) }"
		}
	}
}
