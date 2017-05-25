
class SearchResponse {
    constructor(data) {	//look into constructing from a data structure passed to constructor
    	this.hits = data.hits.hits;
    	this.total = data.hits.total;
    	this.took = data.took/1000 + "s";
    	this.timedOut = data.timed_out;
    }
    
    clear() { 
		
	}
}

export default SearchResponse;