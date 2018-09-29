
class SearchInstance {
	
	constructor() {	
		this.mode = undefined;	//DEFAULT, RECENT, SAVED, PAGINATION, ENCOUNTER
		this.authorizedContext = undefined;
		this.uuid = this.generateUuid();
		//this.inputExpansion = false;
		this.distinctCounts =  {
			on : false,
			style: {}
		}
		this.recent = { docsQuery:undefined, aggsQuery: undefined };
		this.pastQueryInfo = undefined;
		//queryId returned from server after successful execution
		this.queryId = undefined;	
//alert(JSON.stringify(this));
    }
	
	reset() {
		this.uuid = this.generateUuid();
		this.queryId = undefined;
	}
	
	generateUuid() {
		return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
			var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
			return v.toString(16);
		});
	}
	
	toggleDistinctCounts() {
    	console.log("toggle distinct counts");
    	this.distinctCounts.on = !this.distinctCounts.on;
    	if ( this.distinctCounts.on ) {
    		this.distinctCountsOn();
    	} else {
    		this.distinctCountsOff();
    	}
    }
	
	distinctCountsOn() {
    	this.distinctCounts.on = true;
    	this.distinctCounts.style = { 'color':'green' };
    }
	
	distinctCountsOff() {
    	this.distinctCounts.on = false;
    	this.distinctCounts.style = {};
    }
	
}

export default SearchInstance;