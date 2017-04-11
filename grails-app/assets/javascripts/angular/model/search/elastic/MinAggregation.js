
class MinAggregation {
	constructor(field) {	
		this.min = {
			"field": field
		}
    }
}

export default MinAggregation;

/*
{
    "aggs" : {
        "min_price" : { "min" : { "field" : "price" } }
    }
}
*/