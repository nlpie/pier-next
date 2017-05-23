
class QuerystringQuery {
    
	constructor(field, userInput) {
        this.query_string = {
        	"default_field" : field,
        	"default_operator": "AND",
        	"query": userInput
        }
    }
    

}

export default QuerystringQuery;

/*
"query": {"query_string": {
    "query": "text:( female ) ",
    "default_operator": "AND",
    "fields": ["text"]
}}
*/