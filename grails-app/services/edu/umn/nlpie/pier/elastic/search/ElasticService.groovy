package edu.umn.nlpie.pier.elastic.search

import grails.converters.JSON
import grails.plugins.rest.client.RestBuilder
import grails.transaction.Transactional


@Transactional
class ElasticService {

    def search( url, elasticQuery ) {
		def rest = new RestBuilder()
		rest.post(url) { json elasticQuery.toString() }
		//think about how best to float an exception to the user
    }
}
