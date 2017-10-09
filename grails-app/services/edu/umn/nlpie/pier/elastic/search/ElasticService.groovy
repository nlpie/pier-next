package edu.umn.nlpie.pier.elastic.search

import edu.umn.nlpie.pier.api.ScrollPayload
import grails.plugins.rest.client.RestBuilder
import grails.transaction.Transactional
import groovy.json.JsonOutput


@Transactional
class ElasticService {

    def search( url, elasticQuery ) {
		def rest = new RestBuilder()
		rest.post(url) { json elasticQuery.toString() }
		//think about how best to float an exception to the user
    }
	
	def scroll( scrollUrl, ScrollPayload sp ) {
		def rest = new RestBuilder()
		rest.post( scrollUrl ) { json JsonOutput.toJson(sp) }
	}
}
