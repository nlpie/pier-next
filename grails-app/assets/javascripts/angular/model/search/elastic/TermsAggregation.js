
class TermsAggregation {
	constructor(field,size) {	
		this.terms = {
			"field": field,
	        "size": size,
	        "missing": "not specified"
		}
    }
}

export default TermsAggregation;

/*
{
    "aggs" : {
        "genres" : {
            "terms" : { "field" : "genre",
             			"size"  : 10
             			}
        }
    }
}
*/