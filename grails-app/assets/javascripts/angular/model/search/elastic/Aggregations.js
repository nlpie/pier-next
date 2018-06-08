import TermsAggregation from './TermsAggregation';
import SignificantTermsAggregation from './SignificantTermsAggregation';
import MinAggregation from './MinAggregation';
import MaxAggregation from './MaxAggregation';

//this class puts aggregations on AggregationQuery using agg data stored in the corpus param passed to constructor
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
//alert(JSON.stringify(corpus,null,'\t'));
		var me = this;
    	//var aggregations = corpus.metadata.aggregations;
    	for ( let ontology of corpus.metadata.aggregations ) {
    		for ( let aggregation of ontology.aggregations ) {
    			if ( aggregation.isTemporal ) {
    				//alert("temporal");
    				me.add( aggregation.label+'.min', new MinAggregation( aggregation.field.fieldName ) );
    				me.add( aggregation.label+'.max', new MaxAggregation( aggregation.field.fieldName ) );
    			} else if ( aggregation.field.significantTermsAggregatable ) {
    				me.add( aggregation.label, new SignificantTermsAggregation( aggregation.field.fieldName, aggregation.numberOfFilterOptions ) );
    			} else {
    				me.add( aggregation.label, new TermsAggregation( aggregation.field.fieldName, aggregation.numberOfFilterOptions ) );
    			}
    		}
    	}
    }
	
	addAggregations_orig( corpus ) {
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
    			} else if ( aggregation.field.significantTermsAggregatable ) {
    				me.add( aggregation.label, new SignificantTermsAggregation( aggregation.field.fieldName, aggregation.numberOfFilterOptions ) );
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