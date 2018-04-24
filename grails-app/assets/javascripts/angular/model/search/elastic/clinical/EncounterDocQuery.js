import DocumentQuery from '../DocumentQuery';
import TermFilter from '../TermFilter';

class EncounterDocQuery extends DocumentQuery {
    constructor( corpus, userInput, serviceId ) {
    	super( corpus, userInput );
    	//this.query.bool.filter.push( new TermFilter( "service_id", serviceId ) );
    	this.query.addToFilter( new TermFilter( "service_id", serviceId ) );
    	this.sort = [
    	             {"service_date" : {"order" : "desc"} }
    	];
    	//clear filters, set to encounter, sort by date
    }
}

export default EncounterDocQuery;

/*
should be able to append a 'sort' property to a DocumentQuery where the underlying BoolQuery has a should clause specifying the encounter_id

https://www.elastic.co/guide/en/elasticsearch/reference/2.3/search-request-sort.html
curl -XPOST 'localhost:9200/_search' -d '{
"query" : {
 ...
},
"sort" : [
   {"price" : {"order" : "asc", "mode" : "avg"}}
]
}'

{
    "sort" : [
        { "post_date" : {"order" : "asc"}},
        "user",
        { "name" : "desc" },
        { "age" : "desc" },
        "_score"
    ],
    "query" : {
        "term" : { "user" : "kimchy" }
    }
}
*/