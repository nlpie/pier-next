import AbstractHydrator from './AbstractHydrator';
import CorpusAggregationsResponse from '../rest/response/CorpusAggregationsResponse'; 

class CorpusMetadata extends AbstractHydrator {
    
	constructor( obj ) {
		super( obj );
		this.aggregations = [];
		this.hydrateObjectProperties( obj );
	}
	
	hydrateObjectProperties( obj ) {
		//noop
	}

}

export default CorpusMetadata;

//iterable: for ( let value of [10, 20, 30] ) {}
//Object: for (var prop in obj) { obj[prop]; }
// if (obj.hasOwnProperty(prop)) {