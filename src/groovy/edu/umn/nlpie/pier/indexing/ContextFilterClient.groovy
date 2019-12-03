
package edu.umn.nlpie.pier.indexing

import groovy.json.JsonSlurper
import groovy.sql.Sql
import groovyx.gpars.GParsPool

//import org.apache.commons.dbcp2.*

import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager
import org.apache.commons.httpclient.methods.HeadMethod
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
	
//old commons http
//import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager
//import org.apache.commons.httpclient.HttpClient
//import org.apache.commons.httpclient.methods.HeadMethod
//import org.apache.commons.httpclient.methods.PostMethod
//newer http client	
import org.apache.http.impl.client.HttpClientBuilder
//import org.apache.http.entity.StringEntity
//import org.apache.http.client.methods.HttpPost
//import org.apache.http.util.EntityUtils
	
class ContextFilterClient {
	
	//https://openjdk.java.net/groups/net/httpclient/recipes.html
	def dataSource_notes
	
	def configPath = "context.filter.config.groovy"
	def config = new ConfigSlurper().parse( new File( configPath ).toURI().toURL().text )
	
	//https://github.com/eugenp/tutorials/blob/master/httpclient/src/test/java/org/baeldung/httpclient/conn/MultiHttpClientConnThread.java
	//set up http pooling
	//PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
	
	//connManager.setDefaultMaxPerRoute(100);   //per destination
	//connManager.setMaxTotal(500);            //across all destinations
	def timeout = config.http.timeout
	/*
	RequestConfig reqConfig = RequestConfig.custom()
		.setConnectTimeout(timeout * 1000)
		.setConnectionRequestTimeout(timeout * 1000)
		.setSocketTimeout(timeout * 1000).build()
	*/
	//set up db conn pooling
	/*def poolSize = 25
	BasicDataSource dataSource = new BasicDataSource()
	dataSource.setDriverClassName( config.db.driver )
	dataSource.setUrl( config.db.url )
	dataSource.setUsername( config.db.username )
	dataSource.setPassword( config.db.password )
	dataSource.setMinIdle(10)
	dataSource.setMaxIdle(100)
	dataSource.setMaxOpenPreparedStatements(100)
	dataSource.setPoolPreparedStatements(true)
	dataSource.setInitialSize(5);           //init num of conns
	dataSource.setMinIdle(0);
	dataSource.setMaxIdle(poolSize-5);
	dataSource.setMaxTotal(poolSize);       //total # active and idle conn in pool
	dataSource.setTestOnBorrow(true);
	dataSource.setTestWhileIdle(true);
	dataSource.setValidationQuery("select 1 from dual");*/
	
	//set up elastic cluster, index, note values
	def cluster = config.elastic.cluster	//"http://nlp05.ahc.umn.edu:9200"
	def index = config.elastic.index 		//"notes_2019"
	def docType = config.elastic.type	//"note"
	
	def statement = new File( config.src.sql.file ).text
	
	def process( cluster, index, docType, noteId, filterValues, existsClient, metrics, json ) {
		try {
			if ( existsInIndex( cluster, index, docType, noteId, existsClient, metrics ) ) {
				def indivUpdate = upsertPayload( noteId, docType, index, filterValues )
				json << indivUpdate
			}
		} catch(Exception e) {
			def exceptionLog = new File("context-filter-exceptions")
			exceptionLog << "PROCESS EXCEPTION: ${e.message}\n"
		} finally {
			
		}
	}
	
	def existsInIndex( cluster, index, docType, noteId, existsClient, metrics ) {
		//def response	//
		//def head = new HttpHead("${cluster}/${index}/${docType}/${noteId}")
		//https://hc.apache.org/httpclient-3.x/methods/head.html
		HeadMethod head = new HeadMethod( "${cluster}/${index}/${docType}/${noteId}" )
		try {
			
			def status = existsClient.executeMethod( head )
	//println "${noteId} ${status}"
			//if ( response.statusLine.statusCode == 200 ) {
			if ( status == 200 ) {
				metrics.incrementExists()
				return true
			}
			if ( status == 404 ) {
				metrics.incrementNotExists()
				return false
			}
			//shouldn't get here - log it
			def unexpectedLog = new File("elastic-unexpected-responses")
			unexpectedLog << "${cluster}/${index}/${docType}/${noteId} ${response.statusLine}\n"
			metrics.incrementErrored()
		} catch(Exception e) {
			def exceptionLog = new File("context-filter-exceptions")
			exceptionLog << "EXISTS IN ELASTIC EXCEPTION: ${e.message}\n"
		} finally {
			//if ( response ) response.close()
			head.releaseConnection()
		}
	}
	
	def upsertPayload( noteId, docType, index, filterValues ) {
"""{ "update" : {"_id" : "${noteId}", "_type" : "${docType}", "_index" : "${index}", "_retry_on_conflict":2} }
{"doc":{"authorized_context_filter_value":[${filterValues}]},"doc_as_upsert":true}
"""
	}

	
	
		
		
