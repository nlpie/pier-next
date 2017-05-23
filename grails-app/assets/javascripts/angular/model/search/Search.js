import Result from './Result';
import SearchQuery from './elastic/SearchQuery';
import Pagination from './Pagination';
import ContextFilter from './elastic/ContextFilter';
//import SearchService from '../../service/search/SearchService';
import Service from '../../model/search/Search';

class Search {
    constructor($http) {
    	//'ngInject';
    	//this.searchService = new SearchService();
    	this.$http = $http;
    	//this.currentSearch = currentSearch;
    	
    	this.userInput = "blank set of terms";
    	this.context = undefined;
    	//this.contextFilter = undefined; //new ContextFilter(this.currentContext.contextFilterValue);
    	
		this.pagination = new Pagination();
    	
    	this.error = undefined;
        this.dirty = false;
        
        this.cuiExpansionEnabled = false;				
		this.similarityExpansionEnabled = false; 
		this.cuiExpansion = {};				//{ heart:[], valve:[] } or { enabled:false, expansionMap: { heart:[], valve:[] } }
		this.relatednessExpansion = {}; 		//{ heart:[], valve:[] } or { enabled:false, expansionMap: { heart:[], valve:[] } }

		this.results = [];	//Result[]
    }
    
    setContext(searchContext) {
    	if (!searchContext) return;	
    	//for some reason this function gets invoked with undefined searchContext on change of contexts dropdown; 
    	//this check and immediate return prevents console errors, otherwise the app appears to work as expected
    	this.context = searchContext;
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
			alert(JSON.stringify(corpus));
			if ( corpus.queryInfo.searchable ) {
				this.searchCorpus( corpus )
	    			.then( function(response) {
	    				alert("response from elastic");
	    			});
			}
		}
    	
    }
    
    searchCorpus( corpus ) {
		//return the promise and let the client resolve it
    	var url = corpus.queryInfo.url;
    	var contextFilter = new ContextFilter(corpus.queryInfo.contextFilterField, this.context.contextFilterValue);
    	var elasticQuery = new SearchQuery(contextFilter, corpus.queryInfo.defaultSearchField, this.userInput, this.pagination);
    	
		return this.$http.post( APP.ROOT + '/search/elastic/', JSON.stringify( {"url":url, "elasticQuery": elasticQuery} ) );
	}
    
    assignResults(data) {
    	//alert("populate results");
    	this.currentSearch.result = new Result();
    	this.currentSearch.result.notes = data;	
    }
    
    /*execute() {
    	console.info("Search.execute()");
    	this.searchService.fetchResultsFromElastic("notes_v1",this.query).then();
    }*/
    
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