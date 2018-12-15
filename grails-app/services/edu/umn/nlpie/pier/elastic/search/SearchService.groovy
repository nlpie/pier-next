package edu.umn.nlpie.pier.elastic.search

import edu.umn.nlpie.pier.api.HistorySummaryDTO
import edu.umn.nlpie.pier.api.ScrollPayload
import edu.umn.nlpie.pier.api.exception.BadElasticRequestException
import edu.umn.nlpie.pier.audit.DistinctCount
import edu.umn.nlpie.pier.audit.Query
import groovy.sql.Sql
import groovyx.gpars.GParsPool


//@Transactional
class SearchService {
	
	def elasticService
	def dataSource
	def userService
	
	def searchHistory( excludeMostRecent ) {
		def sql = new StringBuffer()
		def username = userService.currentUserUsername

		sql << """
select q.id as queryId, q.label, q.authorizedContext, q.id as queryId, q.filterSummary, q.expansionTerms, q.saved, q.uuid, q.userInput, q.inputExpansion, q.distinctCounts
from Query q
where q.username=? and q.httpStatus=? and q.type in ( 'DocumentQuery','EncounterQuery' )
order by q.dateCreated desc """.toString()

		def lastQueryId = 0.toLong()
		
		if ( excludeMostRecent ) {
			//def result = Query.executeQuery( "select max(q.id) as queryId from Query q where q.username=? and q.type=? ", [username,"DocumentQuery"] )
			//lastQueryId = result[0]
		}
		
		def results = Query.executeQuery(sql.toString(), [username, 200] )
		def summaries = []
		results.each {
			summaries << new HistorySummaryDTO(it)
		}
		summaries
		//lookup user from userService
	}
	
	def savedQueriesByContext( authorizedContext ) {
		def sql = new StringBuffer()
		sql << """
select q.id as queryId, q.label, q.authorizedContext, q.id as queryId, q.filterSummary, q.expansionTerms, q.saved, q.uuid, q.userInput, q.inputExpansion, q.distinctCounts
from Query q
where q.httpStatus=? and q.type in ( 'DocumentQuery','EncounterQuery' ) and q.saved=? and q.authorizedContext=?
order by q.label """.toString()
		
		def results = Query.executeQuery(sql.toString(), [200, true, authorizedContext] )
		def queries = []
		results.each {
			queries << new HistorySummaryDTO(it)
		}
		println "in context: ${queries.size()}"
		queries
	}
	
	def savedQueriesByUserExcludingContext( authorizedContext ) {
		def sql = new StringBuffer()
		def username = userService.currentUserUsername
		sql << """
select q.id as queryId, q.label, q.authorizedContext, q.id as queryId, q.filterSummary, q.expansionTerms, q.saved, q.uuid, q.userInput, q.inputExpansion, q.distinctCounts
from Query q
where q.username=? and q.httpStatus=? and q.type in ( 'DocumentQuery','EncounterQuery' ) and q.saved=? and q.authorizedContext not in (?)
order by q.authorizedContext, q.label """.toString()
		
		def results = Query.executeQuery(sql.toString(), [username, 200, true, authorizedContext] )
		def queries = []
		results.each {
			queries << new HistorySummaryDTO(it)
		}
		println "not in context: ${queries.size()}"
		queries
	}
	
	def recentQuery(id) {
		def d = Query.get( id.toLong() )
		def a = Query.findByUuidAndType(d.uuid,"AggregationQuery")
		[ "docsQuery":d, "aggsQuery":a]
	}
	
	def logBucketCountInfo( postBody, elasticResponse ) {
		def b = new Date().time
		def json = elasticResponse.json
		def c = new DistinctCount()
//println postBody.query.toString(2)
		c.query = postBody.query
		c.terms = postBody.query.query.bool.must.query_string.query
		c.httpStatus = elasticResponse.status
		c.hits = json.hits.total
		c.took = json.took
		c.timedOut = json.timed_out
		c.label = postBody.label
		c.countType = postBody.countType
		c.uuid = postBody.uuid
		def cardinalityLabel = postBody.label + " Cardinality Estimate"
		c.cardinalityEstimate = json.aggregations[cardinalityLabel].value
		def buckets = json.aggregations[postBody.label]?.buckets
		c.bucketCount = (buckets) ?  buckets.size() : 0
		c.save(failOnError:true,flush:true)
		println "CE: [${postBody.label}] ${c.id} ${c.cardinalityEstimate}"
		println "${postBody.label} ${buckets.size()} FINAL ${(new Date().time-b)/1000}"
		return c
	}
	
