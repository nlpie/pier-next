import TermsAggregation from './TermsAggregation'; 

class Aggregations {
	
	constructor( corpus ) {
		this.addAggregations( corpus );
	}
	
	add(label,agg) {
		this[label] = agg;
	}
	
	addAggregations( corpus ) {
		var me = this;
    	var aggregations = corpus.metadata.aggregations;
    	Object.keys(aggregations).map( function(key,index) {
    		var aggregationCategory = corpus.metadata.aggregations[key];
    		for (const aggregation of aggregationCategory) {
    			//alert(JSON.stringify(aggregation,null,'\t'));
    			me.add( aggregation.label, new TermsAggregation(aggregation.fieldName,	aggregation.numberOfFilterOptions) );
    		}
    		
    	} );
    }
}

export default Aggregations;

/*
{
    "aggs" : {
        "genres" : {
            "terms" : { "field" : "genre",
             			"size"  : 10
             			}
        }
    }
}
*/

//use https://www.elastic.co/guide/en/elasticsearch/reference/2.3/search-aggregations-bucket-filter-aggregation.html for AuthorizedContext filtering
//added aggregations are those filters that user has enabled in their preferences; should AND/OR filters exclude the filters complement? And should NOT filter be excluded?