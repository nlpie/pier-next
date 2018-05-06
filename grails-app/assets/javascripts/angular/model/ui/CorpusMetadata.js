import AbstractHydrator from './AbstractHydrator';

class CorpusMetadata extends AbstractHydrator {
    
	constructor( obj ) {
		super( obj );
		this.aggregations = {};//TODO aggregationsByOntology ?
		this.appliedFilters = [];//TODO move to aggregation level?
		this.hydrateComplexNodes( obj );
	}
	
	hydrateComplexNodes( obj ) {
		//noop
	}
	
	

}

export default CorpusMetadata;

//iterable: for ( let value of [10, 20, 30] ) {}
//Object: for (var prop in obj) { obj[prop]; }
// if (obj.hasOwnProperty(prop)) {