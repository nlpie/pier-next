package edu.umn.nlpie.pier.api

import edu.umn.nlpie.pier.api.exception.BadElasticRequestException
import edu.umn.nlpie.pier.api.exception.HttpMethodNotAllowedException
import edu.umn.nlpie.pier.api.exception.PierApiException
import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import grails.validation.ValidationException


@Secured(["ROLE_USER"])
class SearchController {//extends RestfulController {
	
	static responseFormats = ['json']
	//static allowedMethods = ["elastic": "GET", "find": "POST"]
	
	def elasticService
	def searchService
	def auditService
	
	//TODO refactor to superclass
	private respondWithException(Exception e) {
		def msg = e.message.replace('\n',' ')	//\n causes problems when client parses returned JSON
		respond ( e, status:e.status )
		//render(status: e.status, text: '{"message":"'+ msg +'"}', contentType: "application/json") as JSON
	}
	
	
	def index() { }	//default search view
	
	
	def search() { }	//deprecated 
	
	
	def elastic() {
		//println request.JSON.toString(2)
		def postBody = request.JSON
		def elasticResponse
		try {
			if ( request.method!="POST" ) throw new HttpMethodNotAllowedException(message:"issue GET instead")
			//TODO sanity check on request.JSON - needs query, url, searchRequest.id, etc
			//println postBody.toString(2)
			elasticResponse = elasticService.search( postBody.url, postBody.query )
			//println elasticResponse.json.toString(2)
			def status = elasticResponse.status
			if ( status==400 ) {
				throw new BadElasticRequestException( message:"Malformed query - check your syntax" )
			}
			if ( status==500 ) {
				throw new PierApiException( message:elasticResponse.json.error.root_cause.reason )
			}
			auditService.logQueryAndResponse( postBody, elasticResponse )
			respond elasticResponse.json 
		} catch( PierApiException e) {
			auditService.logException ( postBody, elasticResponse, e )
			respondWithException(e)
		} catch( ValidationException e) {
			auditService.logException ( postBody, e )
			respondWithException( new PierApiException( postBody, elasticResponse, e ) )
		} catch( Exception e) {
			auditService.logException ( postBody, elasticResponse, e )
			respondWithException( new PierApiException( message:e.message ) ) 
		} finally {
		
		}
		/*500 error from elastic when trying deep paging
		{
			"error": {
			  "root_cause": [
				{
				  "type": "query_phase_execution_exception",
				  "reason": "Result window is too large, from + size must be less than or equal to: [10000] but was [510000]. See the scroll api for a more efficient way to request large data sets. This limit can be set by changing the [index.max_result_window] index level parameter."
				}
			  ],
			  "type": "search_phase_execution_exception",
			  "reason": "all shards failed",
			  "phase": "query",
			  "grouped": true,
			  "failed_shards": [
				{
				  "shard": 0,
				  "index": "notes_v3",
				  "node": "YXHHd9aqSN-S9Pu33CIUXQ",
				  "reason": {
					"type": "query_phase_execution_exception",
					"reason": "Result window is too large, from + size must be less than or equal to: [10000] but was [510000]. See the scroll api for a more efficient way to request large data sets. This limit can be set by changing the [index.max_result_window] index level parameter."
				  }
				}
			  ]
			},
			"status": 500
		  }
		  */
	}
	
	def noteCount() {
		//println request.JSON.toString(2)
		def postBody = request.JSON
		def elasticResponse
		//try {
			if ( request.method!="POST" ) throw new HttpMethodNotAllowedException(message:"issue GET instead")
			//TODO sanity check on request.JSON - needs query, url, searchRequest.id, etc
			elasticResponse = elasticService.search( postBody.url, postBody.query )
			def status = elasticResponse.status
			//println "distinct: ${status} ${elasticResponse.json.toString(2)}"
			
			if ( status==400 ) {
				throw new BadElasticRequestException( message:"Malformed query - check your syntax" )
			}
			def distinctCount = searchService.logNoteIdCountInfo( postBody, elasticResponse )
			//respond elasticResponse.json
			respond distinctCount
	/*	} catch( PierApiException e) {
			//auditService.logException ( postBody, elasticResponse, e )
			respondWithException(e)
		} catch( ValidationException e) {
			//auditService.logException ( postBody, e )
			e.printStackTrace()
			respondWithException( new PierApiException( message:e.message ) )
		} catch( Exception e) {
			//auditService.logException ( postBody, elasticResponse, e )
			respondWithException( new PierApiException( message:e.message ) )
		} finally {
		
		}*/
	}
	
