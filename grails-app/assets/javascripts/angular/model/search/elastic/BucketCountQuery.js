import BaseQuery from './BaseQuery';
import Aggregations from './Aggregations';
import TermsAggregation from './TermsAggregation';
import CardinalityAggregation from './CardinalityAggregation';
import MaxAggregation from './MaxAggregation';

class BucketCountQuery extends BaseQuery {
    constructor( corpus, userInput, label, fieldName, maxCount ) {
    	super( corpus, userInput );
    	this.aggs = new Aggregations();
    	this.aggs.add( label, new TermsAggregation( fieldName, maxCount ) );
    	//include cardinality estimate
    	this.aggs.add( label + ' Cardinality Estimate', new CardinalityAggregation( fieldName ) )
    	this.size = 0;
    }
}

export default BucketCountQuery;