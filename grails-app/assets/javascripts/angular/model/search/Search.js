import Result from './Result';
import SearchQuery from '../search/elastic/SearchQuery';

class Search {
    constructor(context,terms,notesPerPage,offset) {
    	this.context = context;
    	this.terms = terms; 			//"heart OR valve"
        this.error = undefined;
        this.dirty = false;
        this.cuiExpansionEnabled = false;				
		this.similarityExpansionEnabled = false; 
		this.cuiExpansion = {};				//{ heart:[], valve:[] } or { enabled:false, expansionMap: { heart:[], valve:[] } }
		this.similarityExpansion = {}; 		//{ heart:[], valve:[] } or { enabled:false, expansionMap: { heart:[], valve:[] } }
		this.dirty = false;
		this.availableCorpora = [];			// [notes,pathology,imaging] loaded from external config, need to differentiate indexes and note sources for each request
		this.targetCorpora = ['notes','imaging'];			//user-selected indexes to search
		
		
		//delete after development
		
		this.terms = "penny OR lane";
		notesPerPage = 1;
		offset = 0;
		//end delete
		
		this.SearchQuery = new SearchQuery(this.terms, notesPerPage, offset);
		this.results = new Result(this.targetCorpora);
    	
    	
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