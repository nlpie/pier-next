package edu.umn.nlpie.pier.elastic.search

import edu.umn.nlpie.pier.audit.Query
import grails.transaction.Transactional
import groovy.json.JsonSlurper


@Transactional
class AuditService {
	
	def userService
	static scope = "prototype"
	
	def logQueryAndResponse( request, postBody, elasticResponse ) {
		//println postBody.toString()
		def json = elasticResponse.json
		def q = new Query(postBody)
		q.username = userService.currentUserUsername
		
		q.query = postBody.query
//println postBody.expansionTerms
		q.expansionTerms = postBody.expansionTerms
		q.hashCodedQuery = postBody.query.query.bool.toString().hashCode()	//q.query.hashCode()
		q.terms = postBody.query.query.bool.must.query_string.query[0]
		
		q.httpStatus = elasticResponse.status
		q.hits = json.hits.total
		q.took = json.took
		q.timedOut = json.timed_out
		q.label = Query.createLabel(q)
		//q.filters = this.filterSummary( postBody )
		q.session = request.userPrincipal.details.sessionId
		q.save(failOnError:true)
		return q
	}
		
	def logException( request, postBody, elasticResponse, Exception e ) {
		def q = new Query(postBody)
		q.query = postBody.query
		//q.filters = this.filterSummary( postBody )
		q.terms = postBody.query.query.bool.must.query_string.query
		q.httpStatus = elasticResponse?.status?:501
		q.exceptionMessage = e.message
		q.session = request.userPrincipal.details.sessionId
		q.save(failOnError:true)
		return q
	}
	
}