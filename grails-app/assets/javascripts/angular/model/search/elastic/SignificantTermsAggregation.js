
class SignificantTermsAggregation {
	
	constructor( field,size ) {
		/*var notSpecified = "missing";	//display value for documents where value is not defined
		if ( field=="service_date" || field=="filing_date" ) {
			notSpecified = "1899-12-31";
		} else if ( field=="encounter_id" ) {
			notSpecified = null
		}*/
		
		this.significant_terms = {
			"field": field,
	        "size": size//,
	        //"missing": notSpecified	
		}
    }
	
}

export default SignificantTermsAggregation;

/*
"aggs": {
    "most_sig": {
      "significant_terms": { 
        "field": "movie",
        "size": 6
      }
    }
  }
*/