import Query from './Query';

class ExportQuery extends Query {
    constructor( corpus, userInput ) {
    	super( corpus, userInput );
    	this.size = 500;
    	this.fields = [];
    }
}

export default ExportQuery;