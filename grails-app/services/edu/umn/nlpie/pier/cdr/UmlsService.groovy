package edu.umn.nlpie.pier.cdr

import grails.transaction.Transactional

@Transactional
class UmlsService {
	
	def elasticService

    def umlsStringFromDb( cui ) {
		//Umls.findByCuiAndTsAndIspref( cui.substring(0,8), 'P', 'Y'  )
		//Umls.findByCuiAndTs( cui.substring(0,8), 'P'  )
		Umls.findByCui( cui.substring(0,8)  )
		/*returns:
{
    "class": "edu.umn.nlpie.pier.cdr.Umls",
    "id": null,
    "aui": "A22844017",
    "cui": "C0457798",
    "ispref": "N",
    "sab": "SNOMEDCT_US",
    "str": "Dermatitis of eyelid",
    "ts": "P"
}
		 */
		
    }
	
	def umlsStringFromIndex( cui ) {
		elasticService.cui( cui.substring(0,8) )
		/*returns:
		{
			"hits": {
				"total": 2,
				"hits": [
					{
						"_type": "entry",
						"_source": {
							"sui": "Other specified disorders of urethra ",
							"cui": "C0348769",
							"semantic_type": "T047",
							"vocab": "SNOMEDCT_US"
						},
						"_id": "AWPL4h_PXIjz5q-hnb4g",
						"_index": "umls_2013",
						"_score": 14.580928
					},
					{
						"_type": "entry",
						"_source": {
							"sui": "Oth spcf dsdr urethra",
							"cui": "C0348769",
							"semantic_type": "T047",
							"vocab": "ICD9CM"
						},
						"_id": "AWPL7MwCXIjz5q-hoA-P",
						"_index": "umls_2013",
						"_score": 14.580928
					}
				],
				"max_score": 14.580928
			},
			"_shards": {
				"total": 1,
				"failed": 0,
				"successful": 1
			},
			"timed_out": false,
			"took": 2
		}*/
	}
}
