
class AbstractResponseData {
    
	//pass in response.data object from $http call
	constructor( dataObject ) {
		for ( let prop in dataObject ) {
			let objType = typeof(dataObject[prop]);
			switch ( objType ) {
				case "string":
				case "number":
				case "boolean":
				case "undefined":
				case null:
//alert( prop + " (" + typeof(dataObject[prop]) + "): " + JSON.stringify(dataObject[prop]) );
					this[prop] = dataObject[prop];
					break;
				case "object":
					if ( dataObject[prop]==null ) {
						this[prop] = dataObject[prop];
					} else {
alert("need to hydrate: " + prop );
					}
					break;
				default:
			}
			//assign to this[propertyName]
		}
	}
	
	hydrateComplexNodes( prop, obj ) {
		alert("super hydrate");
		//override in subclasses
	}

}


export default AbstractResponseData;

//iterable: for ( let value of [10, 20, 30] ) {}
//Object: for (var prop in obj) { obj[prop]; }
// if (obj.hasOwnProperty(prop)) {