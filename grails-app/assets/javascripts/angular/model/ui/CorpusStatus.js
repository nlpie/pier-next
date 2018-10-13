
class CorpusStatus { 

	constructor() {
		this.searchingDocs = false;
		this.computingAggs = false;
		this.active = false;
		this.dirty = false;
		this.opacity = null;
		this.FILTER_ACTIVE = { 'color':'green' };
		this.FILTER_INACTIVE = { 'color':'#ccc' };
		this.filter = {
			on: false,
			style: this.FILTER_INACTIVE,
		}
	}

	/*toggle() {
		console.log("corpus.status.toggle()");
		if ( this.filter.on ) {
			this.filter.style = this.FILTER_INACTIVE;
		} else {
			this.filter.style = this.FILTER_ACTIVE;
		}
		this.filter.on = !this.filter.on;
	}*/
	
	activateFilter() {
		this.filter.on = true;
		this.filter.style = this.FILTER_ACTIVE;
	}
	inactivateFilter() {
		this.filter.on = false;
		this.filter.style = this.FILTER_INACTIVE;
	}

}

export default CorpusStatus;