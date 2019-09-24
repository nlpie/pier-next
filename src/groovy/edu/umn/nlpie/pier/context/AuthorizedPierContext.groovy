
package edu.umn.nlpie.pier.context

public interface AuthorizedPierContext {
	//marker interface for search context implementations
	Long requestId
	String label
	String description
	String corpusName
	Boolean filteredContext
	Long contextFilterValue	//filter value used in ES for note sets
	
}