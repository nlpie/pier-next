
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