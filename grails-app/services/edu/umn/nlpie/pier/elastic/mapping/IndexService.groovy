package edu.umn.nlpie.pier.elastic.mapping

import grails.transaction.Transactional

import grails.plugins.rest.client.RestBuilder

@Transactional
class IndexService {

    def createAdminConfigurationFromIndexMapping(cluster,index) {
		def rest = new RestBuilder()
		def esResponse
		esResponse = rest.get("http://${cluster}:9200/${index}")
		println esResponse.json
		
    }
}
