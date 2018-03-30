
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
/* data var in constructor has this form
{
	"_shards": {
		"total": 6,
		"failed": 0,
		"successful": 6
	},
	"hits": {
		"hits": [],
		"total": 62821,
		"max_score": 0
	},
	"took": 243,
	"timed_out": false,
	"aggregations": {
		"Medical Concepts": {
			"doc_count_error_upper_bound": 1929,
			"sum_other_doc_count": 2019534,
			"buckets": [
				{
					"doc_count": 62752,
					"key": "C0030705"
				},
				{
					"doc_count": 62745,
					"key": "C1299487"
				},
				...
*/