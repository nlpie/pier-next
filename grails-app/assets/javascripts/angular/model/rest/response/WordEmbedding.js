import RelatedTerm from '../../ui/RelatedTerm';
import RelatedMisspelling from '../../ui/RelatedMisspelling';

class WordEmbedding {
	
	constructor( responseData ) {
		console.log("WordEmbedding");
		this.word = responseData._id;
		this.semanticallyRelatedTerms = [];
		this.relatedMisspellings = [];
		for ( let obj of responseData._source.semantically_related_terms ) {
			this.semanticallyRelatedTerms.push ( new RelatedTerm( obj ) );
		}
		for ( let obj of responseData._source.related_misspellings ) {
			this.relatedMisspellings.push ( new RelatedMisspelling( obj ) );
		}
    }
	
}

export default WordEmbedding;