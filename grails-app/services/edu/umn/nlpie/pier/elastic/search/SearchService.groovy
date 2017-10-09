package edu.umn.nlpie.pier.elastic.search

import edu.umn.nlpie.pier.api.HistorySummaryDTO
import edu.umn.nlpie.pier.api.ScrollPayload
import edu.umn.nlpie.pier.api.exception.BadElasticRequestException
import edu.umn.nlpie.pier.audit.Bucket
import edu.umn.nlpie.pier.audit.DistinctCount
import edu.umn.nlpie.pier.audit.Query
import edu.umn.nlpie.pier.audit.ScrollValue
import edu.umn.nlpie.pier.audit.SearchRegistration
import groovy.sql.Sql
import groovyx.gpars.GParsPool


//@Transactional
class SearchService {
	
	def elasticService
	def dataSource
	
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
	
	def logNoteIdCountInfo( postBody, elasticResponse ) {
		def b = new Date().time
		def json = elasticResponse.json
		//println json.toString(2)
		def sr = SearchRegistration.get(postBody["registration.id"].toLong())
		def c = new DistinctCount(registration:sr)
		c.query = postBody.query
		c.terms = postBody.query.query.bool.must.query_string.query
		c.httpStatus = elasticResponse.status
		c.hits = json.hits.total
		c.took = json.took
		c.timedOut = json.timed_out
		c.label = postBody.label
		c.countType = "hits"
		def cardinalityLabel = postBody.label + " Cardinality Estimate"
		c.cardinalityEstimate = json.aggregations[cardinalityLabel].value
		c.save(failOnError:true,flush:true)
		sr.save(failOnError:true,flush:true)
		println "CE [${postBody.label}]: ${c.id} ${c.cardinalityEstimate}"
		println "${postBody.label} ${c.hits} FINAL ${(new Date().time-b)/1000}"
		return c
	}
	
	def logBucketCountInfo( postBody, elasticResponse ) {
		def b = new Date().time
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
		def buckets = json.aggregations[postBody.label]?.buckets
		c.bucketCount = (buckets) ?  buckets.size() : 0
		c.save(failOnError:true,flush:true)
		sr.save(failOnError:true,flush:true)
		println "CE: [${postBody.label}] ${c.id} ${c.cardinalityEstimate}"
		
		if ( buckets ) {
			def sql = new Sql(dataSource)
			String insertStmt = "insert into bucket ( `key`, key_as_string, doc_count, distinct_count_id, version ) values ( :key, :keyAsString, :docCount, :fk, :ver )"
			//GParsPool.withPool {
				sql.withBatch( 3000, insertStmt ) { ps ->
					buckets.each { 
						ps.addBatch( key:it.key, keyAsString:it.key_as_string?:null, docCount:it.doc_count, fk:c.id, ver:0 )
						//c.addToBuckets( new Bucket( key:it.key, keyAsString:it.key_as_string, docCount:it.doc_count ) )
					}
				}
			//}
		}
		/*
		DistinctCount.async.task {
			withTransaction { status ->
				sql.withBatch( 5000, insertStmt ) { ps ->
					initialBatch.each {
						//println it
						ps.addBatch( val:it.toString(), fk:pkId, ver:0 )
					}
				}
				//status.flush()
			}
		}
		*/
		println "${postBody.label} ${buckets.size()} FINAL ${(new Date().time-b)/1000}"
		return c
	}
	
