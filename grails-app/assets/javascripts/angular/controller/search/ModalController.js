import WordEmbedding from '../../model/rest/response/WordEmbedding';
import NotFoundWordEmbedding from '../../model/rest/response/NotFoundWordEmbedding';

class ModalController {
	
	constructor( $scope, currentSearch, expansionService, modalService ) {
		this.currentSearch = currentSearch;
		this.expansionService = expansionService;
		this.modalService = modalService;
		this.embeddings = [];
	}
	
	//convenience method for easily proofing state of objects
	show( obj ){
		alert(JSON.stringify(obj,null,'\t'));
	}
	
	$onInit() {
		let me = this;
		let candidates = this.expansionService.parseUserInputIntoEmbeddingCandidates( this.currentSearch.userInput );
		console.log(JSON.stringify(candidates));
		for ( let term of candidates ) {
			this.expansionService.fetchRelated( term )
				.then( function(response) {
					var wordEmbeddings = response.data
					if ( wordEmbeddings.found ) {
						me.embeddings.push( new WordEmbedding( wordEmbeddings ) );
					} else {
						me.embeddings.push( new NotFoundWordEmbedding( wordEmbeddings ) );
					}
				});
		}
		//alert(JSON.stringify(this.embeddings));
	}
	//esophagogastroduodenoscopy
}

ModalController.$inject = [ '$scope', 'currentSearch', 'expansionService', 'modalService' ];

export default ModalController;