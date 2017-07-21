
class AggregationsResponse {
    constructor(data) {	//look into constructing from a data structure passed to constructor
    	this.total = data.hits.total;
    	this.took = data.took/1000 + "s";
    	console.info("aggs took " + this.took);
    	this.timedOut = data.timed_out;
    	this.aggs = undefined;
    	if ( this.total>0 ) {	//do not set if results
    		this.aggs = data.aggregations;
    	}
    }
    
    clear() { 
		
	}
}

export default AggregationsResponse;