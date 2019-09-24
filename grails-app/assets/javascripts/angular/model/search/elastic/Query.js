import BoolQuery from './BoolQuery';
import QuerystringQuery from './QuerystringQuery';
import TermFilter from './TermFilter';
import RangeFilter from './RangeFilter';

//do not instantiate, use subclasses instead
class Query {
    constructor( corpus, userInput ) {
    	this.query = new BoolQuery();
    	this.setContextFilter( corpus );
    	this.setTextQuery( corpus, userInput );
    	this.setDiscreteValueFilters( corpus );
    	this.setRangeFilters( corpus );
    	this.size = 0;
    }
    
    setContextFilter( corpus ) {
    	if ( corpus.filtered ) {
			this.query.addToFilter( corpus.contextFilter );
    	}
    }
    
    setTextQuery( corpus, userInput ) {
    	this.query.addToMust( new QuerystringQuery(corpus.metadata.defaultSearchField, userInput) );
    }

    setDiscreteValueFilters( corpus ) {
console.log("Query.setDiscreteValueFilters");
    	let me = this;
    	//corpus.status.inactivateFilter();
    	for ( let ontology of corpus.metadata.aggregations ) {
    		for ( let aggregation of ontology.aggregations ) {
    			//add filter values in a should clause context of new Bool query, then add to this bool query's filter context
    			if ( !( JSON.stringify(aggregation.filters) === JSON.stringify({}) ) && !aggregation.isTemporal ) {
    				//potential fields to be added
    				let filter = undefined;	////defer assignment until proof of need is established
	    			Object.keys( aggregation.filters ).map( function(value,i) {
	    				let addFilter = aggregation.filters[value];	//could be false
	    				if ( addFilter ) {
	    					if ( !filter ) {
	    						filter = new BoolQuery();	//need is established, assign new Bool query to hold the OR options for this aggregation
	    						//corpus.status.activateFilter(); 
	    					}
	    					filter.addToShould( new TermFilter( aggregation.field.fieldName,value ) );
	    				}
	    			});
	    			if ( filter ) me.addFilter( filter );	//at least one TermFilter added to should clause of bool
    			}
        	}
    	}
//alert(`Query.setDiscreteValueFilters:QUERY\n ${JSON.stringify(me.query,null,'\t')}`);
    }
 
    setRangeFilters( corpus ) {
console.log("Query.setRangeFilters");
    	let me = this;
    	for ( let ontology of corpus.metadata.aggregations ) {
    		for ( let aggregation of ontology.aggregations ) {
    			if ( !( JSON.stringify(aggregation.filters) === JSON.stringify({}) ) && aggregation.isTemporal ) {	    
//alert(`Query.setRangeFilters:AGGREGATION\n ${JSON.stringify(aggregation,null,'\t')}`);
    				if ( aggregation.filters.max && aggregation.filters.min ) {
    					//add range filter to filter clause context of this.query
    					let range = new RangeFilter( aggregation.field.fieldName, aggregation.filters.min, aggregation.filters.max );
    					me.addFilter( range );
    				}
    			}
        	}
    	}
//alert(`Query.setRangeFilters:QUERY\n ${JSON.stringify(me.query,null,'\t')}`);
    }
    /* Service Date Aggregation
     {
		"id": 127,
		"label": "Service Date",
		"displayOrder": 10,
		"numberOfFilterOptions": 5,
		"isTemporal": true,
		"isNumeric": false,
		"countDistinct": false,
		"aggregate": true,
		"export": false,
		"filters": {
			"min": 1120673830000,
			"max": 1509401420000
		},
		"min": null,
		"max": null,
		"count": null,
		"status": {
			"computingCounts": false
		},
		"field": {
			"id": 21,
			"fieldName": "service_date",
			"aggregatable": true,
			"significantTermsAggregatable": false,
			"exportable": true,
			"contextFilterField": false,
			"dataTypeName": "DATE",
			"description": "Date of Service"
		},
		"initialSlider": {
			"minValue": 1098835200000,
			"maxValue": 1557446400000,
			"options": {
				"floor": 1098835200000,
				"ceil": 1557446400000,
				"step": 10000,
				"noSwitching": true,
				"hideLimitLabels": false
			},
			"filtered": true
		},
		"currentSlider": {
			"minValue": 1120673830000,
			"maxValue": 1509401420000,
			"options": {
				"floor": 1098835200000,
				"ceil": 1557446400000,
				"step": 10000,
				"noSwitching": true,
				"hideLimitLabels": false
			},
			"filtered": false
		}
	}
     */
    
    //convienience method
    addFilter( filter ) {
    	this.query.addToFilter( filter )
    }
	
}
export default Query;
    /*example aggregation.filters object for discrete values aggregation
		{
			Geriatrics:true,	//result of being selected or re-selected
			Pediatrics:false	//previously selected, but now de-selected
		}
    */
    /*example aggregation.filters object for temporal range aggregation
	{
		min:1108771200000,	//lower limit of date/time range
		max:1108771286400	//upper limit of date/time range
	}
     */

/*
{
	"query": {
		"bool": {

		    "should":[],
		    "must_not":[],
			"must": [
			{
				"query_string": {
					"query": "iliitis",
					"default_operator": "AND",
					"default_field": "text"
				}
			}
			],
    		"filter": [	{
    				    "bool": {
    				        "must":[],
    				        "filter":[],
    				        "must_not":[],
            				"should": [
            				{
            					"term": {
            						"encounter_department_specialty": "Internal Medicine"
            					}
            				},
            				{
            					"term": {
            						"encounter_department_specialty": "Geriatrics"
            					}
            				}
            				]
        			    }
    				},
    			    {
    				    "bool": {
            				"should": [
            				{
            					"term": {
            						"role": "11. Physician"
            					}
            				}
            				]
        			    }
    				},
    				{
    				    "bool": {
    				        "must":[],
    				        "filter":[],
    				        "must_not":[],
            				"should": []
            			}
            		}
    				]
			
		}
	},
	"size": 1,
	"from": 0
}
*/
/*    	
1. generate/capture JS API query types [terms; terms+1filter; terms+2f; terms+nf1Catetory; terms+nf2C; NOT filter types]
2. run query JS API query types, note hits returned
3. devise strategy for keeping track of aggregate filters
4. formulate JS API queries using objects - create unit/integration tests for these
*/
    	
    	
