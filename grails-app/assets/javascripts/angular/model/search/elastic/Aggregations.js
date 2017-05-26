
class Aggregations {
	
	constructor() {	}
	
	add(label,agg) {
		this[label] = agg;
	}
}

export default Aggregations;

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

//use https://www.elastic.co/guide/en/elasticsearch/reference/2.3/search-aggregations-bucket-filter-aggregation.html for AuthorizedContext filtering
//added aggregations are those filters that user has enabled in their preferences; should AND/OR filters exclude the filters complement? And should NOT filter be excluded?