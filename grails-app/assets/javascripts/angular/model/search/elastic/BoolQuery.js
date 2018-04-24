
class BoolQuery {
    constructor() {
    	this.bool = {
    		"filter": [],	//does not affect doc score
    		"must": [],		//affects doc score
    		"should":[],	//affects doc score
    		"must_not": []
    	};
    }
    
    addToFilter( filter ) {
    	//typically this will be a TermQuery instance used to restrict the search context
    	this.bool.filter.push( filter );
    }
    
    addToMust( query ) {
    	//typically this will be a QueryStringQuery instance
    	this.bool.must.push( query );
    }

    addToShould( filter ) {
    	//obj should be a TermFilter
    	this.bool.should.push( filter );
    }
    
    addToMustNot( filter ) {
    	//typically this will be a a TermFilter instance
    	this.bool.must_not.push( filter );
    }
}

export default BoolQuery;