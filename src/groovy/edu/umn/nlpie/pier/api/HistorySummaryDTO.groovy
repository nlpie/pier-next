/**
 * 
 */
package edu.umn.nlpie.pier.api

import groovy.transform.InheritConstructors


@InheritConstructors
class HistorySummaryDTO {
	
	HistorySummaryDTO( Object o ) {
		this.registration["id"] = o[0]
		this.query["label"] = o[1]
		this.registration["authorizedContext"] = o[2]
		this.query["id"] = o[3]
		this.query["filters"] = o[4]
		this.query["saved"] = o[5]
	}
	 
	Map registration = [:]	
	Map query = [:]	
	
}
