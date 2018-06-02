import DocumentQuery from './DocumentQuery';

class PaginationQuery extends DocumentQuery {
    constructor( corpus, userInput ) {
    	super( corpus, userInput );
    	this.from = corpus.results.pagination.from;
    }
}

export default PaginationQuery;