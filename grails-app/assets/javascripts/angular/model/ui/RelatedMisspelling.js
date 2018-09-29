import AbstractHydrator from './AbstractHydrator';

class RelatedMisspelling extends AbstractHydrator {
	
	constructor( obj ) {
		super( obj );
		this.on = false;	//default for checkbox behavior
    }
	
}

export default RelatedMisspelling;

/* data coming back from ES
{
	"term": "spark",
	"frequency": 10,
	"cosine_distance": 0.289,
	"edit_distance": 2,
	"score": -0.313
}
*/