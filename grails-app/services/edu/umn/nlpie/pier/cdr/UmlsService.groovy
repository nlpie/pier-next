package edu.umn.nlpie.pier.cdr

import grails.transaction.Transactional

@Transactional
class UmlsService {
	
	def elasticService

    def umlsStringFromDb( cui ) {
		//Umls.findByCuiAndTsAndIspref( cui.substring(0,8), 'P', 'Y'  )
		//Umls.findByCuiAndTs( cui.substring(0,8), 'P'  )
		Umls.findByCui( cui.substring(0,8)  )
    }
	
	def umlsStringFromIndex( cui ) {
		elasticService.cui( cui.substring(0,8) )
	}
}
