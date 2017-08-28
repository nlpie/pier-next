import BaseQuery from './BaseQuery';
import Aggregations from './Aggregations';
import TermsAggregation from './TermsAggregation';
import CardinalityAggregation from './CardinalityAggregation';
import MaxAggregation from './MaxAggregation';

class DistinctValuesEstimationQuery extends BaseQuery {
    constructor( corpus, userInput, label, fieldName, maxBuckets, countType ) {
    	super( corpus, userInput );
    	this.aggs = new Aggregations();
    	if ( countType=="combined" ) this.aggs.add( label, new TermsAggregation(fieldName, maxBuckets) )
    	this.aggs.add( label + ' Cardinality Estimate', new CardinalityAggregation(fieldName) )
    	this.size = 0;
    }
}

export default DistinctValuesEstimationQuery;