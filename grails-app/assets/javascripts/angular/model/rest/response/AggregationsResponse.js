import DateRangeSlider from '../../ui/DateRangeSlider';

class AggregationsResponse {
    constructor( data, corpus ) {	//look into constructing from a data structure passed to constructor
    	this.total = data.hits.total;
    	this.took = data.took/1000 + "s";
    	console.info("aggs took " + this.took);
    	this.timedOut = data.timed_out;
    	this.aggs = undefined;
    	if ( this.total>0 ) {	//do not set if results
    		this.aggs = data.aggregations;
    		this.decorateCorpus( corpus );
    	}
    }
    
    clear() { }
    
    isEmpty() {
		return ( this.total==0 ) ? true : false;
	}
    
    decorateCorpus( corpus ) {
    	let me = this;
    	for ( let ontology of corpus.metadata.aggregations ) {
    		for ( let aggregation of ontology.aggregations ) {
    			if ( aggregation.isTemporal ) {
    				let minObj = me.aggs[aggregation.label+".min"];
    				let maxObj = me.aggs[aggregation.label+".max"];
    				if ( minObj && maxObj ) {
//alert( `${JSON.stringify(me.aggs[aggregation.label+".min"],null,'\t')} \n ${JSON.stringify(me.aggs[aggregation.label+".max"],null,'\t')}` );
	    				if ( !aggregation.initialSlider ) {
	    					//first, unfiltered return for this search, cache a DateRangeSlider for later use
	    					aggregation.initialSlider = new DateRangeSlider( minObj, maxObj );
	    				}
	    				aggregation.currentSlider = new DateRangeSlider( minObj, maxObj );
	    				//aggregation.currentSlider.filtered = true;
    				}
//alert(JSON.stringify(aggregation,null,'\t'));
    			}
    		}
    	}
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