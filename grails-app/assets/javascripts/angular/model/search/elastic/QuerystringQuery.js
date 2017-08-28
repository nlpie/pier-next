
class QuerystringQuery {
    
	constructor( defaultSearchField, userInput ) {
        this.query_string = {
        	"query": userInput,
        	"default_operator": "AND",
        	"default_field": defaultSearchField
        }
    }
    

}

export default QuerystringQuery;