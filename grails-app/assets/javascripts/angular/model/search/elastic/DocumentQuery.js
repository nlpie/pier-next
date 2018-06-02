import Query from './Query';
import Highlight from './Highlight';

class DocumentQuery extends Query {
    constructor( corpus, userInput ) {
    	super( corpus, userInput );
    	this.highlight = new Highlight(corpus.metadata.defaultSearchField);
    	this.size = corpus.results.pagination.notesPerPage;
    }
}

export default DocumentQuery;