package edu.umn.nlpie.pier.poc

import org.asynchttpclient.AsyncHttpClient
import org.asynchttpclient.DefaultAsyncHttpClient

import edu.umn.nlpie.pier.context.AuthorizedContext
import edu.umn.nlpie.pier.elastic.Type
import edu.umn.nlpie.pier.ui.FieldPreference
import grails.plugins.rest.client.RestBuilder
import grails.util.Environment
import groovy.sql.Sql
import groovyx.gpars.GParsPool


class PocController {
	
	def dataSource_notes

    def index() { 
		println AuthorizedContext.count()
		AuthorizedContext.findAllByUserIdIsNotNull(sort:"label").each {
			println "${it.label} clinical ${it.username} ${it.hasClinicalNotes()}"
			println "${it.label} microbio ${it.hasMicrobiologyNotes()}"
			println "---"
			//println "${it.label} \t\t\t\t\t\t\t ${ ( (it.hasClinicalNotes()==it.hasMicrobiologyNotes()) && it.hasMicrobiologyNotes() ) }"
		}
	}
	
	def defaultQueryFilters() {
		def type = Type.find("from Type as t where t.corpusType.id=? and environment=? and t.index.status=?", [ 1.toLong(), Environment.current.name, 'Available' ])
		def preferences = FieldPreference.where{ field.type.id==type.id && applicationDefault==true }.list()
		//prefs.each {
			//println "${it.label} ${it.ontology.name}"
		//}
		def ontologies = preferences.collect{ it.ontology }.unique()
		ontologies.each {
			println it.name
		}
		ontologies.each { o ->
			def prefsByOntology = preferences.findAll{ it.ontology.id==o.id && it.displayAsFilter==true }
			prefsByOntology.each {
				println "\t${it.label}"
			}
		}
		
	}
	
	/*
	def asyncAcl() {
		//https://github.com/AsyncHttpClient/async-http-client
		AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
		asyncHttpClient.properties.each { println it }
		Future<Response> f = asyncHttpClient.preparePost("http://www.google.com/").setBody("""{"body":"test"}""").execute();
		Response r = f.get();
	}
	*/
	
	private String template(id,contexts) {
		"""{ "update" : { "_index" : "notes_v3", "_type" : "note", "_id" : "${id}" } }
{"doc" : {"authorized_context_filter_value": ["${contexts}"] }}
"""
	}
	
	def qa() {
		//def requestSetId = params.id
		//println "starting ${requestSetId}"
		def s = new Date().time
		//asyncHttpClient()
		def totalProcessed = 0
		def found = 0
		def missing = 0
		GParsPool.withPool {
			(0..1).eachParallel { batch ->
			//(0..0).each { batch ->
				AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
				def sql = Sql.newInstance(dataSource_notes)
				sql.withTransaction {	
					sql.withStatement { stmt ->
						stmt.setFetchSize(5000)	//5000 works well in office, on nlp01?
					}
					def num = 0
					
					def result = sql.firstRow( "select count(*) as c from mm_277 where batch=?", [batch] )
					long cnt = result.c
					println "${cnt}, ${totalProcessed}"
					
	
					sql.eachRow( "select note_id from mm_277 where batch=?", [batch] ) { row ->
						totalProcessed++
						num++
						
						def rest = new RestBuilder()
						def url = "http://nlp05.ahc.umn.edu:9200/notes_v3/note/${row.note_id}"
						def esResponse = rest.head(url) { }
						esResponse.status==200 ? found++ : missing++
						
						/*
						Future<Response> f = asyncHttpClient.prepareHead("http://nlp05.ahc.umn.edu:9200/notes_v3/note/${row.note_id}").execute()
						Response r = f.get() //blocking, slows down loop
						//println "${r.statusCode} http://nlp05.ahc.umn.edu:9200/notes_v3/note/${row.note_id}"
						//println r.toString()
						r.statusCode==200 ? found++ : missing++ 
						*/
						if ( totalProcessed%1000==0 || num==cnt ) println "$batch\t${found}\t\t${missing}"
					}
				}
				sql.close()
			}
		}
		println "done ${totalProcessed}"
		println "took: ${((new Date().time)-s)/1000} sec"
	}
	
