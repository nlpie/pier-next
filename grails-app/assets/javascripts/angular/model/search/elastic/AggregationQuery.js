import BaseQuery from './BaseQuery';
import Aggregations from './Aggregations';
import MinAggregation from './MinAggregation';
import MaxAggregation from './MaxAggregation';

class AggregationQuery extends BaseQuery {
    constructor( corpus, userInput ) {
    	super( corpus, userInput );
    	this.aggs = new Aggregations( corpus );
    	this.size = 0;
    	
    }
}

export default AggregationQuery;