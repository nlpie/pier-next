
class DocumentsResponse {
    
	constructor(data) {	//look into constructing from a data structure passed to constructor
    	this.hits = data.hits.hits;
    	this.total = data.hits.total;
    	this.took = data.took/1000 + "s";
    	console.info("docs took " + this.took);
    	this.timedOut = data.timed_out;
    }
	
	isEmpty() {
		return ( this.total==0 ) ? true : false;
	}

}

export default DocumentsResponse;