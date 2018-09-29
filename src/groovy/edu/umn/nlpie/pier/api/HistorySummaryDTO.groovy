/**
 * 
 */
package edu.umn.nlpie.pier.api

import groovy.transform.InheritConstructors


@InheritConstructors
class HistorySummaryDTO {
	
	HistorySummaryDTO( Object o ) {
		//this.registration["id"] = o[0]
		this.query["label"] = o[1]
		this.query["authorizedContext"] = o[2]
		this.query["id"] = o[3]
		this.query["filterSummary"] = o[4]
		this.query["expansionTerms"] = o[5]
		this.query["saved"] = o[6]
		this.query["uuid"] = o[7]
		this.query["userInput"] = o[8]
		this.query["inputExpansion"] = o[9]
		this.query["distinctCounts"] = o[10]
	}
	 
	//Map registration = [:]	
	Map query = [:]	
	
}
