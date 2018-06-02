
class NotFoundWordEmbedding {
	
	constructor( responseData ) {
		this.word = responseData._id;
		this.semanticallyRelatedTerms =  [ { "term":"no suggestions found",	"cosine_distance":-1 } ];
		this.relatedMisspellings = [ { "term":"no suggestions found",	"cosine_distance":-1 } ];
    }
	
}

export default NotFoundWordEmbedding;