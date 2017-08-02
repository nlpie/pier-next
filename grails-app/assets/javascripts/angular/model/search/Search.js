import DocumentsResponse from './DocumentsResponse';
import AggregationsResponse from './AggregationsResponse';
import DocumentQuery from './elastic/DocumentQuery';
import AggregationQuery from './elastic/AggregationQuery';
import TermsAggregation from './elastic/TermsAggregation';
import Pagination from './Pagination';
import TermFilter from './elastic/TermFilter';
import Service from '../../model/search/Search';

class Search {
    constructor( $http, growl ) {
    	this.$http = $http;
    	this.growl = growl;
    	this.userInput = "heart";
    	this.context = undefined;
    	this.registration = undefined;
    	
		this.resultsOpacity = {
			dimmed: { 'opacity': 0.2 },
			bright: { 'opacity': 1 }
		}
		this.searchIconClass = "";
		
        this.status = {
        	error: undefined,
        	dirty: false,
        	searchingDocs: false,
        	computingAggs: false,
        	uuid: undefined
        }
        this.cuiExpansionEnabled = false;				
		this.similarityExpansionEnabled = false; 
		this.cuiExpansion = {};				//{ heart:[], valve:[] } or { enabled:false, expansionMap: { heart:[], valve:[] } }
		this.relatednessExpansion = {}; 		//{ heart:[], valve:[] } or { enabled:false, expansionMap: { heart:[], valve:[] } 
		
		//this.results = {};	//object containing key:SearchResponse{}
    }
    
    dirty(corpus) {
		this.searchIconClass = "fa fa-refresh fa-spin";
		if (corpus) {
			//dim only this corpus
			corpus.opacity = this.resultsOpacity.dimmed;
		} else {
			//dim doc results for all copora
			for (let corpus of this.context.candidateCorpora) {
				corpus.opacity = this.resultsOpacity.dimmed;
			}
		}
	}
	clean() {
		//clean applies to all corpora, no need to sniff for target corpus
		this.searchIconClass = "fa fa-search";
		for (let corpus of this.context.candidateCorpora) {
			corpus.opacity = this.resultsOpacity.bright;
		}
	}
    
    addFilter(corpus, aggregation, value) {
    	if ( !corpus.appliedFilters[aggregation.label] ){
    		corpus.appliedFilters[aggregation.label] = [];
    	}
    	corpus.appliedFilters[aggregation.label].push( new TermFilter(aggregation.fieldName,value) );
    	this.dirty(corpus);
    	//alert(JSON.stringify(corpus.appliedFilters));
    }

    setContext(searchContext) {
    	if (!searchContext) return;	
    	//for some reason this function gets invoked with undefined searchContext on change of contexts dropdown; 
    	//this check and immediate return prevents console errors, otherwise the app appears to work as expected
    	this.context = searchContext;
    	this.clearResults();
    	var me = this;
    	for ( let corpus of this.context.candidateCorpora ) {
    		if ( corpus.metadata.searchable ) {
    			corpus.contextFilter = new TermFilter(corpus.metadata.contextFilterField, this.context.contextFilterValue);	//contextFilter specific to each searchable corpus
    			if ( !corpus.appliedFilters ) {
    				corpus.appliedFilters = {};
    				corpus.opacity = this.resultsOpacity.bright;
    				corpus.pagination = new Pagination();
    				corpus.results = {};	//object containing key:SearchResponse{}
    				this.clean();
    			}
				this.availableAggregations( corpus )
	    			.then( function(response) {
	    				corpus.metadata.aggregations = response.data;
	    			});
				corpus.metadata.appliedFilters = [];	//array to hold user-selected Term filter objects used to refine query results 
			}
    	}
    }
    
    /*newUuid() {
    	return this.$http.post( APP.ROOT + '/config/uuid' );
    }
    assignUuid() {
    	//alert(JSON.stringify(response));
    	var me = this;
    	this.newUuid()
    		.then( function(response) { 
    			//alert(JSON.stringify(response));
    			me.status.uuid = response.data.uuid; 
    		})
    		.catch( function(e) {
    			me.remoteError("full",e);
    		});
    }
    */
    
    availableAggregations( corpus ) {
    	//gets aggregation filters based on user prefs 
    	return this.$http.get( APP.ROOT + '/config/corpusAggregationsByType/' + corpus.id, { cache:false } );
    	//https://coderwall.com/p/40axlq/power-up-angular-s-http-service-with-caching
    }
    
