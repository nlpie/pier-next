import AbstractHydrator from './AbstractHydrator';
import CorpusMetadata from './CorpusMetadata';
import CorpusStatus from './CorpusStatus';
import Results from './Results';
import DateRangeSlider from './DateRangeSlider';

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
    			if ( !( JSON.stringify(aggregation.filters) == JSON.stringify({}) ) ) {
	    			aggregation.filters = {};
    			}
    			if ( aggregation.isTemporal ) {
    				aggregation.resetSlider();
    			}
        	}
    	}
		this.status.inactivateFilter();
    	this.status.dirty = true;
    	this.updateUiFilterInfo();
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
//console.log("Corpus.parsePastQueryFilterArray");
//alert("QUERY FILTER ARRAY\n" + JSON.stringify(queryFilterArray,null,'\t'));
//alert("filters metadata aggregations\n" + JSON.stringify(this.metadata,null,'\t'));
//console.info("ONTOLOGY AGGREGATIONS\n" + JSON.stringify(this.metadata.aggregations,null,'\t'));
		for ( let ontology of this.metadata.aggregations ) {
    		for ( let aggregation of ontology.aggregations ) {
    			for ( let filterObject of queryFilterArray ) {
					if ( filterObject.bool ) {
						for ( let should of filterObject.bool.should ) {
							if ( should.term[aggregation.field.fieldName] ) {
//alert("BOOL:\n"+JSON.stringify(filterObject,null,'\t')+"\n"+filterObject.bool.should.length);
								aggregation.filters[ should.term[aggregation.field.fieldName] ] = true;
							}
						}
    				}
					if ( filterObject.range ) {
						let rangeObject = filterObject.range;
						if ( rangeObject.hasOwnProperty( aggregation.field.fieldName ) ) {
//alert(`Corpus.pastQueryFilterArray: AGGREGATION OBJ\n${JSON.stringify(aggregation,null,'\t')}`);
//alert(`Corpus.pastQueryFilterArray: RANGE OBJ\n${JSON.stringify(rangeObject,null,'\t')}`);
							//these are listed separately - no OR (should semantics)\
							aggregation.filters['min'] = rangeObject[aggregation.field.fieldName].gte;
							aggregation.filters['max'] = rangeObject[aggregation.field.fieldName].lte;
//alert(`Corpus.pastQueryFilterArray: AGGREGATION FILTERS\n${JSON.stringify(aggregation.filters,null,'\t')}`);
							//NEED TO ASSIGN INITIAL SLIDER
							aggregation.initialSlider = new DateRangeSlider( rangeObject[aggregation.field.fieldName].gte, rangeObject[aggregation.field.fieldName].lte );
							aggregation.initialSlider.filtered = true;
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
    	this.updateUiFilterInfo();
	}
	
	isDirty() {
		return this.status.dirty;
	}
	
	prepare() {
		this.results = new Results();
	}
	
	updateUiFilterInfo() {
//console.log(`Corpus.updateUiFilterInfo:AGGREGATIONS\n${JSON.stringify(this.metadata.aggregations,null,'\t')}`);
    	let summary = [];
    	let me = this;
    	me.status.inactivateFilter(); //turn off filtered indication, if there is a match then it will be turned back on
    	for ( let ontology of this.metadata.aggregations ) {
    		for ( let aggregation of ontology.aggregations ) {
    			if ( !( JSON.stringify(aggregation.filters) === JSON.stringify({}) ) && !aggregation.isTemportal ) {
//console.log(`Corpus.updateUiFilterInfo:AGGREGATIONS[${aggregation.label}]\n${JSON.stringify(aggregation,null,'\t')}`);
    				//let filter = undefined;	////defer assignment until proof of need is established
	    			Object.keys( aggregation.filters ).map( function(value,i) {
	    				//discrete filter values handled here
//alert(`${aggregation.label}\n${JSON.stringify(value)}`);
	    				if (aggregation.filters[value]==true) {
	    					summary.push( value );
	    					me.status.activateFilter();
	    					/*if ( aggregation.isTemporal ) {
	    						alert("not here!");
	    						aggregation.currentSlider.filtered = true;
	    					}*/
	    				}
	    			});
    			}
    			//if ( !( JSON.stringify(aggregation.filters) === JSON.stringify({}) ) && aggregation.isTemporal ) {
    			if ( aggregation.isTemporal && aggregation.isActive() ) {
	    			//range filters for dates handled here
//alert(`${aggregation.label}\n${JSON.stringify(aggregation.filters)}`);
					let min = new Date( aggregation.filters.min ).toLocaleDateString("en-US");
					let max = new Date( aggregation.filters.max ).toLocaleDateString("en-US");
					let rangeSummary = `${aggregation.label}:${min}-${max}`;
					summary.push( rangeSummary );
					me.status.activateFilter();
    			}
        	}
    	}
//console.log(`Corpus.updateUiFilterInfo:\n${JSON.stringify(summary,null,'\t')}`);
    	this.currentFilterSummary = summary.join(", ");
	}

}

export default Corpus;

//iterable: for ( let value of [10, 20, 30] ) {}
//Object: for (var prop in obj) { obj[prop]; }
// if (obj.hasOwnProperty(prop)) {