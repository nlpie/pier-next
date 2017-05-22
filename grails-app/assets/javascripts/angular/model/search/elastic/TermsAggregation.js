
class TermsAggregation {
	constructor(field,size) {
		var notSpecified = "missing";	//display value for documents where value is not defined
		if ( field=="service_date" || field=="filing_date" ) {
			notSpecified = "1899-12-31";
		}
			
		this.terms = {
			"field": field,
	        "size": size,
	        "missing": notSpecified	
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