import SearchResponse from './elastic/SearchResponse';


class Result {
    constructor(targetCorpora) {	//targetCorpora is an array, allowing forEach use
    	this.dirty = true;
    	let me = this;
    	
    	targetCorpora.forEach( function(value, i) {
    		console.log(`adding ${value} to Result obj instance`);
    		me[value] = new SearchResponse();
    	});
    	
    }
    
    clear() { 
		
	}
}

export default Result;