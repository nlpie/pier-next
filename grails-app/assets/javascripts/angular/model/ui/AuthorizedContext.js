import AbstractHydrator from './AbstractHydrator';
import Corpus from './Corpus';

class AuthorizedContext extends AbstractHydrator {
    
	//pass in response.data object from $http call
	constructor( obj ) {
		super( obj );
		this.corpus = undefined;
		this.hydrateObjectProperties( obj );
//alert(JSON.stringify(this,null,'\t'));
	}
	
	hydrateObjectProperties( obj ) {
		let complexNodes = [ "corpus" ];	//not used, but lists the complex nodes expected
		for ( let prop in obj ) {
			let objType = typeof( obj[prop] );
			switch ( objType ) {
				case "object":
					if ( obj[prop]!=null && prop=="corpus" ) {
						this.corpus = new Corpus( obj[prop] );
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