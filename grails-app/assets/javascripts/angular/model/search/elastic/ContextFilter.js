
class ContextFilter {
    
	//instances of this class are pushed onto the filter array +/- scalar filters in the filter clause of an elastic query (see example in Search.js
	constructor(contextFilterField, contextFilterValue) {
    	this.term = {
    		contextFilterField: contextFilterValue
    	}
    }

}

/*
{
    "term": {
      "status": "normal" 
    }
}
*/
export default ContextFilter;