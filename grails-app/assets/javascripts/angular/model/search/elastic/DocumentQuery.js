import Query from './Query';
import Highlight from './Highlight';

class DocumentQuery extends Query {
    constructor( corpus, userInput ) {
    	super( corpus, userInput );
    	this.highlight = new Highlight(corpus.metadata.defaultSearchField);
    	//alert(JSON.stringify(corpus.pagination,null,'\t'));
    	this.size = corpus.pagination.notesPerPage;
    	//this.from = corpus.pagination.from;
    }
}

export default DocumentQuery;