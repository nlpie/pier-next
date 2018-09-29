import AbstractHydrator from './AbstractHydrator';

class RelatedTerm extends AbstractHydrator {
	
	constructor( obj ) {
		super( obj );
		this.on = false;	//default for checkbox behavior
    }
	
}

export default RelatedTerm;

/* data coming back from ES
{
	"term": "spark",
	"frequency": 64,
	"cosine_distance": 0.7042
}
*/