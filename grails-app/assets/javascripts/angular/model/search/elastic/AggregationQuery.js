import Query from './Query';
import Aggregations from './Aggregations';
import MinAggregation from './MinAggregation';
import MaxAggregation from './MaxAggregation';

class AggregationQuery extends Query {
    constructor( corpus, userInput ) {
    	super( corpus, userInput );
    	this.aggs = new Aggregations( corpus );
    	this.size = 0;
    }
}

export default AggregationQuery;