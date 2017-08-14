import TermsAggregation from './TermsAggregation'; 

class Aggregations {
	
	constructor( corpus ) {
		if ( corpus===undefined ) {
			//noop - do not set configured aggs for corpus
		} else {			
			this.addAggregations( corpus );
		}
	}
	
	add(label,agg) {
		this[label] = agg;
	}
	
	addAggregations( corpus ) {
		var me = this;
    	var aggregations = corpus.metadata.aggregations;
    	//console.info(JSON.stringify(aggregations,null,'\t'));
    	Object.keys(aggregations).map( function(key,index) {
    		var aggregationCategory = corpus.metadata.aggregations[key];
    		for (const aggregation of aggregationCategory) {
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