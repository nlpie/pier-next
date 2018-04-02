import Query from './Query';
import Aggregations from './Aggregations';
import CardinalityAggregation from './CardinalityAggregation';

class CardinalityOnlyQuery extends Query {
    constructor( corpus, userInput, label, fieldName ) {
    	super( corpus, userInput );
    	this.aggs = new Aggregations();
    	//include cardinality estimate
    	this.aggs.add( label + ' Cardinality Estimate', new CardinalityAggregation( fieldName ) )
    	this.size = 0;
    }
}

export default CardinalityOnlyQuery;