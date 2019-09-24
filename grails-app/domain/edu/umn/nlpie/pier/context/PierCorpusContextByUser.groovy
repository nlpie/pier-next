package edu.umn.nlpie.pier.context

import edu.umn.nlpie.pier.api.CorpusMetadata
import edu.umn.nlpie.pier.ui.Corpus


class PierCorpusContextByUser extends AbstractPierContext implements AuthorizedPierContext {

	
	static mapping = {
		datasource 'notes'
		//table name: "unioned_auth_contexts_by_user", schema: "notes"	//view in notes schema
		table name: "pier_corpus_context_by_user", schema: "notes"	//view in notes schema
		corpusName column: 'corpus'
		contextFilterValue column: 'filter_value'
		version false
	}
	
	String username
	Long userId
	
}