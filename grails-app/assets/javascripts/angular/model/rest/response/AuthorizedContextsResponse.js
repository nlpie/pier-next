import AuthorizedContext from '../../ui/AuthorizedContext';

class AuthorizedContextsResponse extends Array {
	
	constructor( responseData ) {
		//responseData is a JSON array of objects (AuthorizedContext) from API
		super();
		for ( let obj of responseData ) {
			this.push( new AuthorizedContext( obj ) );
		}
//alert(JSON.stringify(this,null,'\t'));
	}

}

export default AuthorizedContextsResponse;

//iterable: for ( let value of [10, 20, 30] ) {}
//Object: for (var prop in obj) { obj[prop]; }
// if (obj.hasOwnProperty(prop)) {