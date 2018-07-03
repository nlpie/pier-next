
class SearchPayload extends AbstractHydrator {
    
	//pass in existing Search instance OR response.data object from $http call
	constructor( search ) {
		
	}

}

export default SearchPayload;

//iterable: for ( let value of [10, 20, 30] ) {}
//Object: for (var prop in obj) { obj[prop]; }
// if (obj.hasOwnProperty(prop)) {