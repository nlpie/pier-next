/**
 * 
 */
package edu.umn.nlpie.pier.api

import groovy.transform.InheritConstructors


@InheritConstructors
class HistorySummaryDTO {
	
	HistorySummaryDTO(Object o) {
		this.registration["id"] = o[0]
		this.registration["authorizedContext"] = o[2]
		this.query["label"] = o[1]
	}
	 
	Map registration = [:]	
	Map query = [:]	
	
}
