import AbstractHydrator from './AbstractHydrator';
import Field from './Field';
import AggregationStatus from './AggregationStatus';

class Aggregation extends AbstractHydrator {
    
	constructor( obj ) {
		super( obj );
		this.filters = {};
		this.min = null;
		this.max = null;
		this.count = null;
		this.status = new AggregationStatus();
		this.field = undefined;
		this.hydrateObjectProperties( obj );
//alert(JSON.stringify(this,null,'\t'));
	}
	
	hydrateObjectProperties( obj ) {
		let complexNodes = [ "field" ];
		for ( let prop in obj ) {
			let objType = typeof( obj[prop] );
			switch ( objType ) {
				case "object":
					if ( obj[prop]!=null && prop=="field" ) {
						let field = obj[prop];
						this.field = new Field( obj[prop] )
					}
					break;
				default:
			}
		}
	}
	
}

export default Aggregation;

//iterable: for ( let value of [10, 20, 30] ) {}
//Object: for (var prop in obj) { obj[prop]; }
// if (obj.hasOwnProperty(prop)) {