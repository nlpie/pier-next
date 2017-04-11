import Highlight from './Highlight';
import QuerystringQuery from './QuerystringQuery';
import Aggregations from './Aggregations';
import TermsAggregation from './TermsAggregation';
import MinAggregation from './MinAggregation';
import MaxAggregation from './MaxAggregation';

class SearchQuery {
    constructor(terms,notesPerPage,offset) {
    	this.query = new QuerystringQuery(terms);
    	this.aggs = new Aggregations();
    	this.highlight = new Highlight();
    	//fields					//get fields from user settings OR just rely on _source (default)
    	this.size = notesPerPage;
    	this.from = offset;
    	//filter
    	
/*    	"filter": {"and": {"filters": [
2017-02-13_22:17:30.90671       {"terms": {
2017-02-13_22:17:30.90671         "setting": [
2017-02-13_22:17:30.90671           "4. Inpatient Hospital",
2017-02-13_22:17:30.90672           "3. Emergency Department"
2017-02-13_22:17:30.90672         ],
2017-02-13_22:17:30.90672         "_name": "Setting"
2017-02-13_22:17:30.90672       }},
2017-02-13_22:17:30.90673       {"not": {
2017-02-13_22:17:30.90673         "terms": {
2017-02-13_22:17:30.90673           "_name": "Enc Dept",
2017-02-13_22:17:30.90673           "encounter_dept": ["Unknown"]
2017-02-13_22:17:30.90673         },
2017-02-13_22:17:30.90674         "_name": "NOT Enc Dept"
2017-02-13_22:17:30.90674       }}
2017-02-13_22:17:30.90674     ]}}
2017-02-13_22:17:30.90674   }},

use bool query instead of filters (in filter block?)
*/		
    	//delete after development
    	
    	this.aggs.add( "Service Date", new TermsAggregation("service_date",5) );
		this.aggs.add( "TOS", new TermsAggregation("tos",5) );
		this.aggs.add( "Min Svc Date", new MinAggregation("service_date"));
		this.aggs.add( "Max Svc Date", new MaxAggregation("service_date"));
    	alert(JSON.stringify(this));
    	
    	//end delete
    }
    
    clear() { 
		
	}
}

export default SearchQuery;