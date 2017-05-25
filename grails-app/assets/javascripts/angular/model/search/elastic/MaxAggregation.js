
class MaxAggregation {
	
	constructor(field) {	
		this.max = {
			"field": field
		}
    }
}

export default MaxAggregation;

/*
{
    "aggs" : {
        "max_price" : { "max" : { "field" : "price" } }
    }
}
*/