	def logScrollCountInfo( postBody, elasticResponse ) {
		
		//look at https://hackernoon.com/parallel-scan-scroll-an-elasticsearch-index-db02583d10d1
		
		//println postBody.query.fields
		def json = elasticResponse.json
		//passed elasticResponse has hits that need to be added to count and ScrollValue collection
		//println "INIT RESPONSE"
		//println elasticResponse.json.toString(2)
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
		c.scrollCount = 0

		def cardinalityLabel = postBody.label + " Cardinality Estimate"
		c.cardinalityEstimate = json.aggregations[cardinalityLabel].value
		
		c.save(failOnError:true,flush:true)
		sr.save(failOnError:true,flush:true)
		def pkId = c.id
		println "CE: ${pkId} ${c.cardinalityEstimate}"
		
		def cumulativeUnique = Collections.synchronizedSet( new HashSet() )//Collections.synchronizedList( new ArrayList() )
		def b = new Date().time
		
		//def sql = new Sql(dataSource)
		//String insertStmt = "insert into scroll_value ( value, distinct_count_id, version ) values ( :val, :fk, :ver )"

		//add hits to set/count
		//optimization based on http://danielhalima.com/grails/2011/04/30/groovy-sql-e-batch-updates-microbenchmark/?lang=en
		//def sql = new Sql(dataSource)
		//String insertStmt = "insert into scroll_value ( value, distinct_count_id, version ) values ( :val, :fk, :ver )"
		def initialBatch = Collections.synchronizedSet( new HashSet() )
		GParsPool.withPool {
			//sql.withBatch( 20000, insertStmt ) { ps ->
				json.hits.hits.each {
					def val = it.fields[postBody.query.fields][0]
					//c.addToScrollValues( new ScrollValue( value:it.toString() ))
					//new ScrollValue( value:it.toString(), distinctCount:c ).save()
					initialBatch << val
					//ps.addBatch( val:val.toString(), fk:c.id, ver:0 )
				}
			//}
		}
		cumulativeUnique.addAll( initialBatch )
		
		/* GOOD 
		 * DistinctCount.async.task {
			withTransaction {
				def sql = new Sql(dataSource)
				String insertStmt = "insert into scroll_value ( value, distinct_count_id, version ) values ( :val, :fk, :ver )"
				sql.execute( insertStmt, [val:initialBatch.join(" "), fk:pkId, ver:0] )
			}
		}
		*/
		
		/*
		slow
		DistinctCount.async.task {
			withTransaction { status ->
				sql.withBatch( 5000, insertStmt ) { ps ->
					initialBatch.each {
						//println it
						ps.addBatch( val:it.toString(), fk:pkId, ver:0 )
					}
				}
				//status.flush()
			}
		}*/
		
		println " initial increment [${postBody.query.fields}] ${initialBatch.size()}"
		
		//def sql = new Sql(dataSource)
		//String insertStmt = "insert into scroll_value ( value, distinct_count_id, version ) values ( :val, :fk, :ver )"
		/*GParsPool.withPool {
			sql.withBatch( 5000, insertStmt ) { ps ->
				initialBatch.eachParallel {
					//def val = it.fields[postBody.query.fields][0]
					ps.addBatch( val:it.toString(), fk:c.id, ver:0 )
				}
			}
		}*/
		
		/*initialBatch.each {
			//println it
			DistinctCount.async.task {
			    withTransaction {
			       //def dc = get(c.id)
			       c.addToScrollValues( new ScrollValue( value:it.toString() ))
				   c.save()    
			    }
			}	
		}*/
		
		//ExecutorService executor = Executors.newFixedThreadPool(10)
		
		//Runnable initInsert = new ScrollValuesInserter( dataSource:dataSource, batchIncrement:cumulativeUnique, fkId:c.id, batchSize:5000)
		//executor.execute(initInsert)
		
		/*def sql = new Sql(dataSource)
		String insertStmt = "insert into scroll_value ( value, distinct_count_id, version ) values ( :val, :fk, :ver )"
		sql.withBatch( 5000, insertStmt ) { ps ->
			cumulativeUnique.each {
				//def val = it.fields[postBody.query.fields][0]
				ps.addBatch( val:it.toString(), fk:c.id, ver:0 )
			}
		}*/
		//iterate through scroll sets
		
		def scrollId = json._scroll_id
		def scroll = true
		while ( scroll ) {
			def scrollBatch = Collections.synchronizedSet( new HashSet() )
			def sp = new ScrollPayload( scroll_id:scrollId, scroll:"1m" )
			//println postBody.toString(2)
			def response = elasticService.scroll( postBody.scrollUrl, sp )
			//println "SCROLL ITERATION RESPONSE"
			//println response.json.toString(2)
			def hits = response.json.hits.hits
			GParsPool.withPool {
				//sql.withBatch( 20000, insertStmt ) { ps ->
					hits.eachParallel {
						def val = it.fields[postBody.query.fields][0]
						//c.addToScrollValues( new ScrollValue( value:it.toString() ))
						//new ScrollValue( value:it.toString(), distinctCount:c ).save()
						scrollBatch << val
						//ps.addBatch( val:val.toString(), fk:c.id, ver:0 )
					}
				//}
			}
			def batchIncrement = scrollBatch - ( cumulativeUnique.intersect(scrollBatch) )
			cumulativeUnique.addAll( batchIncrement )
			
			
			/*
			Promise p = Promises.task {
				DistinctCount.withNewSession {
					//def dc = DistinctCount.get(pkId) cannot .get b/c it is not visible to this session
					batchIncrement.each {
						dc.addToScrollValues( id:pkId, value:it.toString() )
						//ps.addBatch( val:it.toString(), fk:c.id, ver:0 )
					}
					dc.save()
				}
			}*/
		//sleep(5000)
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
				
				/*DistinctCount.async.task {
					withTransaction { status ->
						sql.withBatch( 5000, insertStmt ) { ps ->
							batchIncrement.each {
								//println it
								ps.addBatch( val:it.toString(), fk:pkId, ver:0 )
							}
						}
						//status.flush()
					}
				}
				*/
			}
			
			//Runnable inserter = new ScrollValuesInserter( dataSource:dataSource, batchIncrement:batchIncrement, fkId:c.id, batchSize:5000 )
			//executor.execute(inserter)
			
			/*sql.withBatch( 5000, insertStmt ) { ps ->
				batchIncrement.each {
					//def val = it.fields[postBody.query.fields][0]
					ps.addBatch( val:it.toString(), fk:c.id, ver:0 )
				}
			}*/
			
			/*GParsPool.withPool {
				def sql = new Sql(dataSource)
				String insertStmt = "insert into scroll_value ( value, distinct_count_id, version ) values ( :val, :fk, :ver )"
				sql.withBatch( 1000, insertStmt ) { ps -> 
					cumulativeUnique.eachParallel {
						ps.addBatch( val:it.toString(), fk:c.id, ver:0 )
					}
				}
			}*/
			
			scroll = ( hits.size()>0 ? true : false )
			def status = elasticResponse.status
			//println "distinct: ${status} ${elasticResponse.json.toString(2)}"
				
			if ( status==400 ) {
				throw new BadElasticRequestException( message:"Scroll iteration problem" )
			}
			scrollId = elasticResponse.json._scroll_id
			println "${postBody.query.fields} ${cumulativeUnique.size()} ${(new Date().time-b)/1000}"
		}
		//def distinctValues = allValues.unique()
		println "${postBody.query.fields} UNIQUE [${cumulativeUnique.size()}] ${(new Date().time-b)/1000}"
		
		
		println "${postBody.query.fields} UNIQUE-SAVE-DONE [${cumulativeUnique.size()}] ${(new Date().time-b)/1000}"
		
		c.scrollCount = cumulativeUnique.size()
		c.save(failOnError:true,flush:true)
			
		cumulativeUnique = null
		//executor.shutdown()
		//while (!executor.isTerminated()) {
		//}
		println "${postBody.query.fields} FINAL ${(new Date().time-b)/1000}"
		return c
	}
	
}