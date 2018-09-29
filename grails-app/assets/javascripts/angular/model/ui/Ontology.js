import AbstractHydrator from './AbstractHydrator';
import Aggregation from './Aggregation';

class Ontology extends AbstractHydrator {
    
	//pass in response.data object from $http call
	constructor( obj ) {
		super( obj );
		this.aggregations = [];
		this.hydrateObjectProperties( obj );
	}
	
	hydrateObjectProperties( obj ) {
		let complexNodes = [ "aggregations" ];
		for ( let prop in obj ) {
			let objType = typeof( obj[prop] );
			switch ( objType ) {
				case "object":
					if ( obj[prop]!=null && prop=="aggregations" ) {
						let aggregations = obj[prop];
						for ( let a of aggregations ) {
							this.aggregations.push( new Aggregation( a ) );
						}
					}
					break;
				default:
			}
		}
	}
	
}

export default Ontology;

//iterable: for ( let value of [10, 20, 30] ) {}
//Object: for (var prop in obj) { obj[prop]; }
// if (obj.hasOwnProperty(prop)) {

/*
{
	"id": 24,
	"label": "Encounter Center",
	"displayOrder": 10,
	"numberOfFilterOptions": 5,
	"isTemporal": false,
	"isNumeric": false,
	"countDistinct": false,
	"aggregate": true,
	"export": false,
	"filters": {},
	"min": null,
	"max": null,
	"count": null,
	"field": {
		"id": 3,
		"fieldName": "encounter_center",
		"aggregatable": true,
		"significantTermsAggregatable": false,
		"exportable": true,
		"contextFilterField": false,
		"dataTypeName": "NOT_ANALYZED_STRING",
		"description": "Encounter center name in Epic"
	}
}
*/