	def bucketCount() {
		//println request.JSON.toString(2)
		def postBody = request.JSON
		def elasticResponse
		//try {
			if ( request.method!="POST" ) throw new HttpMethodNotAllowedException(message:"issue GET instead")
			//TODO sanity check on request.JSON - needs query, url, searchRequest.id, etc
			elasticResponse = elasticService.search( postBody.url, postBody.query )
			def status = elasticResponse.status
			//println "distinct: ${status} ${elasticResponse.json.toString(2)}"
			
			if ( status==400 ) {
				throw new BadElasticRequestException( message:"Malformed query - check your syntax" )
			}
			def distinctCount = searchService.logBucketCountInfo( postBody, elasticResponse )
			//respond elasticResponse.json
			respond distinctCount
	/*	} catch( PierApiException e) {
			//auditService.logException ( postBody, elasticResponse, e )
			respondWithException(e)
		} catch( ValidationException e) {
			//auditService.logException ( postBody, e )
			e.printStackTrace()
			respondWithException( new PierApiException( message:e.message ) )
		} catch( Exception e) {
			//auditService.logException ( postBody, elasticResponse, e )
			respondWithException( new PierApiException( message:e.message ) )
		} finally {
		
		}*/
	}
	
	def scrollCount() {
		def postBody = request.JSON
		def elasticResponse
		//try {
			if ( request.method!="POST" ) throw new HttpMethodNotAllowedException(message:"issue GET instead")
			def scrollInitiationUrl = postBody.url + "?scroll=1m"
			//TODO sanity check on request.JSON - needs query, url, searchRequest.id, etc
			elasticResponse = elasticService.search( scrollInitiationUrl, postBody.query )
			def status = elasticResponse.status
			//println "distinct: ${status} ${elasticResponse.json.toString(2)}"
			
			if ( status==400 ) {
				throw new BadElasticRequestException( message:"Malformed query - check your syntax" )
			}
			def scrollCount = searchService.logScrollCountInfo( postBody, elasticResponse )
			//respond elasticResponse.json
			respond scrollCount
		/*} catch( PierApiException e) {
			println "SCROLL COUNT ERROR"
			println e.toString()
			//auditService.logException ( postBody, elasticResponse, e )
			respondWithException(e)
		} catch( ValidationException e) {
			//auditService.logException ( postBody, e )
			e.printStackTrace()
			respondWithException( new PierApiException( message:e.message ) )
		} catch( Exception e) {
			//auditService.logException ( postBody, elasticResponse, e )
			respondWithException( new PierApiException( message:e.message ) )
		} finally {
		
		}*/
	}
	
