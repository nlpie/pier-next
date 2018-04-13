import TermsAggregation from './TermsAggregation';
import MinAggregation from './MinAggregation';
import MaxAggregation from './MaxAggregation';

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
		Object.keys(aggregations).map( function(key,index) {
    		var aggregationCategory = aggregations[key];
    		Object.keys( aggregationCategory ).map( function(agg,index) {
    			var aggregation = aggregationCategory[agg];
    			if ( aggregation.isTemporal ) {
    				//alert("temporal");
    				me.add( aggregation.label+'.min', new MinAggregation( aggregation.field.fieldName ) );
    				me.add( aggregation.label+'.max', new MaxAggregation( aggregation.field.fieldName ) );
    			} else {
    				me.add( aggregation.label, new TermsAggregation( aggregation.field.fieldName, aggregation.numberOfFilterOptions ) );
    			}
    		});
    	});
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