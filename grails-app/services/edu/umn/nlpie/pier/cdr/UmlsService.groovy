package edu.umn.nlpie.pier.cdr

import grails.transaction.Transactional

@Transactional
class UmlsService {
	

    def umlsString( cui ) {
		//Umls.findByCuiAndTsAndIspref( cui.substring(0,8), 'P', 'Y'  )
		//Umls.findByCuiAndTs( cui.substring(0,8), 'P'  )
		Umls.findByCui( cui.substring(0,8)  )
    }
}
