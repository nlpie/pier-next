import AbstractHydrator from './AbstractHydrator';
import Corpora from './Corpora';

class AuthorizedContext extends AbstractHydrator {
    
	//pass in response.data object from $http call
	constructor( obj ) {
		super( obj );
		this.hydrateComplexNodes( obj );
//alert(JSON.stringify(this,null,'\t'));
	}
	
	hydrateComplexNodes( obj ) {
		let complexNodes = [ "copora" ];
		for ( let prop in obj ) {
			let objType = typeof( obj[prop] );
			switch ( objType ) {
				case "object":
					if ( obj[prop]!=null && prop=="corpora" ) {
						this[prop] = new Corpora( obj[prop] );	//obj[prop];
					}
					break;
				default:
			}
		}
	}

}

export default AuthorizedContext;

//iterable: for ( let value of [10, 20, 30] ) {}
//Object: for (var prop in obj) { obj[prop]; }
// if (obj.hasOwnProperty(prop)) {