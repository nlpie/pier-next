
class RangeFilter {
	
	constructor(field, lower, upper) {
		var rng = {};
		rng[field] = {
			"lte": upper,
			"gte": lower,
			"format": "epoch_millis"
			//"format": "YYYY-MM-dd HH:mm||YYYY-MM-dd" e.g. 2018-03-13 18:07 value_as_string property for aggre
			//"format": "epoch_millis" e.g., 1520964420000 value prop for aggregation
		};
		this.range = rng;
    }

}

/*
{
    "range" : {
        "born" : {
            "gte": "01/01/2012",
            "lte": "2013",
            "format": "dd/MM/yyyy||yyyy"
        }
    }
}
*/
export default RangeFilter;