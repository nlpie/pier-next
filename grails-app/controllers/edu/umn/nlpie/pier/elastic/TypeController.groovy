package edu.umn.nlpie.pier.elastic

import grails.plugin.springsecurity.annotation.Secured


@Secured(["ROLE_SUPERADMIN"])
class TypeController {

    static scaffold = true
	//def index() { }
	
	
}
