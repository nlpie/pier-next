import WordEmbedding from '../../model/rest/response/WordEmbedding';
import NotFoundWordEmbedding from '../../model/rest/response/NotFoundWordEmbedding';

class ExpansionController {
	
	constructor( $scope, currentSearch, expansionService, modalService ) {
		this.currentSearch = currentSearch;
		this.expansionService = expansionService;
		this.modalService = modalService;
		this.embeddings = [];
		this.allRelatedMisspellings = false;
		this.allSemanticallyRelatedTerms = false;
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
				})
				.then( function() {me.populateExistingExpansions();} );
		}
	}
	
	populateExistingExpansions() {
		if ( this.currentSearch.inputExpansion.terms.length==0 ) return;
		for ( let embeddingSet of this.embeddings ) {	//one set for each term
			for ( let term of this.currentSearch.inputExpansion.terms ) {	//user-designated expansion terms
				for ( let suggestion of embeddingSet.relatedMisspellings ) {
					for ( let expansionTerm of term.expandUsing ) {
						if ( expansionTerm==suggestion.term ) {
							suggestion.on = true;
						}
					}
				}
				for ( let suggestion of embeddingSet.semanticallyRelatedTerms ) {		
					for ( let expansionTerm of term.expandUsing ) {
						if ( expansionTerm==suggestion.term ) {
							suggestion.on = true;
						}
					}
				}
			}
		}
	}
	
	clear() {
		this.currentSearch.inputExpansion.reset();
	}
	
	toggleAllRelatedMisspellings( targetTerm, relatedMisspellingSuggestions ) {
		this.allRelatedMisspellings = !this.allRelatedMisspellings;
		for ( let suggestion of relatedMisspellingSuggestions ) {
			this.currentSearch.inputExpansion.add( targetTerm, suggestion );
		}
	}
	
	toggleAllSemanticallyRelatedTerms( targetTerm, semanticallyRelatedSuggestions ) {
		this.allSemanticallyRelatedTerms = !this.allSemanticallyRelatedTerms;
		for ( let suggestion of semanticallyRelatedSuggestions ) {
			this.currentSearch.inputExpansion.add( targetTerm, suggestion );
		}
	}
	
	//esophagogastroduodenoscopy
}

ExpansionController.$inject = [ '$scope', 'currentSearch', 'expansionService', 'modalService' ];

export default ExpansionController;