import BaseQuery from './BaseQuery';
import Highlight from './Highlight';

class DocumentQuery extends BaseQuery {
    constructor( corpus, userInput, pagination ) {
    	super( corpus, userInput );
    	this.highlight = new Highlight(corpus.defaultSearchField);
    	this.size = corpus.pagination.notesPerPage;
    	this.from = corpus.pagination.offset;
    }
}

export default DocumentQuery;