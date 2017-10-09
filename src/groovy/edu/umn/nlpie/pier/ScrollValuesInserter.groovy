/**
 * 
 */
package edu.umn.nlpie.pier

import groovy.sql.Sql


/**
 * @author ${name:git_config(user.name)} and ${email:git_config(user.email)} (rmcewan) 
 *
 */
class ScrollValuesInserter implements Runnable {
	
	
	//pass these in constructor
	def dataSource
	HashSet batchIncrement
	Long fkId
	Integer batchSize 
	
	final String insertStmt = "insert into scroll_value ( value, distinct_count_id, version ) values ( :val, :fk, :ver )"
	
	void run() {
		def sql = new Sql(dataSource)
		sql.withBatch( batchSize, insertStmt ) { ps ->
			batchIncrement.each {
				//def val = it.fields[postBody.query.fields][0]
				ps.addBatch( val:it.toString(), fk:fkId, ver:0 )
			}
		}
		//sql.close()
	}
}
