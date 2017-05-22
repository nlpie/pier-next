{
    "query": {"query_string": {
        "query": "note_text:( heart ) ",
        "default_operator": "AND",
        "fields": ["note_text"]
    }},
    "facets": {
        "setting": {"terms": {
            "field": "setting",
            "size": 3
        }},
        "tos": {"terms": {
            "field": "tos",
            "size": 3
        }},
        "encDeptSpec": {"terms": {
            "field": "encounter_dept_specialty",
            "size": 3
        }}
    },
    "from": 0,
    "highlight": {
            "encoder": "html",
            "fields": {"note_text": {
                "number_of_fragments": 15,
                "post_tags": ["<\/span>"],
                "pre_tags": ["<span class='hl'>"],
                "fragment_size": 300
        }}
    },
    /*"fields": [
        "_source"
    ],*/
    //defaults to _source if fields is empty
    "size": 10
}



{
	  "took": 935,
	  "timed_out": false,
	  "_shards": {
	    "total": 34,
	    "successful": 34,
	    "failed": 0
	  },
	  "hits": {
	    "total": 11476966,
	    "max_score": 3.3909204,
	    "hits": [
	      {
	        "_index": "2012_notes_v1",
	        "_type": "note",
	        "_id": "188190805",
	        "_score": 3.3909204,
	        "fields": {
	          "note_text_length": [
	            6
	          ],
	          "setting": [
	            "7.b. Office"
	          ],
	          "epic_note_id": [
	            188190805
	          ],
	          "epic_note_csn_id": [
	            181431097
	          ],
	          "note_text": [
	            ".heart"
	          ],
	          "encounter_dept": [
	            "Unit 6C UMMC East Bank"
	          ]
	        },
	        "highlight": {
	          "note_text": [
	            ".<span class='hl'>heart</span>"
	          ]
	        }
	      },
	      {
	        "_index": "2011_notes_v1",
	        "_type": "note",
	        "_id": "88053023",
	        "_score": 3.378463,
	        "fields": {
	          "note_text_length": [
	            6
	          ],
	          "setting": [
	            "7.b. Office"
	          ],
	          "epic_note_id": [
	            88053023
	          ],
	          "epic_note_csn_id": [
	            93380368
	          ],
	          "note_text": [
	            ".heart"
	          ],
	          "encounter_dept": [
	            "Unit 6C UMMC East Bank"
	          ]
	        },
	        "highlight": {
	          "note_text": [
	            ".<span class='hl'>heart</span>"
	          ]
	        }
	      },
	      {
	        "_index": "2010_notes_v1",
	        "_type": "note",
	        "_id": "63648245",
	        "_score": 3.1524043,
	        "fields": {
	          "note_text_length": [
	            6
	          ],
	          "setting": [
	            "7.b. Office"
	          ],
	          "epic_note_id": [
	            63648245
	          ],
	          "epic_note_csn_id": [
	            69071602
	          ],
	          "note_text": [
	            ".heart"
	          ],
	          "encounter_dept": [
	            "Fairview Clinics Eagan"
	          ]
	        },
	        "highlight": {
	          "note_text": [
	            ".<span class='hl'>heart</span>"
	          ]
	        }
	      }
	    ]
	  },
	  "facets": {
	    "encDeptSpec": {
	      "_type": "terms",
	      "missing": 0,
	      "total": 11476966,
	      "other": 0,
	      "terms": [
	        {
	          "term": "Family Practice",
	          "count": 2844467
	        },
	        {
	          "term": "Unknown",
	          "count": 2293199
	        }
	      ]
	    }
	  }
	}