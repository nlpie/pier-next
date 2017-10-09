import BaseQuery from './BaseQuery';
import Aggregations from './Aggregations';
import CardinalityAggregation from './CardinalityAggregation';

/*
 * 	search results from using this class are handled server-side (actuall scrolling done on server), no client-side processing of its results, 
 */
class SingleFieldScrollCountQuery extends BaseQuery {
    constructor( corpus, userInput, label, fieldName ) {
    	super( corpus, userInput );
    	this.size = 15000;	//TODO externalize
    	this._source = false;
    	this.fields = fieldName;
    	this.sort = [ fieldName ];	//fieldName
    	this.aggs = new Aggregations();
    	//include cardinality estimate
    	this.aggs.add( label + ' Cardinality Estimate', new CardinalityAggregation( fieldName ) )
    	//alert(JSON.stringify(this, null, '\t'));
    }
}

export default SingleFieldScrollCountQuery;

/* results look like this
{
  "_scroll_id": "cXVlcnlUaGVuRmV0Y2g7NjsxNDM1OllYSEhkOWFxU04tUzlQdTMzQ0lVWFE7Mjc5MjpWUkxjdFVIOVNnQ2lJS2NvclBPNEJ3OzI3OTI6WUNKclVUTVpTdUdXek13Q2RPVjNiQTsxNDM1OlhQb1BFRUdsU0w2UDVDOXRKZDkwT3c7MTQzNTo3R2p6dHF1c1N0aTlYT3NsYlhjb2hBOzE0MzU6X25MQW5Lcm9SREs5MWVpMWJiWmZUQTswOw==",
  "took": 6,
  "timed_out": false,
  "_shards": {
    "total": 6,
    "successful": 6,
    "failed": 0
  },
  "hits": {
    "total": 152,
    "max_score": null,
    "hits": [
      {
        "_index": "notes_v3",
        "_type": "note",
        "_id": "11294465",
        "_score": null,
        "_timestamp": 1500054948309,
        "fields": {
          "note_id": [
            11294465
          ]
        },
        "sort": [
          43610
        ]
      }
    ]
  },
  "aggregations": {
    "Note Id Cardinality Estimate": {
      "value": 152
    }
  }
}
*/