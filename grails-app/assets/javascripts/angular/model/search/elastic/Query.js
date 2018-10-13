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
    	if ( corpus.contextFilterValue ) {
alert("BPIC request context");
    		this.query.addToFilter( new TermFilter( corpus.metadata.contextFilterField, corpus.contextFilterValue ) ); //TODO this is not the right reference for a BPIC note set
    	}
    }
    
    setTextQuery( corpus, userInput ) {
//alert("query: " + userInput);
    	this.query.addToMust( new QuerystringQuery(corpus.metadata.defaultSearchField, userInput) );
    }

    setDiscreteValueFilters( corpus ) {
//alert("discrete");
    	let me = this;
    	corpus.status.inactivateFilter();
    	for ( let ontology of corpus.metadata.aggregations ) {
    		for ( let aggregation of ontology.aggregations ) {
    			//add filter values in a should clause context of new Bool query, then add to this bool query's filter context
    			if ( !( JSON.stringify(aggregation.filters) === JSON.stringify({}) ) && !aggregation.isTemporal ) {
    				//potential fields to be added
//alert("agg for " + aggregation.field.fieldName);
    				let filter = undefined;	////defer assignment until proof of need is established
	    			Object.keys( aggregation.filters ).map( function(value,i) {
	    				let addFilter = aggregation.filters[value];	//could be false
	    				if ( addFilter ) {
	    					if ( !filter ) {
	    						filter = new BoolQuery();	//need is established, assign
	    						corpus.status.activateFilter(); 
	    					}
	    					//alert(JSON.stringify(filter,null,'\t'));
	    					filter.addToShould( new TermFilter( aggregation.field.fieldName,value ) );
	    				}
	    			});
	    			//alert(JSON.stringify(filter,null,'\t'));
	    			if ( filter ) me.addFilter( filter );	//at least one TermFilter added to should clause of bool
    			}
        	}
    	}
//alert(JSON.stringify(corpus.metadata.aggregations,null,'\t'));
    }
    
    setRangeFilters( corpus ) {
    	//alert("range");
    	let me = this;
    	//corpus.status.inactivateFilter();
    	for ( let ontology of corpus.metadata.aggregations ) {
    		for ( let aggregation of ontology.aggregations ) {
    			if ( !( JSON.stringify(aggregation.filters) === JSON.stringify({}) ) && aggregation.isTemporal ) {	    			
    				if ( aggregation.filters.max && aggregation.filters.min ) {
    					//add range filter to filter clause context of this.query
//alert("range " + aggregation.field.fieldName);
    					let range = new RangeFilter( aggregation.field.fieldName, aggregation.filters.min, aggregation.filters.max );
    					me.addFilter( range );
    				}
    			}
        	}
    	}
    }
    
    //convienience method
    addFilter( filter ) {
    	this.query.bool.filter.push( filter );
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
    	
    	
