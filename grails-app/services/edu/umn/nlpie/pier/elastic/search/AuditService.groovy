package edu.umn.nlpie.pier.elastic.search

import edu.umn.nlpie.pier.audit.Query
import edu.umn.nlpie.pier.audit.SearchRegistration
import grails.transaction.Transactional


@Transactional
class AuditService {
	
	//TODO def userService
	static scope = "prototype"
	
	def register( postBody ) {
		def user
		try {
			user = userService.find(username)
		} catch (Exception e) {
			postBody.username = "nouserservice.user"
		}
		def ql = new SearchRegistration( postBody ).save(failOnError:true)
		return ql
	}
	
	def record( postBody ) {
		def ql = SearchRegistration.get(postBody["queryLog.id"].toLong())
		def q = new Query(postBody)
		q.query = postBody.query
		//println "query: ${q.query}"
		ql.addToQueries(q).save(failOnError:true)
		return q
	}
	
	def update( query,elasticResponse ) {
		def q = Query.get( query.id.toLong() )
		println q.toString()
		q.hits = elasticResponse.hits.total
		q.took = elasticResponse.took
		println "${q.id} ${q.type} ${q.took}"
		q.searchRegistration.save(failOnError:true)
		//q.timedOut = elasticResponse.timed_out
	}
}