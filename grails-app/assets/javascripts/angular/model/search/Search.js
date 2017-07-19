import DocumentsResponse from './DocumentsResponse';
import AggregationsResponse from './AggregationsResponse';
import DocumentQuery from './elastic/DocumentQuery';
import AggregationQuery from './elastic/AggregationQuery';
import TermsAggregation from './elastic/TermsAggregation';
import Pagination from './Pagination';
import ContextFilter from './elastic/ContextFilter';
import Service from '../../model/search/Search';

class Search {
    constructor($http) {
    	this.$http = $http;
    	
    	this.userInput = "heart";
    	this.context = undefined;
    	
		this.pagination = new Pagination();
    	
    	this.error = undefined;
        this.dirty = false;
        
        this.cuiExpansionEnabled = false;				
		this.similarityExpansionEnabled = false; 
		this.cuiExpansion = {};				//{ heart:[], valve:[] } or { enabled:false, expansionMap: { heart:[], valve:[] } }
		this.relatednessExpansion = {}; 		//{ heart:[], valve:[] } or { enabled:false, expansionMap: { heart:[], valve:[] } }

		this.results = {};	//object containing key:SearchResponse{}
    }
    
    setContext(searchContext) {
    	if (!searchContext) return;	
    	//for some reason this function gets invoked with undefined searchContext on change of contexts dropdown; 
    	//this check and immediate return prevents console errors, otherwise the app appears to work as expected
    	this.context = searchContext;
    	this.results = {};
    	var me = this;
    	for ( let corpus of this.context.candidateCorpora ) {
    		if ( corpus.queryInfo.searchable ) {
				this.fetchFilters( corpus )
	    			.then( function(response) {
	    				corpus.queryFilters = response.data;
	    			});
			}
    	}
    }
    
    fetchFilters( corpus ) {
    	//gets aggregation filters based on user prefs
    	return this.$http.get( APP.ROOT + '/config/defaultFilters/' + corpus.id );
    }
    
    
/*    
    GET _search
    {
      "query": { 
        "bool": { 
          "must": [
            { "match": { "title":   "Search"        }}, 
            { "match": { "content": "Elasticsearch" }}  
          ],
          "filter": [ 
            { "term":  { "status": "published" }}, 
            { "range": { "publish_date": { "gte": "2015-01-01" }}} 
          ]
        }
      }
    }
*/
    
    
    execute() {
    	//pagination.notesPerPage and .offset come from this.pagination
    	var me = this;
    	var defaultCorpusSet = false;
    	for ( let corpus of this.context.candidateCorpora ) {
    		if ( corpus.queryInfo.searchable ) {
    			
    			//alert(JSON.stringify(corpus,null,'\t'));
				
    			var contextFilter = new ContextFilter(corpus.queryInfo.contextFilterField, this.context.contextFilterValue);
				var docs = this.searchCorpus( corpus, contextFilter )
	    			.then( function(response) {
	    				me.assignDocumentsResponse( corpus,response );
	    				//client-side setting of default corpus
	    				if ( !defaultCorpusSet ) {
	    					corpus.selected = true;
	    					defaultCorpusSet = true;
	    				} else {
	    					corpus.selected = false;
	    				}
	    			})
	    			.catch( console.log.bind(console) );
				var aggs = this.fetchAggregations( corpus, contextFilter )
    				.then( function(response) {
	    				me.assignAggregationsResponse( corpus,response );
    				})
    				.catch( console.log.bind(console) );
			}
		}
    	
    }
    
    searchCorpus( corpus, contextFilter ) {
		//return the promise and let the client resolve it
    	var url = corpus.queryInfo.url;
    	var docsQuery = new DocumentQuery(contextFilter, corpus.queryInfo.defaultSearchField, this.userInput, this.pagination);
    	//alert(JSON.stringify(docsQuery));
		return this.$http.post( APP.ROOT + '/search/elastic/', JSON.stringify( {"url":url, "elasticQuery": docsQuery} ) );
	}
    fetchAggregations( corpus, contextFilter ) {
    	//return the promise and let the client resolve it
    	var url = corpus.queryInfo.url;
    	var aggsQuery = new AggregationQuery(contextFilter, corpus.queryInfo.defaultSearchField, this.userInput);
    	//use defaultFilters.filter.label for now as the name of th aggregation
    	aggsQuery.aggs.add("Mrn", new TermsAggregation("mrn",10));
    	aggsQuery.aggs.add("Encounter Id", new TermsAggregation("encounter_id",10));
    	aggsQuery.aggs.add("Service Date", new TermsAggregation("service_date",10));
    	//alert(JSON.stringify(aggsQuery,null,'\t'));
		return this.$http.post( APP.ROOT + '/search/elastic/', JSON.stringify( {"url":url, "elasticQuery": aggsQuery} ) );
    }
    
    assignDocumentsResponse(corpus,response) {
    	if ( !this.results[corpus.name] ) this.results[corpus.name] = {};
    	this.results[corpus.name].docs = new DocumentsResponse(response.data);
    	
    	/*{"_shards":{"total":6,"failed":0,"successful":6},"hits":{"hits":[],"total":128,"max_score":0.0},"took"
    		:613,"timed_out":false,"aggregations":{"KoD":{"doc_count_error_upper_bound":0,"sum_other_doc_count":0
    		,"buckets":[{"doc_count":128,"key":"7. Note"}]}}}
    	*/
    	
    }
    assignAggregationsResponse(corpus,response) {
    	if ( !this.results[corpus.name] ) this.results[corpus.name] = {};
    	this.results[corpus.name].aggs = new AggregationsResponse(response.data);
    }
    
    /*clear() { 
		this.resetFilters();
		this.resetQuery();
		this.dirty = false;
	}
	resetQuery() { 
		this.currentQuery.queryText = ""; 
	}
	resetFilters() { 
		this.currentQuery.filters = {}; 
	}*/
    
}

Search.$inject = [ '$http' ];

export default Search;