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
		def sr = new SearchRegistration( postBody ).save(failOnError:true)
		return sr
	}
	
	def logQueryAndResponse( postBody, elasticResponse ) {
		def json = elasticResponse.json
		def sr = SearchRegistration.get(postBody["registration.id"].toLong())
		def q = new Query(postBody)
		q.registration = sr
		q.query = postBody.query
		q.httpStatus = elasticResponse.status
		q.hits = json.hits.total
		q.took = json.took
		q.timedOut = json.timed_out
		q.label = Query.createLabel(q)
		q.save(failOnError:true)
		//sr.addToQueries(q).save(failOnError:true)
		return q
	}
	
	def logException( postBody, elasticResponse, Exception e ) {
		def sr = SearchRegistration.get(postBody["searchRegistration.id"].toLong())
		def q = new Query(postBody)
		q.registration = sr
		q.query = postBody.query
		q.httpStatus = elasticResponse.status
		q.exceptionMessage = e.message
		q.save(failOnError:true)
		return q
	}
	
}