	def logScrollCountInfo( postBody, elasticResponse ) {
		
		//look at https://hackernoon.com/parallel-scan-scroll-an-elasticsearch-index-db02583d10d1
		
		def json = elasticResponse.json
		//passed elasticResponse has hits that need to be added to count and ScrollValue collection
		def c = new DistinctCount(registration:sr)
		c.query = postBody.query
		c.terms = postBody.query.query.bool.must.query_string.query
		c.httpStatus = elasticResponse.status
		c.hits = json.hits.total
		c.took = json.took
		c.timedOut = json.timed_out
		c.label = postBody.label
		c.countType = postBody.countType
		c.scrollCount = 0

		def cardinalityLabel = postBody.label + " Cardinality Estimate"
		c.cardinalityEstimate = json.aggregations[cardinalityLabel].value
		
		c.save(failOnError:true,flush:true)
		sr.save(failOnError:true,flush:true)
		def pkId = c.id
		println "CE: ${pkId} ${c.cardinalityEstimate}"
		
		def cumulativeUnique = Collections.synchronizedSet( new HashSet() )//Collections.synchronizedList( new ArrayList() )
		def b = new Date().time
		
		//add hits to set/count
		//optimization based on http://danielhalima.com/grails/2011/04/30/groovy-sql-e-batch-updates-microbenchmark/?lang=en
		def initialBatch = Collections.synchronizedSet( new HashSet() )
		GParsPool.withPool {
				json.hits.hits.each {
					def val = it.fields[postBody.query.fields][0]
					initialBatch << val
				}
		}
		cumulativeUnique.addAll( initialBatch )
		
		println " initial increment [${postBody.query.fields}] ${initialBatch.size()}"

		//iterate through scroll sets
		def scrollId = json._scroll_id
		def scroll = true
		while ( scroll ) {
			def scrollBatch = Collections.synchronizedSet( new HashSet() )
			def sp = new ScrollPayload( scroll_id:scrollId, scroll:"1m" )
			//println postBody.toString(2)
			def response = elasticService.scroll( postBody.scrollUrl, sp )
			def hits = response.json.hits.hits
			GParsPool.withPool {
					hits.eachParallel {
						def val = it.fields[postBody.query.fields][0]
						scrollBatch << val
					}
			}
			def batchIncrement = scrollBatch - ( cumulativeUnique.intersect(scrollBatch) )
			cumulativeUnique.addAll( batchIncrement )

			println " batch increment [${postBody.query.fields}] ${batchIncrement.size()}"
			if ( false ) { //( batchIncrement.size()>0 ) {
				def p = DistinctCount.async.task {
					def sql = new Sql(dataSource)
					String insertStmt = "insert into scroll_value ( value, distinct_count_id, version ) values ( :val, :fk, :ver )"
					withTransaction {
						sql.execute( insertStmt, [val:batchIncrement.join(" "), fk:pkId, ver:0] )
					}
					it.flush()
					sql.close()
				}
				p.onError { Throwable err ->
					println "An error occured ${err.message}"
				}
			}
			
			scroll = ( hits.size()>0 ? true : false )
			def status = elasticResponse.status
				
			if ( status==400 ) {
				throw new BadElasticRequestException( message:"Scroll iteration problem" )
			}
			scrollId = elasticResponse.json._scroll_id
			println "${postBody.query.fields} ${cumulativeUnique.size()} ${(new Date().time-b)/1000}"
		}
		println "${postBody.query.fields} UNIQUE [${cumulativeUnique.size()}] ${(new Date().time-b)/1000}"
		
		
		println "${postBody.query.fields} UNIQUE-SAVE-DONE [${cumulativeUnique.size()}] ${(new Date().time-b)/1000}"
		
		c.scrollCount = cumulativeUnique.size()
		c.save(failOnError:true,flush:true)
			
		cumulativeUnique = null
		println "${postBody.query.fields} FINAL ${(new Date().time-b)/1000}"
		return c
	}
	
}