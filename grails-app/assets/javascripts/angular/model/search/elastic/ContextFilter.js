//TOFO: rename to TermFilter
class ContextFilter {
    
	//instances of this class are pushed onto the filter array +/- scalar filters in the filter clause of an elastic query (see example in Search.js
	constructor(contextFilterField, contextFilterValue) {
		var filter = {};
		filter[contextFilterField] = contextFilterValue;
    	this.term = filter;
    }

}

/*
{ "term": {"search_context": "Melton-MeauxG-Req00277" }}
*/
export default ContextFilter;