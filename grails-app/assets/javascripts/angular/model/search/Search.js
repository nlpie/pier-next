import Result from './Result';
import SearchQuery from './elastic/SearchQuery';
import Pagination from './Pagination';
import ContextFilter from './elastic/ContextFilter';
import SearchService from '../../service/search/SearchService';

class Search {
    constructor(currentContext) {
    	this.searchService = new SearchService();
    	this.userInput = "heart OR valves";
    	this.currentContext = currentContext;
    	
		this.pagination = new Pagination();
    	
    	this.error = undefined;
        this.dirty = false;
        
        this.cuiExpansionEnabled = false;				
		this.similarityExpansionEnabled = false; 
		this.cuiExpansion = {};				//{ heart:[], valve:[] } or { enabled:false, expansionMap: { heart:[], valve:[] } }
		this.relatednessExpansion = {}; 		//{ heart:[], valve:[] } or { enabled:false, expansionMap: { heart:[], valve:[] } }

		for ( let corpus of this.currentContext.candidateCorpora ) {
			alert(corpus.queryInfo.contextFilterField +":"+this.currentContext.contextFilterValue);
		}
		//this.contextFilter = new ContextFilter(contextFilterValue);
		//this.query = new SearchQuery(this.contextFilter, this.userInput, this.pagination.notesPerPage, this.pagination.offset);
		//this.results = [];	//Result[]
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
    	this.searchService.fetchResultsFromElastic( this.userInput, this.currentContext, corpus )
    		.then( function(response) {
    			alert("response from elastic");
    		});
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

//Search.$inject = [ 'searchService' ];

export default Search;