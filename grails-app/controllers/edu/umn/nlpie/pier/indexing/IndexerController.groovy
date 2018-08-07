package edu.umn.nlpie.pier.indexing


import grails.plugins.rest.client.RestBuilder
import groovy.sql.Sql
import groovyx.gpars.GParsPool

//this class supports indexing of Echo reports and data for FV use cases
class IndexerController {
	
	def dataSource_notes
	
	def efText() {
		GParsPool.withPool {
			(0..3).eachParallel { batch ->
				def sql = Sql.newInstance(dataSource_notes)
				def counter = 0
				sql.withStatement { stmt ->
					stmt.setFetchSize(10)	
				}
				
				sql.eachRow( this.textSql(), [batch] ) { row ->
					println "${++counter} [${batch}]"
					def doc = [:]
					//def measures = []
					doc.put( "text", text(row) )
					doc.put( "text_source", textSource(row) )
					doc.put( "text_format", 'formatted' )

					row.toRowResult().each { name, val ->
						//println "${name} ${val}"
						if ( name.toLowerCase()!="narrative" 
							&& name.toLowerCase()!="impression" 
							&& name.toLowerCase()!="result_comment" 
							&& name!="id" ) {
								doc.put(name, val)
						}
					}
					
					def rest = new RestBuilder() 
					def resp = rest.put("http://nlp05.ahc.umn.edu:9200/ef_v1/summary/${row.id}") { json doc }
					println "${row.id} [${batch}] ${resp.status} ${resp.text} "
				
					//if ( totalProcessed%1000==0 || num==cnt ) println "$batch\t${found}\t\t${missing}"
				}
				sql.close()
			}
		}
	}
	
	def efMetadata() {
		println "starting efMetadata()...."
		GParsPool.withPool {
			def modDivisor = 5
			def threads = modDivisor-1
			(0..threads).eachParallel { thread ->
				def sql = Sql.newInstance(dataSource_notes)
				def counter = 0
				sql.withStatement { stmt ->
					stmt.setFetchSize(20)
				}
				
				sql.eachRow( this.metadataSql(), [ modDivisor,thread ] ) { row ->
					def doc = [:]
					def updateData = [:]

					row.toRowResult().each { name, val ->
						//println "${name} ${val}"
						if ( name.toLowerCase()!="narrative"
							&& name.toLowerCase()!="impression"
							&& name.toLowerCase()!="result_comment"
							&& name!="id" ) {
								updateData.put(name, val)
						}
					}
					
					def rest = new RestBuilder()
					doc.put("doc", updateData)
					def resp = rest.post("http://nlp05.ahc.umn.edu:9200/ef_v1/summary/${row.id}/_update") { json doc }
					println "${++counter} [${thread}] ${resp.status} ${resp.text} "
					sleep(20)
				}
				sql.close()
			}
		}
		println "DONE"
	}
	
	def efData() {
		println "starting efData()...."
		GParsPool.withPool {
			def modDivisor = 10
			def threads = modDivisor-1
			(0..threads).eachParallel { thread ->
				def sql = Sql.newInstance(dataSource_notes)
				def counter = 0
				sql.withStatement { stmt ->
					stmt.setFetchSize(20)
				}
				
				sql.eachRow( this.discreteDataSql(), [ modDivisor,thread ] ) { row ->
					def doc = [:]
					def updateData = [:]
					def measures = []
					
					
					if ( row.measures ) {
						def measuresArray = row.measures.split(",")
						measuresArray.each {
							measures << it.trim()
						}
						updateData.put( "ef_results", row.measures_and_values )
						updateData.put("ef_measures", measures)
						updateData.put("SYNTHETIC_KEY", row.synthetic_key)
					} else {
						updateData.put( "ef_results", "n/a" )
						updateData.put("ef_measures", "n/a")
					}
					
					doc.put("doc", updateData)
					def rest = new RestBuilder()
					def resp = rest.post("http://nlp05.ahc.umn.edu:9200/ef_v1/summary/${row.id}/_update") { json doc }
					println "${++counter} [${thread}] ${resp.status} ${resp.text} "
				
					//if ( totalProcessed%1000==0 || num==cnt ) println "$batch\t${found}\t\t${missing}"
				}
				sql.close()
			}
		}
	}
		
