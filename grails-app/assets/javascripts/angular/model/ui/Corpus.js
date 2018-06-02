import AbstractHydrator from './AbstractHydrator';
import CorpusMetadata from './CorpusMetadata';
import CorpusStatus from './CorpusStatus';
import Results from './Results';

class Corpus extends AbstractHydrator {
    
	//pass in response.data object from $http call
	constructor( obj ) {
		super( obj );
		this.status = new CorpusStatus();
		this.results = new Results();
		this.hydrateObjectProperties( obj );
//alert(JSON.stringify(this,null,'\t'));
	}
	
	hydrateObjectProperties( obj ) {
		let complexNodes = [ "metadata" ];
		for ( let prop in obj ) {
			let objType = typeof( obj[prop] );
			switch ( objType ) {
				case "object":
					if ( obj[prop]!=null && prop=="metadata" ) {
						this[prop] = new CorpusMetadata( obj[prop] );
					}
					break;
				default:
			}
		}
	}
	
	dim() {
		this.status.opacity = { "opacity": 0.2 };
		this.status.dirty = true;
	}

	brighten() {
		this.status.opacity = { "opacity": 1 };
		this.status.dirty = false;
	}
	
	removeFilters() {
		let me = this;
		for ( let ontology of this.metadata.aggregations ) {
    		for ( let aggregation of ontology.aggregations ) {
    			if ( !( JSON.stringify(aggregation.filters) === JSON.stringify({}) ) ) {
	    			aggregation.filters = {};
    			}
        	}
    	}
		this.status.userSelectedFilters = false;
    	this.status.showBan = false;
    	this.status.dirty = true;
	}
	
	isDirty() {
		return this.status.dirty;
	}
	
	prepare() {
		this.results = new Results();
	}

}

export default Corpus;

//iterable: for ( let value of [10, 20, 30] ) {}
//Object: for (var prop in obj) { obj[prop]; }
// if (obj.hasOwnProperty(prop)) {