	def createAclTable(requestSetId) {
		def s = new Date().time
		def exists = true
		def check = "select * from req_${requestSetId} where rownum=1"
		def drop = "drop table req_${requestSetId} purge"
		def tbl = """
			create table REQ_${requestSetId} as
			select cn.note_id, listagg(trim(cn.filter_value),'", "') within group (order by cn.filter_value) as contexts, mod(cn.note_id,100) as batch
			from search_context_clinical_note cn
			where cn.note_id in ( select note_id from search_context_clinical_note where request_set_id=${requestSetId} )
			group by cn.note_id
		"""
		def sql = Sql.newInstance(dataSource_notes)
		sql.withTransaction {
			try {
				sql.execute( check.toString() )
				sql.execute( drop.toString() )
				println "REQ_${requestSetId}: dropped table"
				//sql.execute("""DROP INDEX req_${requestSetId}_batch_index""".toString() )
			} catch(e) {
				println "REQ_${requestSetId}: INFO ${e.message}"
			}
			try {
				sql.execute( tbl.toString() )
				def result = sql.firstRow( "select count(*) as c from req_${requestSetId}".toString() )
				sql.execute("""CREATE INDEX REQ_${requestSetId}_BATCH_IDX ON REQ_${requestSetId} (BATCH)""".toString() )
				println "REQ_${requestSetId}: SUCCESS ${result.c} notes; index created on BATCH col"
			} catch(ex) {
				println "REQ_${requestSetId}: FAIL ${ex.message} "
			}
		}
		sql.close()
		println "\ttook: ${((new Date().time)-s)/1000} sec"
	}
	
	def complete() {
		createAclTable(15500)
		updateContextFilters(15500)
	}
	
	def fb() {
		(1..100).each {
			def s = '' << "${it} "
 			(it%3==0)?s<<"fizz":s<<""
			(it%5==0)?s<<"buzz":s<<""
			println s
		}
	}
	
	def updateContextFilters(requestSetId) {
		//def requestSetId = params.id
		println "starting filter context updating"
		def disableRefreshJson = """{ "index" : { "refresh_interval" : "-1"} }"""
		def enableRefreshJson  = """{ "index" : { "refresh_interval" : "-1"} }"""
		//def enableRefreshJson  = """{ "index" : { "refresh_interval" : "60s"} }"""
		def refreshRest = new RestBuilder()
		def refreshResp = refreshRest.put("http://nlp05.ahc.umn.edu:9200/notes_v3/_settings") { json disableRefreshJson.toString() }
		println "refresh off: ${refreshResp.status}"
		def s = new Date().time
		GParsPool.withPool {
			(0..99).eachParallel { batch ->
			//(0..4).each { batch ->
				def sql = Sql.newInstance(dataSource_notes)
				sql.withStatement { stmt ->
					stmt.setFetchSize(5000)	//5000 works well in office, on nlp01?
				}
				def num = 0
				def payload = ''<<''
				sql.withTransaction {
					def result = sql.firstRow("select count(*) as c from REQ_${requestSetId} where batch=${batch}".toString())
					long cnt = result.c
					println cnt
					def incrementStartTime = new Date().time
					sql.eachRow("select * from REQ_${requestSetId} where batch=?".toString(), [batch]) { row ->
						num++
						payload << template(row.note_id,row.contexts)
						if ( num%250==0 || num==cnt) {
							
							def rest = new RestBuilder()
							def url = "http://nlp05.ahc.umn.edu:9200/_bulk" //println "refresh off: ${refreshResp.status}"
							def esResponse = rest.post(url) { json payload.toString() }
							payload = ''<<''
							def incrementEndTime = new Date().time
							println "${batch} ${num} ${incrementEndTime-incrementStartTime}ms: ${row.note_id} [\"${row.contexts}\"]"
							incrementStartTime = new Date().time
						}
					}
				}		
				sql.close()
			}
		}
		refreshResp = refreshRest.put("http://nlp05.ahc.umn.edu:9200/notes_v3/_settings") { json enableRefreshJson.toString() }
		println "refresh on: ${refreshResp.status}"
		println "\ttook: ${((new Date().time)-s)/1000} sec"
	}
}
