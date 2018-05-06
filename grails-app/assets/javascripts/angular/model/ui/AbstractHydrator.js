
class AbstractHydrator {
    
	//pass in response.data object from $http call
	constructor( obj ) {
		for ( let prop in obj ) {
			let objType = typeof(obj[prop]);
			switch ( objType ) {
				case "string":
				case "number":
				case "boolean":
				case "undefined":
				case null:
//alert( prop + " (" + typeof(obj[prop]) + "): " + JSON.stringify(obj[prop]) );
					this[prop] = obj[prop];
					break;
				case "object":
					if ( obj[prop]==null ) {
						this[prop] = obj[prop];
					}
					break;
				default:
			}
		}
	}
	
	hydrateComplexNodes( prop, obj ) {
		alert("super hydrate");
		//override in subclasses
	}

}


export default AbstractHydrator;

//iterable: for ( let value of [10, 20, 30] ) {}
//Object: for (var prop in obj) { obj[prop]; }
// if (obj.hasOwnProperty(prop)) {