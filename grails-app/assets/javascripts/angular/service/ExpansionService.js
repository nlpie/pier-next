
class ExpansionService {

	constructor( $http, $q, growl ) {
		this.$http = $http;
		this.$q = $q;
		this.growl = growl;
	}
	
	fetchRelated( term ) {
		//TODO externalize expansion terms URL
		//return this.$http.post( APP.ROOT + '/search/related/', { "url": "http://nlp05.ahc.umn.edu:9200/expansion_v1/word/", "term":term } ) ;
		let url = "http://nlp05.ahc.umn.edu:9200/expansion_v1/word/"
		
		if ( APP.ENV=="fvdev" ) {
			url = "http://localhost:9200/expansion_v1/word/"
		} else if ( APP.ENV=="fvprod" || APP.ENV=="fvtest" ) {
			url = "http://nlp02.fairview.org:9200/expansion_v1/word/"
		}
		return this.$http.post( APP.ROOT + '/search/related', { "url": url, "term":term } ) ;
	}
	
	parseUserInputIntoEmbeddingCandidates( userInput ) {
		let tokens = userInput.split(" ");
		//alert(JSON.stringify(tokens));
		let embeddingCandidates = [];
		let phrase = false;
		for ( let token of tokens ) {
			let field = false;
			if ( token.includes(":") ) field = true;
			if ( token.startsWith('"') ) {
				//alert("phrase start: " + token );
				phrase = true;
			}
			if ( !phrase && !field && ( token!="AND" && token!="OR" && token!="NOT") ) {
				embeddingCandidates.push( token.replace(/\(/g,"").replace(/\)/g,"") );
			}
			if ( token.endsWith('"') || ( token.includes('"') && (!token.endsWith('"') && !token.startsWith('"')) )  ){
				//alert("phrase contains/end: " + token );
				phrase = false;
			}
		}
		//alert( JSON.stringify(embeddingCandidates) );
		return embeddingCandidates;
	}

}

ExpansionService.$inject = [ '$http', '$q', 'growl' ];

export default ExpansionService;