    clearResults() {
    	this.results = {};
    }
    
    
/*    
    GET _search
    {
      "query": { 
        "bool": { 
          "must": [
            { "match": { "title":   "Search"        }}, 
            { "match": { "content": "Elasticsearch" }}  
          ],
          "filter": [ 
            { "term":  { "status": "published" }}, 
            { "range": { "publish_date": { "gte": "2015-01-01" }}} 
          ]
        }
      }
    }
*/
    conduct() {
    	var me = this;
    	this.register()
    		.then( function(response) {
    			me.assignRegistration(response.data);
    			me.execute();
    		});
    }
    
    execute() {
    	//pagination.notesPerPage and .offset come from this.pagination
    	this.clearResults();
    	var me = this;
    	var defaultCorpusSet = false;
    	for ( let corpus of this.context.candidateCorpora ) {
    		if ( corpus.metadata.searchable ) {
    			try {
					this.searchCorpus( corpus )
		    			.then( function(response) {
		    				me.assignDocumentsResponse( corpus,response );
		    				//client-side setting of default corpus, probably should be a property of corpus type
		    				if ( !defaultCorpusSet ) {
		    					corpus.selected = true;
		    					defaultCorpusSet = true;
		    				} else {
		    					corpus.selected = false;
		    				}
		    			})
		    			.catch( function(e) {
	    					//catches non-200 status errors
	    					me.remoteError("docs",e);
	    				})
						.finally( function() {
							me.status.searchingDocs = false;
						});
					
					this.fetchAggregations( corpus )
	    				.then( function(response) {
		    				me.assignAggregationsResponse( corpus,response );
	    				})
	    				.catch( function(e) {
	    					//catches non-200 status errors
	    					me.remoteError("aggs",e);
	    				})
						.finally( function() {
	    					me.status.computingAggs = false;
	    				});	
					
    			} catch(e) {
    				//need  better return from controller, json response if !=200
					me.error("full",e);
				}
			}
		}
    	this.clean()
    }
    
    error( div,e ) {
    	//alert(e.toString());
    	this.growl.error( e.toString(), {ttl:5000, referenceId:div} );
    }
    remoteError( div,e ) {
    	console.log(JSON.stringify(e,null,'\t'));
    	//this.growl.error( e.statusText + " (" + e.status + ") <<  " + e.data.error.root_cause[0].type + "::" + e.data.error.root_cause[0].reason, {ttl:5000, referenceId:div} );
    	//TODO ensure bad elastic queries are captured
    	this.growl.error( e.statusText + " (" + e.status + ") - " + e.data.message, {ttl:10000, referenceId:div} );
    }
    
    register() {
    	return this.$http.post( APP.ROOT + '/audit/register/', JSON.stringify( { "authorizedContext":this.context.label } ) );
    }
    assignRegistration(data) {
    	this.registration = data; 	//TODO fluff up a RegistrationResponse object
    }
    
    searchCorpus( corpus ) {
		//return promise and let the client resolve it
    	this.status.searchingDocs = true;
    	var url = corpus.metadata.url;
    	var docsQuery = new DocumentQuery(corpus, this.userInput, this.pagination);
		return this.$http.post( APP.ROOT + '/search/elastic/', JSON.stringify( {"queryLog.id":this.registration.id, "type":"document", "url":url, "query": docsQuery} ) );
	}
    fetchAggregations( corpus ) {
    	//return promise and let the client resolve it
    	this.status.computingAggs = true;
    	var url = corpus.metadata.url;
    	var aggsQuery = new AggregationQuery(corpus, this.userInput);
    	//alert(JSON.stringify(aggsQuery));
		return this.$http.post( APP.ROOT + '/search/elastic/', JSON.stringify( {"queryLog.id":this.registration.id, "type":"aggregation", "url":url, "query": aggsQuery} ) );
    }
    
    assignDocumentsResponse(corpus,response) {
    	corpus.results.docs = new DocumentsResponse(response.data);
    	/*{"_shards":{"total":6,"failed":0,"successful":6},"hits":{"hits":[],"total":128,"max_score":0.0},"took"
    		:613,"timed_out":false,"aggregations":{"KoD":{"doc_count_error_upper_bound":0,"sum_other_doc_count":0
    		,"buckets":[{"doc_count":128,"key":"7. Note"}]}}}
    	*/
    	
    }
    assignAggregationsResponse(corpus,response) {
    	corpus.results.aggs = new AggregationsResponse(response.data);
    }
    
}

Search.$inject = [ '$http', 'growl' ];

export default Search;