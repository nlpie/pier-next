
class CardinalityAggregation {
	
	constructor( field ) {
		this.cardinality = {
			"field": field,	
		}
    }
}

export default CardinalityAggregation;

/*
{
    "aggs" : {
        "cardinality_estimate_note_id" : {
            "cardinality" : {
              "field" : "note_id"
            }
        }
    }
}
*/