	def efFindings() {
		println "starting efData()...."
		GParsPool.withPool {
			def modDivisor = 5
			def threads = modDivisor-1
			(0..threads).eachParallel { thread ->
				def sql = Sql.newInstance(dataSource_notes)
				def counter = 0
				sql.withStatement { stmt ->
					stmt.setFetchSize(20)
				}
				
				sql.eachRow( this.findingsSql(), [ modDivisor,thread ] ) { row ->
					def doc = [:]
					def updateData = [:]
					def findings = []
					
					
					if ( row.findings ) {
						updateData.put( "ef_results", row.findings )
						//updateData.put("SYNTHETIC_KEY", row.synthetic_key)
					} else {
						updateData.put( "ef_results", "n/a" )
						//updateData.put("ef_measures", "n/a")
					}
					
					doc.put("doc", updateData)
					def rest = new RestBuilder()
					def resp = rest.post("http://nlp05.ahc.umn.edu:9200/ef_v1/summary/${row.id}/_update") { json doc }
					println "${++counter} [${thread}] ${resp.status} ${resp.text} "
				
					//if ( totalProcessed%1000==0 || num==cnt ) println "$batch\t${found}\t\t${missing}"
				}
				sql.close()
			}
		}
	}
		
		
	
	private textSource(row) {
		def src
		if ( row.impression && !row.narrative && !row.result_comment ) {
			src = "impression"
		} else if ( !row.impression && row.narrative && !row.result_comment ) {
			src = "narrative"
		} else if ( !row.impression && !row.narrative && row.result_comment ) {
			src = "result comment"
		} else if ( row.impression && row.narrative && !row.result_comment ) {
			src = "impression/narrative"
		} else if ( row.impression && !row.narrative && row.result_comment ) {
			src = "impression/result comment"
		} else if ( !row.impression && row.narrative && row.result_comment ) {
			src = "narrative/result comment"
		}
		src
	}
	
	private text(row) {
		def text = new StringBuilder()
		if ( row.impression && !row.narrative && !row.result_comment ) {
			def clob = row.impression
			def len = clob.length()
			text << clob.getSubString(1.toLong(), len.toInteger())
			clob.free()
		} else if ( !row.impression && row.narrative && !row.result_comment ) {
			def clob = row.narrative
			def len = clob.length()
			text << clob.getSubString(1.toLong(), len.toInteger())
			clob.free()
		} else if ( !row.impression && !row.narrative && row.result_comment ) {
			def clob = row.result_comment
			def len = clob.length()
			text << clob.getSubString(1.toLong(), len.toInteger())
			clob.free()
		} else if ( row.impression && row.narrative && !row.result_comment ) {
			def iClob = row.impression
			def len = iClob.length()
			text << "NARRATIVE\n"
			text << iClob.getSubString(1.toLong(), len.toInteger())
			iClob.free()
			def nClob = row.narrative
			len = nClob.length()
			text << "\n\nIMPRESSION\n"
			text << nClob.getSubString(1.toLong(), len.toInteger())
			nClob.free()
		} else if ( row.impression && !row.narrative && row.result_comment ) {
			def iClob = row.impression
			def len = iClob.length()
			text << "IMPRESSION\n"
			text << iClob.getSubString(1.toLong(), len.toInteger())
			iClob.free()
			def rClob = row.result_comment
			len = rClob.length()
			text << "\n\nRESULT COMMENT\n"
			text << rClob.getSubString(1.toLong(), len.toInteger())
			rClob.free()
		} else if ( !row.impression && row.narrative && row.result_comment ) {
			def nClob = row.narrative
			def len = nClob.length()
			text << "NARRATIVE\n"
			text << nClob.getSubString(1.toLong(), len.toInteger())
			nClob.free()
			def rClob = row.result_comment
			len = rClob.length()
			text << "\n\nRESULT COMMENT\n"
			text << rClob.getSubString(1.toLong(), len.toInteger())
			rClob.free()
		}
		//println text.toString()
		text.toString()
	}
	
