
class DocumentsResponse {
    
	constructor(data) {	//look into constructing from a data structure passed to constructor
    	this.hits = data.hits.hits;
    	this.total = data.hits.total;
    	this.took = data.took/1000 + "s";
    	console.info(this.took);
    	this.timedOut = data.timed_out;
    }
    
    clear() { 
		
	}
}

export default DocumentsResponse;