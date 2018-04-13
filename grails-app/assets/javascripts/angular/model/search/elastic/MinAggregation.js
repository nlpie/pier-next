
class MinAggregation {
	
	constructor(field) {	
		this.min = {
			"field": field
		}
    }
}

export default MinAggregation;

/* query
{
    "aggs" : {
        "min_price" : { "min" : { "field" : "price" } }
    }
}
*/
/* response
{
	  "took": 11,
	  "timed_out": false,
	  "_shards": {
	    "total": 6,
	    "successful": 6,
	    "failed": 0
	  },
	  "hits": {
	    "total": 173,
	    "max_score": 0,
	    "hits": []
	  },
	  "aggregations": {
	    "Prov Type": {
	      "doc_count_error_upper_bound": 0,
	      "sum_other_doc_count": 8,
	      "buckets": [
	        {
	          "key": "Physician",
	          "doc_count": 107
	        },
	        {
	          "key": "null",
	          "doc_count": 41
	        },
	        {
	          "key": "Nurse Practitioner",
	          "doc_count": 12
	        },
	        {
	          "key": "Physician Assistant - C",
	          "doc_count": 3
	        },
	        {
	          "key": "Osteopath",
	          "doc_count": 2
	        }
	      ]
	    },
	    "Filing Datetime.max": {
	      "value": 1520964420000,
	      "value_as_string": "2018-03-13 18:07"
	    },
	    "Filing Datetime.min": {
	      "value": 1108771200000,
	      "value_as_string": "2005-02-19 00:00"
	    },
	    "Filing Datetime": {
	      "doc_count_error_upper_bound": 3,
	      "sum_other_doc_count": 141,
	      "buckets": [
	        {
	          "key": 1108771200000,
	          "key_as_string": "2005-02-19 00:00",
	          "doc_count": 27
	        },
	        {
	          "key": 1300653480000,
	          "key_as_string": "2011-03-20 20:38",
	          "doc_count": 2
	        },
	        {
	          "key": 1108857600000,
	          "key_as_string": "2005-02-20 00:00",
	          "doc_count": 1
	        },
	        {
	          "key": 1110005940000,
	          "key_as_string": "2005-03-05 06:59",
	          "doc_count": 1
	        },
	        {
	          "key": 1112632080000,
	          "key_as_string": "2005-04-04 16:28",
	          "doc_count": 1
	        }
	      ]
	    }
	  }
	}
*/