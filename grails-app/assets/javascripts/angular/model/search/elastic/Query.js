import BoolQuery from './BoolQuery';
import QuerystringQuery from './QuerystringQuery';
import TermFilter from './TermFilter';

//do not instantiate, use subclasses instead
class Query {
    constructor( corpus, userInput ) {
    	this.query = new BoolQuery();
    	this.query.addToMust( new QuerystringQuery(corpus.metadata.defaultSearchField, userInput) );
    	this.addFilters( corpus );
    	this.size = 0;
    }

    addFilters( corpus ) {
    	if ( corpus.contextFilter ) {
    		this.query.bool.filter.push( corpus.contextFilter ); //TODO this is not the right reference for a BPIC note set
    	}
    	var me = this;
    	Object.keys( corpus.metadata.aggregations ).map( function(ontol,index) {
    		let ontology = corpus.metadata.aggregations[ontol];
    		Object.keys( ontology ).map( function(agg,idx) {
    			let aggregation = ontology[agg];
    			if ( !( JSON.stringify(aggregation.filters) === JSON.stringify({}) ) ) {
    				//potential fields to be added
    				let filter = undefined;	////defer assignment until proof of need is established
	    			Object.keys( aggregation.filters ).map( function(value,i) {
	    				let addFilter = aggregation.filters[value];	//could be false
	    				if ( addFilter ) {
	    					if ( !filter ) filter = new BoolQuery();	//need is established, assign
	    					alert(JSON.stringify(filter,null,'\t'));
	    					filter.addToShould( new TermFilter( aggregation.field.fieldName,value ) );
	    				}
	    			});
	    			alert(JSON.stringify(filter,null,'\t'));
	    			if ( filter ) me.addFilter( filter );	//at least one TermFilter added to should clause of bool
    			}
        	})
    	});
    }
    
    addFilter( filter ) {
    	this.query.bool.filter.push( filter );
    }
	/*example aggregation.filters object
		{
			Geriatrics:true,	//result of being selected or re-selected
			Pediatrics:false	//previously selected, but now de-selected
		}
    */
}

export default Query;
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
    	
    	
