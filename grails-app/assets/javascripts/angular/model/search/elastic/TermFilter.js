
class TermFilter {
    
	//instances of this class are pushed onto the filter array +/- scalar filters in the filter clause of an elastic query (see example in Search.js
	constructor(field, value) {
		var filter = {};
		filter[field] = value;
    	this.term = filter;
    }

}

/*
{ "term": {"authorized_context": "Melton-MeauxG-Req00277" }}	//was search_context
*/
export default TermFilter;