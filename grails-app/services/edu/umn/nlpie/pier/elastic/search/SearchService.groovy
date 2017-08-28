package edu.umn.nlpie.pier.elastic.search

import edu.umn.nlpie.pier.api.HistorySummaryDTO
import edu.umn.nlpie.pier.audit.Bucket
import edu.umn.nlpie.pier.audit.DistinctCount
import edu.umn.nlpie.pier.audit.Query
import edu.umn.nlpie.pier.audit.SearchRegistration


//@Transactional
class SearchService {

    def saveSearch( ) {
		//get user from spring security
    }
	
	def searchHistory( excludeMostRecent ) {
		def sql = new StringBuffer()
		sql << "select distinct max(q.registration.id), q.label, q.registration.authorizedContext from Query q where q.registration.username=? and q.httpStatus=? and q.type=? and q.registration.id not in ( ? ) group by q.hashCodedQuery, q.registration.authorizedContext, q.hashCodedQuery order by max(q.registration.id) desc "

		def registrationId = 0.toLong()
		
		if ( excludeMostRecent ) {
			registrationId = SearchRegistration.createCriteria().get {
				eq ("username", "nouserservice.user")	//TODO change to authenticated user from userService
				projections {
					max "id"
				}
			} as Long
		}
		
		def results = Query.executeQuery(sql.toString(), ["nouserservice.user", 200, "document", registrationId] )
		def summaries = []
		results.each {
			summaries << new HistorySummaryDTO(it)
		}
		summaries
		//lookup user from userService
	}
	
	def registeredSearch(id) {
		SearchRegistration.get(id.toLong())
	}
	
	def logDistinctCountInfo( postBody, elasticResponse ) {
		def json = elasticResponse.json
		//println postBody.toString(2)
		def sr = SearchRegistration.get(postBody["registration.id"].toLong())
		def c = new DistinctCount(registration:sr)
		c.query = postBody.query
		c.terms = postBody.query.query.bool.must.query_string.query
		c.httpStatus = elasticResponse.status
		c.hits = json.hits.total
		c.took = json.took
		c.timedOut = json.timed_out
		c.label = postBody.label
		c.countType = postBody.countType
		def cardinalityLabel = postBody.label + " Cardinality Estimate"
		c.cardinalityEstimate = json.aggregations[cardinalityLabel].value
		println "CE: ${c.cardinalityEstimate}"
		def buckets = json.aggregations[postBody.label]?.buckets //may not have this type of aggregation due to large hit count
		c.bucketCount = (buckets) ?  buckets.size() : -1
		if ( buckets ) {
			buckets.each { 
				c.addToBuckets( new Bucket( key:it.key, keyAsString:it.key_as_string, docCount:it.doc_count ) )
			}
		}
		c.save(failOnError:true)
		sr.save(failOnError:true)
		return c
	}
	
}