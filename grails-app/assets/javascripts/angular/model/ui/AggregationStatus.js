
class CorpusStatus { 

	constructor() {
		this.searchingDocs = false;
		this.computingAggs = false;
		this.active = false;
		this.dirty = false;
		this.userSelectedFilters = false;
		this.showBan = false;
		this.opacity = null;
	}

}

export default CorpusStatus;