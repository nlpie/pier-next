package edu.umn.nlpie.pier.api

import edu.umn.nlpie.pier.context.PierContext
import grails.plugin.springsecurity.annotation.Secured
import grails.plugins.rest.client.RestBuilder
import groovy.json.JsonSlurper
import groovy.sql.Sql
import groovyx.gpars.GParsPool

import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.methods.HeadMethod

@Secured(["ROLE_SUPERADMIN"])
class IndexingController {
	
	def dataSource_notes
	def grailsApplication
	
	static responseFormats = ['json']
	
	//TODO refactor to superclass
	private exceptionResponse(Exception e) {
		def msg = e.message.replace('\n',' ')	//\n causes problems when client parses returned JSON
		respond ( e, status:e.status )
		//render(status: e.status, text: '{"message":"'+ msg +'"}', contentType: "application/json") as JSON
	}

	def updateNoteMetaUsingElasticIndex() {
		def log = new File("${grailsApplication.config.logfiles.updateNoteMetaUsingElasticIndex}")
		def templateUrl = "http://nlp05.ahc.umn.edu:9200/notes_2019/note/_search?q=note_version_id:NVID"
		def sql = """
		SELECT NOTE_ID, NOTE_VERSION_ID, INDEXING_STATUS, NOTE_LENGTH, STATUS FROM RDC_DT.DT_NOTE_META 
		WHERE INDEXING_STATUS='NOT INDEXED' AND STATUS='ACTIVE' AND NOTE_LENGTH>0 
		AND mod(note_id,100)=BATCH"""
		//https://sites.google.com/a/athaydes.com/renato-athaydes/code/groovy---rest-client-without-using-libraries
		log.append("--------------------------${new Date()}------------------------------\n")
		
		GParsPool.withPool(5) {
			(0..99).eachParallel { batch ->
				def start = new Date().time
				def incrementStart = start
				def select
				def update
				try {
					def rest = new RestBuilder()
					select = Sql.newInstance(dataSource_notes)
					update = Sql.newInstance(dataSource_notes)
					//def notIndexed = NoteMeta.findAllByStatusAndIndexingStatusAndNoteLengthGreaterThan('ACTIVE','NOT INDEXED',0,[max: 20000])
					def found = 0
					def updated = 0
					select.eachRow( sql.replace("BATCH",batch.toString()) ) { meta ->
						found++
						if ( found%1000==0 ) {
							def now = new Date().time
							def elapsed = (now-start)/1000
							def interval = (now-incrementStart)/1000
							incrementStart = now
							println "${batch} processed ${found} in ${interval}s, ${elapsed}s since start of batch"
						}
						//println "${meta.note_id}, ${meta.note_version_id}, ${meta.note_length}"
						def url = templateUrl.replace( "NVID", meta.note_version_id.toString() )
						def connection = new URL( url ).openConnection() as HttpURLConnection
						// set some headers
						connection.setRequestProperty( 'User-Agent', 'NLP-PIER' )
						connection.setRequestProperty( 'Accept', 'application/json' )
						//def esResponse = rest.get(url)// { json elasticQuery.toString() }
						def status = connection.responseCode	//esResponse.status
						if ( status == 200 ) {
							// get the JSON response
							def json = connection.inputStream.withCloseable { inStream ->
								new JsonSlurper().parse( inStream as InputStream )
							}
							if ( !json.timed_out && json.hits.total==1 ) {
								//println "${meta.note_id}, ${meta.note_version_id}, ${meta.note_length} ${status} ${json.hits.total}"	
								update.executeUpdate "update RDC_DT.DT_NOTE_META set INDEXING_STATUS='INDEXED' where NOTE_VERSION_ID=${meta.NOTE_VERSION_ID}"
								update.executeUpdate "update PIER_INDEXING_META_SNAPSHOT_5 set INDEXING_STATUS='INDEXED' where NOTE_VERSION_ID=${meta.NOTE_VERSION_ID}"
								updated++
							}			
						} else {
							println "Batch[${batch}] ${status}:${connection.inputStream.text}"
						}
					}
					log.append ("${batch}: found ${found}, updated ${updated}\n")
				} catch (Exception e) {
					println "Batch[${batch}] ${e.message}"
				} finally {
					println "CLOSING db connections for Batch[${batch}]"
					select.close()
					update.close()
				}
			}
		}
		println "DONE updateNoteMetaUsingElasticIndex()"
	}
	
	def contexts() {
		def contexts = PierContext.findAllByNoteStatus('Ready for Processing')
		contexts.each {
			println "${it.label}, ${it.contextFilterValue}"
		}
	}
	
	def compileTest() {
		HeadMethod head = new HeadMethod( "${cluster}/${index}/${docType}/${noteId}" )
	}
	
}
