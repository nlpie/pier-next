import AbstractHydrator from './AbstractHydrator';

class Field extends AbstractHydrator {
    
	constructor( obj ) {
		super( obj );
		this.hydrateObjectProperties( obj );
	}
	
	hydrateObjectProperties( obj ) {
		//noop
	}
	
}

export default Field;

//iterable: for ( let value of [10, 20, 30] ) {}
//Object: for (var prop in obj) { obj[prop]; }
// if (obj.hasOwnProperty(prop)) {