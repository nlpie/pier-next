
class ResultsController {
	
	constructor( currentSearch ) {
		this.search = currentSearch;
	}
	
	//convenience method for easily proofing state of objects
	show( obj ){
		alert(JSON.stringify(obj,null,'\t'));
	}
	
}

ResultsController.$inject = [ 'currentSearch' ];

export default ResultsController;