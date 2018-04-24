import AggregationQuery from '../AggregationQuery';
import Aggregations from '../Aggregations';
import TermsAggregation from '../TermsAggregation';
import TermFilter from '../TermFilter';

class EncounterAggQuery extends AggregationQuery {
    constructor( corpus, userInput, serviceId ) {
    	super( corpus, userInput );
    	this.addFilter( new TermFilter( "service_id", serviceId ));
    	this.aggs = new Aggregations( corpus );
    	this.aggs.add( "Service Id", new TermsAggregation( "service_id", 5 ) );
    	this.size = 0;
    }
}

export default EncounterAggQuery;