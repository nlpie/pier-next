import DocumentsResponse from './DocumentsResponse';
import AggregationsResponse from './AggregationsResponse';
import DocumentQuery from './elastic/DocumentQuery';
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
    	alert (corpus.id)
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
    	for ( let corpus of this.context.candidateCorpora ) {
			//alert(JSON.stringify(corpus));
			if ( corpus.queryInfo.searchable ) {
				this.searchCorpus( corpus )
	    			.then( function(response) {
	    				me.assignDocumentsResponse(corpus,response);
	    			});
			}
		}
    	
    }
    
    searchCorpus( corpus ) {
		//return the promise and let the client resolve it
    	var url = corpus.queryInfo.url;
    	var contextFilter = new ContextFilter(corpus.queryInfo.contextFilterField, this.context.contextFilterValue);
    	var docsQuery = new DocumentQuery(contextFilter, corpus.queryInfo.defaultSearchField, this.userInput, this.pagination);
    	
		return this.$http.post( APP.ROOT + '/search/elastic/', JSON.stringify( {"url":url, "elasticQuery": docsQuery} ) );
	}
    computeAggregations( corpus ) {
    	//return the promise and let the client resolve it
    	var url = corpus.queryInfo.url;
    	var contextFilter = new ContextFilter(corpus.queryInfo.contextFilterField, this.context.contextFilterValue);
    	var aggsQuery = new DocumentQuery(contextFilter, corpus.queryInfo.defaultSearchField, this.userInput, this.pagination);
    	
		return this.$http.post( APP.ROOT + '/search/elastic/', JSON.stringify( {"url":url, "elasticQuery": aggsQuery} ) );
    }
    
    assignDocumentsResponse(corpus,response) {
    	if ( !this.results[corpus.name] ) this.results[corpus.name] = {};
    	this.results[corpus.name].docs = new DocumentsResponse(response.data);
    	//console.info(JSON.stringify(this.results['Clinical Notes'].docs.hits[0]._source.text));
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