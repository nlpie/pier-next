
class Pagination {
	constructor( total ) {
		this.notesPerPage = 10;
		this.from = 0;
		this.maxDocs = 0;
		this.maxPage = 0;
		this.maxFrom = 10000;	//TODO externalize
		this.limitPage = undefined;
		this.currentPage = 0;
		this.truncated = false;
    }
	
	hasNext() {
		return ( this.currentPage<this.maxPage ) ? true : false;
	}
	next() {
		console.log("pagination.next");
		if ( !this.hasNext() ) return false;
		this.currentPage++;
		this.updateFrom();
		return true;
	}
	last() {
		console.log("pagination.last");
		if ( !this.hasNext() ) return false;
		this.currentPage = this.maxPage;
		this.updateFrom();
		return true;
	}
	
	hasPrevious() {
		return ( this.currentPage>0 ) ? true : false;
	}
	previous() {
		console.log("pagination.previous");
		if ( !this.hasPrevious() ) return false;
		this.currentPage--;
		this.updateFrom();
		return true;
	}
	first() {
		console.log("pagination.first");
		if ( !this.hasPrevious() ) return false;
		this.currentPage = 0;
		this.updateFrom();
		return true;
	}
	
	updateFrom() {
		this.from = this.currentPage * this.notesPerPage;
		//let from = this.from;
		//let npp = this.notesPerPage;
		//let limit = 10000;
		if ( this.currentPage > this.paginationLimit ) {	//ES default limit for too deep pagination
			this.currentPage = this.paginationLimit;
			this.truncated = true;
			this.from = this.maxFrom - this.notesPerPage;
		} else {
			this.truncated = false;
		}
	}
	update( total ) {
		//alert("updating");
		let npp = this.notesPerPage;
		let limit = this.maxFrom;
		this.maxDocs = total;
		this.maxPage = Math.floor( total/this.notesPerPage );
		this.paginationLimit = Math.floor( (limit - npp)/npp );
	}
	
	currentPageInfo() {
		let lower = 0;
		if ( this.maxDocs>0 ) {
			lower = this.from + 1;
		}
		let upper = lower+this.notesPerPage-1;
		if ( upper>this.maxDocs ) upper = this.maxDocs;
		return lower + "-" + upper;
	}

}

export default Pagination;