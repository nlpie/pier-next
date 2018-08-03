import AbstractHydrator from './AbstractHydrator';

class RelatedMisspelling extends AbstractHydrator {
	
	constructor( obj ) {
		super( obj );
    }
	
}

export default RelatedMisspelling;

/*
{
	"term": "spark",
	"frequency": 10,
	"cosine_distance": 0.289,
	"edit_distance": 2,
	"score": -0.313
}
*/