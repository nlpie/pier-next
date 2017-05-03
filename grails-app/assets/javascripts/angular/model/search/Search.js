import Result from './Result';
import SearchQuery from '../search/elastic/SearchQuery';

class Search {
    constructor(searchService, userInput, context, notesPerPage, offset) {
    	this.searchService = searchService;
    	
    	this.userInput = userInput;
    	this.context = context;
    	
        
    	this.error = undefined;
        this.dirty = false;
        this.cuiExpansionEnabled = false;				
		this.similarityExpansionEnabled = false; 
		this.cuiExpansion = {};				//{ heart:[], valve:[] } or { enabled:false, expansionMap: { heart:[], valve:[] } }
		this.relatednessExpansion = {}; 		//{ heart:[], valve:[] } or { enabled:false, expansionMap: { heart:[], valve:[] } }
		this.dirty = false;
		this.availableCorpora = [];			// [notes,pathology,imaging] loaded from external config, need to differentiate indexes and note sources for each request
		this.targetCorpora = ['notes','imaging'];			//user-selected indexes to search
		
		this.query = new SearchQuery(this.userInput, notesPerPage, offset);
		this.results = new Result(this.targetCorpora);
    }
    
    execute() {
    	console.info("Search.execute()");
    	this.searchService.fetchResultsFromElastic("notes_v1",this.query).then();
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

export default Search;