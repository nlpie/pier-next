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
    	
		this.resultsOpacity = {
			dimmed: { 'opacity': 0.2 },
			bright: { 'opacity': 1 }
		}
		this.searchIconClass = "";
		
        this.status = {
        	error: undefined,
        	dirty: false,
        	searchingDocs: false,
        	computingAggs: false
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
    	alert(JSON.stringify(corpus.appliedFilters));
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
    			corpus.contextFilter = new TermFilter(corpus.metadata.contextFilterField, this.context.contextFilterValue);	//contextFilter specifi to each searchable corpus
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
    
    availableAggregations( corpus ) {
    	//gets aggregation filters based on user prefs 
    	return this.$http.get( APP.ROOT + '/config/corpusAggregationsByType/' + corpus.id, { cache:false } );
    	//https://coderwall.com/p/40axlq/power-up-angular-s-http-service-with-caching
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
	    					me.remoteError("full",e);
	    				})
						.finally( function() {
							me.status.searchingDocs = false;
						});
					
					this.fetchAggregations( corpus )
	    				.then( function(response) {
		    				if ( response.status!=200 ) throw response.data.type;
		    				me.assignAggregationsResponse( corpus,response );
	    				})
	    				.catch( function(e) {
	    					//catches non-200 status errors
	    					me.remoteError("full",e);
	    				})
						.finally( function() {
	    					me.status.computingAggs = false;
	    				});	
					
    			} catch(e) {
					me.error("full",e);
				}
			}
		}
    	
    }
    
    error( div,e ) {
    	//alert(e.toString());
    	this.growl.error( e.toString(), {ttl:5000, referenceId:div} );
    }
    remoteError( div,e ) {
    	//alert(JSON.stringify(e,null,'\t'));
    	this.growl.error( e.statusText + " (" + e.status + ") <<  " + e.data.error.root_cause[0].type + "::" + e.data.error.root_cause[0].reason, {ttl:5000, referenceId:div} );
    }
    
    searchCorpus( corpus ) {
		//return promise and let the client resolve it
    	this.status.searchingDocs = true;
    	var url = corpus.metadata.url;
    	var docsQuery = new DocumentQuery(corpus, this.userInput, this.pagination);
		return this.$http.post( APP.ROOT + '/search/elastic/', JSON.stringify( {"url":url, "elasticQuery": docsQuery} ) );
	}
    fetchAggregations( corpus ) {
    	//return promise and let the client resolve it
    	this.status.computingAggs = true;
    	var url = corpus.metadata.url;
    	var aggsQuery = new AggregationQuery(corpus, this.userInput);
    	aggsQuery = this.addAggregations(aggsQuery,corpus);
    	//alert(JSON.stringify(aggsQuery));
		return this.$http.post( APP.ROOT + '/search/elastic/', JSON.stringify( {"url":url, "elasticQuery": aggsQuery} ) );
    }
    
    assignDocumentsResponse(corpus,response) {
    	//if ( !this.results[corpus.name] ) this.results[corpus.name] = {};
    	//this.results[corpus.name].docs = 
    	corpus.results.docs = new DocumentsResponse(response.data);
    	corpus.opacity = this.resultsOpacity.bright;
    	this.searchIconClass = "fa fa-search";
    	//corpus.dirty = true;
    	/*{"_shards":{"total":6,"failed":0,"successful":6},"hits":{"hits":[],"total":128,"max_score":0.0},"took"
    		:613,"timed_out":false,"aggregations":{"KoD":{"doc_count_error_upper_bound":0,"sum_other_doc_count":0
    		,"buckets":[{"doc_count":128,"key":"7. Note"}]}}}
    	*/
    	
    }
    assignAggregationsResponse(corpus,response) {
    	//if ( !this.results[corpus.name] ) this.results[corpus.name] = {};
    	//this.results[corpus.name].aggs = new AggregationsResponse(response.data);
    	corpus.results.aggs = new AggregationsResponse(response.data);
    }
    
    addAggregations(aggsQuery,corpus) {
    	var aggregations = corpus.metadata.aggregations;
    	Object.keys(aggregations).map( function(key,index) {
    		var aggregationCategory = corpus.metadata.aggregations[key];
    		for (const aggregation of aggregationCategory) {
    			//alert(JSON.stringify(aggregation,null,'\t'));
    			aggsQuery.aggs.add( aggregation.label, new TermsAggregation(aggregation.fieldName,	aggregation.numberOfFilterOptions) );
    		}
    		
    	} );
    	return aggsQuery
    }
    
    clearResults() {
    	this.results = {};
    }
    
}

Search.$inject = [ '$http', 'growl' ];

export default Search;