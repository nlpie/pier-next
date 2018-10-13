import AbstractHydrator from './AbstractHydrator';
import CorpusMetadata from './CorpusMetadata';
import CorpusStatus from './CorpusStatus';
import Results from './Results';

class Corpus extends AbstractHydrator {
    
	//pass in response.data object from $http call
	constructor( obj ) {
		super( obj );
		this.status = new CorpusStatus();
		this.results = new Results();
		this.hydrateObjectProperties( obj );
		this.currentFilterSummary = undefined;
	}
	
	hydrateObjectProperties( obj ) {
		let complexNodes = [ "metadata" ];
		for ( let prop in obj ) {
			let objType = typeof( obj[prop] );
			switch ( objType ) {
				case "object":
					if ( obj[prop]!=null && prop=="metadata" ) {
						this[prop] = new CorpusMetadata( obj[prop] );
					}
					break;
				default:
			}
		}
	}
	
	dim() {
		this.status.opacity = { "opacity": 0.2 };
		this.status.dirty = true;
	}

	brighten() {
		this.status.opacity = { "opacity": 1 };
		this.status.dirty = false;
	}
	
	removeFilters() {
		for ( let ontology of this.metadata.aggregations ) {
    		for ( let aggregation of ontology.aggregations ) {
    			if ( !( JSON.stringify(aggregation.filters) === JSON.stringify({}) ) ) {
	    			aggregation.filters = {};
    			}
        	}
    	}
		this.status.inactivateFilter();
    	this.status.dirty = true;
    	this.updateFilterSummary();
	}
	
	removeCounts() {
		if ( !( JSON.stringify(this.metadata.aggregations) === JSON.stringify({}) ) ) {
			for ( let ontology of this.metadata.aggregations ) {
	    		for ( let aggregation of ontology.aggregations ) {
	    			if ( aggregation.count || aggregation.cardinalityEstimate ) {
		    			aggregation.count = undefined;
		    			aggregation.cardinalityEstimate = undefined;
	    			}
	        	}
	    	}
		}
	}
	
	parsePastQueryFilterArray ( queryFilterArray ) {
//alert("filters queryFilterArray\n" + JSON.stringify(queryFilterArray,null,'\t'));
//alert("filters metadata aggregations\n" + JSON.stringify(this.metadata,null,'\t'));
		for ( let ontology of this.metadata.aggregations ) {
    		for ( let aggregation of ontology.aggregations ) {
    			for ( let filter of queryFilterArray ) {
    				for ( let should of filter.bool.should ) {
    					if ( should.term[aggregation.field.fieldName] ) {
    						//console.log( should );
    						aggregation.filters[ should.term[aggregation.field.fieldName] ] = true;
//alert( JSON.stringify(should) + "\n" + JSON.stringify(aggregation,null,'\t') );
    					}
    				}
    				
    			}
    			//aggregation
    			/*{
					"id": 54,
					"label": "Encounter Clinic Type",
					"displayOrder": 10,
					"numberOfFilterOptions": 5,
					"isTemporal": false,
					"isNumeric": false,
					"countDistinct": false,
					"aggregate": true,
					"export": false,
					"filters": {
						"Primary Care": true,
						"UMP": false,
						"Sports and Orthopedics": true
					},
					"min": null,
					"max": null,
					"count": null,
					"status": {
						"computingCounts": false
					},
					"field": {
						"id": 2,
						"fieldName": "encounter_clinic_type",
						"aggregatable": true,
						"significantTermsAggregatable": false,
						"exportable": true,
						"contextFilterField": false,
						"dataTypeName": "NOT_ANALYZED_STRING",
						"description": "Clinic type in Epic"
					}
				}*/
    			
    			//queryFilterArray
    			/*[
    				{
    					"bool": {
    						"filter": [],
    						"should": [
    							{
    								"term": {
    									"encounter_clinic_type": "Primary Care"
    								}
    							},
    							{
    								"term": {
    									"encounter_clinic_type": "Sports and Orthopedics"
    								}
    							}
    						],
    						"must_not": [],
    						"must": []
    					}
    				}
    			]*/
    			
        	}
    	}
		this.status.filter.on = true;
    	this.status.showBan = true;
    	this.status.dirty = true;
    	this.updateFilterSummary();
	}
	
	isDirty() {
		return this.status.dirty;
	}
	
	prepare() {
		this.results = new Results();
	}
	
	updateFilterSummary() {
		//alert("discrete");
    	let summary = [];
    	this.status.filter.on = false;
    	for ( let ontology of this.metadata.aggregations ) {
    		for ( let aggregation of ontology.aggregations ) {
    			if ( !( JSON.stringify(aggregation.filters) === JSON.stringify({}) ) && !aggregation.isTemporal ) {
    				//potential fields to be added
    				let filter = undefined;	////defer assignment until proof of need is established
	    			Object.keys( aggregation.filters ).map( function(value,i) {
	    				if (aggregation.filters[value]==true) summary.push( value );
	    			});
    			}
        	}
    	}
    	console.log(summary);
    	this.currentFilterSummary = summary.join(", ");
	}

}

export default Corpus;

//iterable: for ( let value of [10, 20, 30] ) {}
//Object: for (var prop in obj) { obj[prop]; }
// if (obj.hasOwnProperty(prop)) {