	private String textSql() {
		"""select order_id || '-' || order_result_id as "id",
order_id as "order_id", order_result_id as "order_result_id",
mrn as "mrn", patient_id as "patient_id", service_id as "service_id",
narrative, impression, result_comment,	
TO_CHAR(order_datetime,'YYYY-MM-DD HH24:MI') as "order_datetime", TO_CHAR(proc_start_datetime,'YYYY-MM-DD HH24:MI') as "procedure_start_datetime", 
TO_CHAR(order_result_datetime,'YYYY-MM-DD HH24:MI') as "order_result_datetime", TO_CHAR(parent_order_datetime,'YYYY-MM-DD HH24:MI') as "parent_order_datetime",
TO_CHAR(collection_datetime,'YYYY-MM-DD HH24:MI') as "collection_datetime", TO_CHAR(result_result_datetime,'YYYY-MM-DD HH24:MI') as "result_result_datetime"
from fv_use_case_ef_text
where mod(service_id,10)=?
--and rownum<=20"""
	}
	
	private String metadataSql() {
		"""select o.order_id || '-' || r.order_result_id as "id",
o.order_id as "order_id", r.order_result_id as "order_result_id",
pt.mrn as "mrn", o.patient_id as "patient_id", o.service_id as "service_id",
o.proc_code as "procedure_code", o.proc_name as "procedure_name",
o.order_type_orig as "order_type_orig", o.ordering_mode_orig as "ordering_mode_orig",
r.test_name_orig as "test_name_orig",
pr.name as "provider", r.provider_id as "provider_id", o.authorizing_provider_id as "authorizing_provider_id", apr.name as "authorizing_provider"
from fv_use_case_ef_svc_order o
left join fv_use_case_ef_svc_ord_result r on o.order_id=r.order_id
left join rdc_dt.dt_patient pt on pt.patient_id=o.patient_id
left join rdc_dt.dt_provider pr on r.provider_id=pr.provider_id
left join rdc_dt.dt_provider apr on o.authorizing_provider_id=apr.provider_id
where mod(o.service_id,?)=?
--and rownum<=100
"""
	}
	
	private String discreteDataSql() {
		"""select * 
		from fv_use_case_ef_text_and_data 
		where mod(service_id,?)=?
		--and rownum<=10
		"""
	}
	
	private String findingsSql() {
		"""select t.order_id || '-' || t.order_result_id as "id",
f.*, t.service_id
from fv_use_case_ef_text t
left join NOTES.fv_use_case_ef_flat_findings f on (
  (substr(f.synthetic_key,1,10)=t.mrn and t.mrn||'-'||to_char(t.order_result_datetime,'YYYYMMDD')=substr(f.synthetic_key,0,19))
  OR
  (substr(f.synthetic_key,1,10)=t.mrn and t.mrn||'-'||to_char(t.result_result_datetime,'YYYYMMDD')=substr(f.synthetic_key,0,19))
  OR
  (substr(f.synthetic_key,1,10)=t.mrn and t.mrn||'-'||to_char(t.proc_start_datetime,'YYYYMMDD')=substr(f.synthetic_key,0,19))
  OR
  (substr(f.synthetic_key,1,10)=t.mrn and t.mrn||'-'||to_char(t.parent_order_datetime,'YYYYMMDD')=substr(f.synthetic_key,0,19))
  OR  
  (substr(f.synthetic_key,1,10)=t.mrn and t.mrn||'-'||to_char(t.collection_datetime,'YYYYMMDD')=substr(f.synthetic_key,0,19))
  OR
  (substr(f.synthetic_key,1,10)=t.mrn and t.mrn||'-'||to_char(t.order_datetime,'YYYYMMDD')=substr(f.synthetic_key,0,19))
)
where mod(service_id,?)=?
--and rownum<=100
"""
	}
	

	
	/*def updateContextFilters(requestSetId) {
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
	}*/
	
}
