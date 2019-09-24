package edu.umn.nlpie.pier.elastic

import grails.plugin.springsecurity.annotation.Secured

@Secured(["ROLE_SUPERADMIN"])
class IndexController {

	def indexService 
	
	def p() {
		indexService.createAdminConfigurationFromIndexMapping("nlp05.ahc.umn.edu","notes_v2")
	}
	
    static scaffold = true
	//def index() { }
	
}
