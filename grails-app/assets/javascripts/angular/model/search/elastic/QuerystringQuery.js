
class QuerystringQuery {
    
	constructor(queryString) {
        this.query_string = {
        	"default_field" : "text",
        	"default_operator": "AND",
        	"query": queryString
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