import AbstractHydrator from './AbstractHydrator';

class RelatedTerm extends AbstractHydrator {
	
	constructor( obj ) {
		super( obj );
    }
	
}

export default RelatedTerm;

/*
{
    "term": "discovered",
    "cosine_distance": 0.6316
}
*/