import AbstractHydrator from './AbstractHydrator';

class RelatedTerm extends AbstractHydrator {
	
	constructor( obj ) {
		super( obj );
    }
	
}

export default RelatedTerm;

/*
{
	"term": "spark",
	"frequency": 64,
	"cosine_distance": 0.7042
}
*/