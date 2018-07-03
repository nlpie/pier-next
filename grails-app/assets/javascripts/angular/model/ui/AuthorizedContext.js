import AbstractHydrator from './AbstractHydrator';
import Corpus from './Corpus';

class AuthorizedContext extends AbstractHydrator {
    
	//pass in response.data object from $http call
	constructor( obj ) {
		super( obj );
		this.corpora = [];
		this.hydrateObjectProperties( obj );
//alert(JSON.stringify(this,null,'\t'));
	}
	
	hydrateObjectProperties( obj ) {
		let complexNodes = [ "corpora" ];
		for ( let prop in obj ) {
			let objType = typeof( obj[prop] );
			switch ( objType ) {
				case "object":
					if ( obj[prop]!=null && prop=="corpora" ) {
						let corpora = obj[prop];
						for ( let c of corpora ) {
							this.corpora.push( new Corpus( c ) );
						}
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