import AbstractHydrator from './AbstractHydrator';

class RelatedMisspelling extends AbstractHydrator {
	
	constructor( obj ) {
		super( obj );
    }
	
}

export default RelatedMisspelling;

/*
{
    "term": "founf",
    "cosine_distance": 0.2137,
    "edit_distance": 1,
    "score": 0.2137
}
*/