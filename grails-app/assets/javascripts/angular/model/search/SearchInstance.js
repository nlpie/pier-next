
class SearchInstance {
	
	constructor() {	
		this.mode = undefined;	//DEFAULT, RECENT, SAVED, PAGINATION, ENCOUNTER
		this.authorizedContext = undefined;
		this.uuid = this.generateUuid();
		//this.inputExpansion = false;
		this.ACTIVE = { 'color':'green' };
		this.INACTIVE = { 'color':'#ccc' };
		this.distinctCounts =  {
			on : false,
			style: this.INACTIVE
		}
		this.recent = { docsQuery:undefined, aggsQuery: undefined };
		this.lastQuery = undefined;
		//auditedQuery returned from server after successful execution
		this.auditedQuery = undefined;	//TODO does pagination query need to track this?
    }
	
	reset() {
		this.uuid = this.generateUuid();
		this.auditedQuery = undefined;
	}
	
	generateUuid() {
		return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
			var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
			return v.toString(16);
		});
	}
	
	toggleDistinctCounts() {
    	this.distinctCounts.on = !this.distinctCounts.on;
    	if ( this.distinctCounts.on ) {
    		this.distinctCountsOn();
    	} else {
    		this.distinctCountsOff();
    	}
    	console.log("distinct counts on: " + this.countsOn());
    }
	
	distinctCountsOn() {
    	this.distinctCounts.on = true;
    	this.distinctCounts.style = this.ACTIVE;
    }
	
	distinctCountsOff() {
    	this.distinctCounts.on = false;
    	this.distinctCounts.style = this.INACTIVE;
    }
	
	countsOn() {
		return this.distinctCounts.on;
	}
	
}

export default SearchInstance;