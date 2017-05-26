package edu.umn.nlpie.pier.poc

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
	
	def contextAcl() {
		def sql = Sql.newInstance(dataSource_notes)
		
		sql.withStatement { stmt ->
			stmt.setFetchSize(200)
		}
		def query = """
select cn.note_id, listagg(trim(cn.label),'", "') within group (order by cn.label) as contexts
from search_context_clinical_note cn
where cn.note_id in ( select note_id from search_context_clinical_note where request_set_id=14451 and rownum<=10000)
group by cn.note_id
"""
		
		GParsPool.withPool {
			//sql.eachRow(query) { row ->
			sql.rows(query).eachParallel { row ->
				println "${row.note_id} [\"${row.contexts}\"]"
				def rest = new RestBuilder()				
				def payload = """ {"doc" : {"search_context": ["${row.contexts}"] }} """
				def esResponse = rest.post("http://nlp05.ahc.umn.edu:9200/notes_v2/note/${row.note_id}/_update") {
					json  payload
				}
				
				if ( esResponse.status==200 ) {
					println "\t200"
				} else {
					println "${esResponse.status} ${esResponse.text}"
				}		
			}
		}
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