	/*
SELECT 'cirrhosis', dc.id as distinct_count_id, dc.registration_id, dc.terms, dc.label, 
(select bucket_count from distinct_count where terms='cirrhosis' and label=dc.label) as correct_spelling_count,
(select count(*) from bucket where distinct_count_id=dc.id) as potential_marginal_count, 
(select count(*) from bucket where distinct_count_id=dc.id 
	and `key` not in (
		select `key` from bucket where distinct_count_id=(
			select id from distinct_count where terms='cirrhosis' and label=dc.label
		)
	)
) as actual_marginal_count
FROM notes_next.distinct_count dc
where dc.label in ('Mrn', 'Note id')
and terms like '% NOT cirrhosis'
>>next iteration of the query<<
SELECT @term := 'plate';
SELECT @term as term, dc.id as distinct_count_id, dc.registration_id, dc.terms, dc.label,
(select hits from distinct_count where terms=@term and label=dc.label) as correct_spelling_count,
(select hits from distinct_count where terms like concat('% NOT ',@term) and label=dc.label) as potential_marginal_count,
(select hits from distinct_count where terms like concat('% NOT ',@term) and label=dc.label) as actual_marginal_count
FROM notes_next.distinct_count dc
where dc.label in ('Note Id')
and terms like concat('% NOT ',@term)
UNION ALL
SELECT @term, dc.id as distinct_count_id, dc.registration_id, dc.terms, dc.label, 
(select bucket_count from distinct_count where terms=@term and label=dc.label) as correct_spelling_count,
(select count(*) from bucket where distinct_count_id=dc.id) as potential_marginal_count, 
(select count(*) from bucket where distinct_count_id=dc.id 
	and `key` not in (
		select `key` from bucket where distinct_count_id=(
			select id from distinct_count where terms=@term and label=dc.label
		)
	)
) as actual_marginal_count
FROM notes_next.distinct_count dc
where dc.label in ('Mrn')
and terms like concat('% NOT ',@term)
	
	//common misspellings
	'serotonin'
	'seborrheic'
	'paroxysmal'
	'basilar'
	'chalazion'
	'chlamydia'
	'ascites'
	'cirrhosis'
	'abdomen'
	'aneurysm'
	'murmur'
	'warfarin'
	'blepharoplasty'
	
	
	//common terms
	'color'
	'procedure' incl 'proc' abbr? too many 13M
	'murmur'	too many 13M
	
	//ICS Request-based terms
	pancreatitis DONE
	[shirodkar] cerclage
	Ipilimumab NOT PRESENT
	anesthesia DONE
	imaging BAD
	MELANOMA DONE
	glioblastoma DONE
	mutation BAD

Title: Isonym-corrected Edit distance normalized word embeddings used as derived synonym expansion in IR queries
	does not address POS, good for any isonym-based NLP task

Spell checking approaches
https://www.quora.com/What-are-some-algorithms-of-spelling-correction-that-are-used-by-search-engines-For-example-when-I-used-Google-to-search-Google-imeges-it-prompted-me-Did-you-mean-Google-images
https://www.google.com/url?sa=t&rct=j&q=&esrc=s&source=web&cd=8&cad=rja&uact=8&ved=0ahUKEwiVvJC1xLHWAhUl5YMKHYZ1Ds8QFghPMAc&url=http%3A%2F%2Fnorvig.com%2Fspell-correct.html&usg=AFQjCNFXyjXSOFM6ijpCKqBaD8S_Rx0kvw Norvig

	this is not a simple sort of word frequency - has the advantage being comprehensive in that score determines relevance, not absolute frequency
	this approach can produce an isonym map by tokenizing all UMLS tokens, scoring and taking the isonym-corrected list as the set of terms mapping to the correct spelling, doesn't work well on short, common words ( e.g. what )
expansion formula: 
	ct = correctly spelled term/token (stemmed) asList
	ea = expansion arraylist
	isonym(pt) in sts [all suggested terms with score >-0.37] 
		ea << pt
	ea = ea - ct
    expansion = ( ea.join(" OR") ) NOT ct
    
    
    
    
	*/
	
	
	def historySummary() {
		def jsonBody = request.JSON
		//TODO put exception handling in place
		respond searchService.searchHistory( jsonBody.excludeMostRecent )	//projection 
	}
	
	def savedQueries() {
		//TODO put exception handling in place
		respond searchService.savedQueries()
	}
	
	
	def recentQuery() {
		JSON.use ('recent.query') {
			respond searchService.recentQuery(params.id)
		}
	}
	
	
	def related() {
		def jsonBody = request.JSON
		respond elasticService.fetchRelated(jsonBody.url, jsonBody.term).json
	}
	
}
