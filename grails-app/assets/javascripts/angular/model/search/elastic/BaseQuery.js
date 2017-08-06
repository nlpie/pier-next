import QuerystringQuery from './QuerystringQuery';

//do not instantiate, use subclasses instead
class BaseQuery {
    constructor( corpus, userInput ) {
    	this.query = {};
    	this.query.bool = {};
    	this.query.bool.must = {};
    	this.query.bool.must = new QuerystringQuery(corpus.defaultSearchField, userInput);
    	this.query.bool.filter = [];
    	this.addFilters( corpus );
    	this.size = 0;
    }

    addFilters( corpus ) {
    	this.query.bool.filter.push( corpus.contextFilter ); 
    	var me = this;
    	Object.keys(corpus.appliedFilters).map( function(key,index) {
    		for (let filter of corpus.appliedFilters[key] ) {
        		me.query.bool.filter.push( filter );
        		//TODO get more sophisticated here with should clause, NOT filter
        	}
    	});
    }
}

export default BaseQuery;
    	//this.from = 0;
    	
    	/*    
        {
            "highlight": {
              "fields": {"text": {
                "number_of_fragments": 15,
                "pre_tags": ["<span class='hl'>"],
                "post_tags": ["<\/span>"],
                "fragment_size": 300
              }},
              "encoder": "html"
            },
            "size": 5,
            "query": {"bool": {
              "filter": [{"term": {"mrn": "XYZ"}}],
              "must": {"query_string": {
                "default_operator": "AND",
                "default_field": "text",
                "query": "patient"
              }}
            }},
            "from": 40,
            "aggs": {}
          }
     */
/*    	
1. generate/capture JS API query types [terms; terms+1filter; terms+2f; terms+nf1Catetory; terms+nf2C; NOT filter types]
2. run query JS API query types, note hits returned
3. devise strategy for keeping track of aggregate filters
4. formulate JS API queries using objects - create unit/integration tests for these
*/
    	
    	
/*    	"filter": {"and": {"filters": [
2017-02-13_22:17:30.90671       {"terms": {
2017-02-13_22:17:30.90671         "setting": [
2017-02-13_22:17:30.90671           "4. Inpatient Hospital",
2017-02-13_22:17:30.90672           "3. Emergency Department"
2017-02-13_22:17:30.90672         ],
2017-02-13_22:17:30.90672         "_name": "Setting"
2017-02-13_22:17:30.90672       }},
2017-02-13_22:17:30.90673       {"not": {
2017-02-13_22:17:30.90673         "terms": {
2017-02-13_22:17:30.90673           "_name": "Enc Dept",
2017-02-13_22:17:30.90673           "encounter_dept": ["Unknown"]
2017-02-13_22:17:30.90673         },
2017-02-13_22:17:30.90674         "_name": "NOT Enc Dept"
2017-02-13_22:17:30.90674       }}
2017-02-13_22:17:30.90674     ]}}
2017-02-13_22:17:30.90674   }},

use bool query instead of filters (in filter block?)
*/		
    	//delete after development
    	/*
    	this.aggs.add( "Service Date", new TermsAggregation("service_date",5) );
    	this.aggs.add( "Min Svc Date", new MinAggregation("service_date"));
    	this.aggs.add( "Max Svc Date", new MaxAggregation("service_date"));
		this.aggs.add( "TOS", new TermsAggregation("tos",5) );
		this.aggs.add( "MRN", new TermsAggregation("mrn",5) );
    	*/
    	//end delete
    	
    	//alert(JSON.stringify(this));
    