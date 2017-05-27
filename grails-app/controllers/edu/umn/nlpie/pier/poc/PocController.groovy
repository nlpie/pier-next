package edu.umn.nlpie.pier.poc

import java.util.concurrent.Future

import javax.xml.ws.Response

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
	
	def splat() {
		println "starting"
		def s = new Date().time
		GParsPool.withPool {
			(0..1).eachParallel { batch ->
				def sql = Sql.newInstance(dataSource_notes)
				sql.withStatement { stmt ->
					stmt.setFetchSize(200)
				}
				def num = 0
				def query = "select * from Z498_DELETE where batch=? and rownum<=10000"
				//def rest = new RestBuilder()				
				sql.eachRow(query,[batch]) { row ->
					println num++
					//if (num%10000==0)	println "${num}: ${row.note_id} [\"${row.contexts}\"]"	
				}
				sql.close()
			}
		}
		println "took: ${((new Date().time)-s)/1000} sec"
	}
	
	def asyncAcl() {
		//https://github.com/AsyncHttpClient/async-http-client
		AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
		asyncHttpClient.properties.each { println it }
		Future<Response> f = asyncHttpClient.preparePost("http://www.google.com/").setBody("""{"body":"test"}""").execute();
		Response r = f.get();
	}
	
	def contextAcl() {
		println "starting"
		def s = new Date().time
		//asyncHttpClient.properties.each { println it }
		AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
		GParsPool.withPool {
			(0..1).eachParallel { batch ->
				def sql = Sql.newInstance(dataSource_notes)
				def rest = new RestBuilder()	
				sql.withStatement { stmt ->
					stmt.setFetchSize(100)
				}
				def num = 0
				def query = """
				select cn.note_id, listagg(trim(cn.label),'", "') within group (order by cn.label) as contexts
				from search_context_clinical_note cn
				where cn.note_id in ( select note_id from search_context_clinical_note where request_set_id=10573 and rownum<=100000)
				group by cn.note_id
				"""
				query = "select * from Z498_DELETE where batch=? and rownum<=5000"
				
					sql.eachRow(query,[batch]) { row ->
					//sql.rows(query,[batch]).eachParallel { row ->
						num++
						if (num%1000==0)	println "${num}: ${row.note_id} [\"${row.contexts}\"]"
						def payload = """ {"doc" : {"search_context": ["${row.contexts}"] }} """
						/*def esResponse = rest.post("http://nlp05.ahc.umn.edu:9200/notes_v2/note/${row.note_id}/_update") {
							json  payload
						}*/
						Future<Response> f = asyncHttpClient.preparePost("http://nlp05.ahc.umn.edu:9200/notes_v2/note/${row.note_id}/_update")
							.setBody(payload).execute();
						println "${batch} ${num}"
						/*
						if ( esResponse.status==200 ) {
							//println "\t200"
						} else {
							println "\t${num} ${esResponse.status} ${esResponse.text} ${row.note_id} [\"${row.contexts}\"] "
						}
						*/
					}		
				
				sql.close()
			}
		}

		println "done"
		println "took: ${((new Date().time)-s)/1000} sec"
		/*	
		http://localhost:9200/poc/note/100005182/_update
		{
			"doc" : {
				"search_context" : ["ZhangR-Req00259", "ZhangR-Req00498", "New_Req00000"]
			}
		}
		*/
	}
}
