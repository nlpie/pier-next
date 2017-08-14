import BaseQuery from './BaseQuery';
import Aggregations from './Aggregations';
import TermsAggregation from './TermsAggregation';
import MaxAggregation from './MaxAggregation';

class DistinctValuesEstimationQuery extends BaseQuery {
    constructor( corpus, userInput, label, fieldName, maxBuckets ) {
    	super( corpus, userInput );
    	this.aggs = new Aggregations();
    	this.aggs.add( label, new TermsAggregation(fieldName, maxBuckets) )
    	this.size = 0;
    }
}

export default DistinctValuesEstimationQuery;