	//def bulkUpdate( cluster, index, docType, noteId, filterValues, metrics, client ) {
	def bulkUpdate( cluster, metrics, json ) {
		try {
			//String json = """{"doc":{"authorized_context_filter_value":[${filterValues}]},"doc_as_upsert":true}"""
		
			HttpClientBuilder.create().build().withCloseable { client ->
				HttpPost post = new HttpPost( "${cluster}/_bulk" )
				StringEntity entity = new StringEntity( json.toString() )
				post.setEntity( entity )
				//post.setHeader("Accept", "application/json")
				post.setHeader("Content-type", "application/json")
				
				client.execute( post ).withCloseable { response ->
					if ( response.statusLine.statusCode == 200 ) {
						def elasticResponse = new JsonSlurper().parseText( EntityUtils.toString( response.getEntity() ) )
						if ( !elasticResponse.errors ) {
							metrics.incrementUpdated( elasticResponse.items.size() )
							//def successFile = new File( new Date().toString()+".dat" )
							//successFile << json.toString() << "\n\n" << elasticResponse.toString() 
						}
					} else {
						def errorFile = new File( "Batch_" + metrics.batch + new Date().toString()+".err" )
						errorFile << json.toString() << "\n\n" << EntityUtils.toString( response.getEntity() )
					}
					json = new StringBuilder()
				}
				
			}
		} catch(Exception e) {
			e.printStackTrace()
			def exceptionLog = new File("context-filter-exceptions")
			exceptionLog << "UPDATE ELASTIC DOC EXCEPTION: ${e.message}\n"
		} finally {
			//response.close()
			//post.releaseConnection()
		}
		
		//var url = "http://" + esHost + "/" + indexName + "/note/" + noteDoc.note_id + "/_update";
		//117275144, 1530218728, 1917538125, 3244782578, 351010683, 3544847184, 3741645127, 3845730989, 739265607, 967841764
		//POST http://nlp05.ahc.umn.edu:9200/notes_2019/note/1000002486/_update
		/*
			{	"doc":
				{
					"authorized_context_filter_value": [ 117275144, 1530218728, 1917538125, 3244782578, 351010683, 3544847184, 3741645127, 3845730989, 739265607, 967841764 ]
				},
				"doc_as_upsert" : true
			}
			
			//REQUEST, ends with \n
			var url = "http://" + esHost + "/_bulk"
			{ "update" : {"_id" : "1000078217", "_type" : "note", "_index" : "notes_2019", "_retry_on_conflict":2} }
			{"doc":{"authorized_context_filter_value":[1]},"doc_as_upsert":true}
			{ "update" : {"_id" : "1000104645", "_type" : "note", "_index" : "notes_2019", "_retry_on_conflict":2} }
			{"doc":{"authorized_context_filter_value":[1, 2, 3, 4, 5, 6, 7, 8, 9, 10]},"doc_as_upsert":true}
		
			
			//RESPONSE
			{
				"took": 3,
				"errors": false,
				"items": [
					{
						"update": {
							"_index": "notes_2019",
							"_type": "note",
							"_id": "1000078217",
							"_version": 3,
							"_shards": {
								"total": 1,
								"successful": 1,
								"failed": 0
							},
							"status": 200
						}
					},
					{
						"update": {
							"_index": "notes_2019",
							"_type": "note",
							"_id": "1000104645",
							"_version": 3,
							"_shards": {
								"total": 1,
								"successful": 1,
								"failed": 0
							},
							"status": 200
						}
					}
				]
			}
			*/
		
	}	

	def process() {
		GParsPool.withPool( 10 ) {
			(0..4).eachParallel { batch ->
				sleep( batch*10 )	//spacer
				def sql = new Sql( dataSource )
				sql.withStatement { stmt -> stmt.fetchSize = 300 }		
				def metrics = new Metrics( unit:"updates", batch:batch )
				//CloseableHttpClient client = HttpClients.custom().setConnectionManager( connManager ).setDefaultRequestConfig( reqConfig ).build()
				MultiThreadedHttpConnectionManager connManager = new MultiThreadedHttpConnectionManager()
				HttpClient existsClient = new HttpClient( connManager )
				//GParsPool.withPool() { pool ->
				def json = new StringBuilder()
				sql.eachRow( statement.toString(), [batch] ) { contextNote ->
					//println "PROCESSING ${pool} ${contextNote.NOTE_ID}"
					process( cluster, index, docType, contextNote.NOTE_ID, contextNote.FILTER_VALUES, existsClient, metrics, json )
					if ( metrics.incrementProcessed()%config.stats.interval==0 ) {
						bulkUpdate( cluster, metrics, json )
						println metrics.stats()
						json = new StringBuilder()
					}
				}
				if ( !json.toString().isEmpty() ) {
					bulkUpdate( cluster, metrics, json )
				}
				println metrics.stats()
				metrics.log()
				println "BATCH ${batch} -- connManager and sql close..."
				sql.close()
				connManager.shutdown()
			}	
		}
		println "dataSource close..."
		dataSource_notes.close()
	}


}
