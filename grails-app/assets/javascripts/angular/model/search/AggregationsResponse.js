import DateRangeSlider from '../ui/DateRangeSlider';

class AggregationsResponse {
    constructor( data, corpus ) {	//look into constructing from a data structure passed to constructor
    	this.total = data.hits.total;
    	this.took = data.took/1000 + "s";
    	console.info("aggs took " + this.took);
    	this.timedOut = data.timed_out;
    	this.aggs = undefined;
    	if ( this.total>0 ) {	//do not set if results
    		this.aggs = data.aggregations;
    	}
    	this.decorateCorpus( corpus );
    }
    
    clear() { }
    
    decorateCorpus( corpus ) {
    	let me = this;
    	let aggregations = corpus.metadata.aggregations;
		Object.keys(aggregations).map( function(key,index) {
    		let aggregationCategory = aggregations[key];
    		Object.keys( aggregationCategory ).map( function(agg,index) {
    			let aggregation = aggregationCategory[agg];
    			if ( aggregation.isTemporal ) {
//alert(JSON.stringify(me.aggs[aggregation.label+".min"],null,'\t'));
    				if ( !aggregation.initialSlider ) {
    					//first, unfiltered return for this search, cache a DateRangeSlider for later use
    					aggregation.initialSlider = new DateRangeSlider( me.aggs[aggregation.label+".min"], me.aggs[aggregation.label+".max"] );
    				}
    				aggregation.currentSlider = new DateRangeSlider( me.aggs[aggregation.label+".min"], me.aggs[aggregation.label+".max"], aggregation.initialSlider );
//alert(JSON.stringify(aggregation,null,'\t'));
    			}
    		});
    	});
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