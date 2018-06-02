import Pagination from './Pagination';

class Results { 

	constructor() {
		this.docs = undefined;
		this.aggs = undefined;
		this.pagination = new Pagination();
	}
	
	forwardCursor( corpus ) {
		return ( !corpus.isDirty() && this.pagination.hasNext() ) ? "pointer" : "not-allowed";
	}
	backwardCursor( corpus ) {
		return ( !corpus.isDirty() && this.pagination.hasPrevious() ) ? "pointer" : "not-allowed";
	}

}

export default Results;