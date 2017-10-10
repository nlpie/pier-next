import DocumentsResponse from './DocumentsResponse';
import AggregationsResponse from './AggregationsResponse';
import DocumentQuery from './elastic/DocumentQuery';
import AggregationQuery from './elastic/AggregationQuery';
import BucketCountQuery from './elastic/BucketCountQuery';
import CardinalityOnlyQuery from './elastic/CardinalityOnlyQuery';
import SingleFieldScrollCountQuery from './elastic/SingleFieldScrollCountQuery';
import TermsAggregation from './elastic/TermsAggregation';
import Pagination from './Pagination';
import TermFilter from './elastic/TermFilter';

class Search {
	    constructor( $http, growl, searchService, uiService ) {
    	this.$http = $http;
    	this.growl = growl;
    	this.searchService = searchService;
    	this.uiService = uiService;
    	
    	this.userInput = "heart";//"iliitis";
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
        	//searchingDocs: false,
        	//computingAggs: false,
        	uuid: undefined,
        	version: 0
        }
        this.cuiExpansionEnabled = false;				
		this.similarityExpansionEnabled = false; 
		this.cuiExpansion = {};				//{ heart:[], valve:[] } or { enabled:false, expansionMap: { heart:[], valve:[] } }
		this.relatednessExpansion = {}; 		//{ heart:[], valve:[] } or { enabled:false, expansionMap: { heart:[], valve:[] } 
		
		this.options = {
    		relatednessExpansion : {
    			on : false,
    			style: {}
    		}
    	}
    }
	    
    toggleRelatednessExpansion() {
    	console.log("togtoggleRelatednessExpansiongle");
    	this.options.relatednessExpansion.on = !this.options.relatednessExpansion.on;
    	if ( this.options.relatednessExpansion.on ) {
    		this.options.relatednessExpansion.style = { 'color':'green' };
    	} else {
    		this.options.relatednessExpansion.style = { };
    	}
    }
    
    dirty( corpus ) {
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
	done() {
		//clean applies to all corpora, no need to sniff for target corpus
		this.searchIconClass = "fa fa-search";
		for (let corpus of this.context.candidateCorpora) {
			corpus.opacity = this.resultsOpacity.bright;
		}
	}
    
    //adds filters to corpus.appliedFilters object (not array)
	addFilter(corpus, aggregation, value) {
    	if ( !corpus.appliedFilters[aggregation.label] ){
    		corpus.appliedFilters[aggregation.label] = [];
    	}
    	corpus.appliedFilters[aggregation.label].push( new TermFilter(aggregation.fieldName,value) );
    	this.dirty( corpus );
    	//alert(JSON.stringify(corpus.appliedFilters));
    }

    setContext(searchContext) {
    	//if (!searchContext) return;	
    	//for some reason this function gets invoked with undefined searchContext on change of contexts dropdown; 
    	//this check and immediate return prevents console errors, otherwise the app appears to work as expected
    	this.context = searchContext;
    	this.status.version++;
    	this.clearResults();
    	for ( let corpus of this.context.candidateCorpora ) {
    		if ( corpus.metadata.searchable ) {
    			if ( corpus.metadata.filtered ) {
    				corpus.contextFilter = new TermFilter(corpus.metadata.contextFilterField, this.context.contextFilterValue);	//contextFilter specific to each searchable corpus
    				//alert(JSON.stringify(corpus.contextFilter));
    			}
    			if ( !corpus.appliedFilters ) {
    				corpus.appliedFilters = {};
    				corpus.opacity = this.resultsOpacity.bright;
    				corpus.pagination = new Pagination();
    				corpus.results = {};
    				this.done();
    			}
				this.availableAggregations( corpus )
	    			.then( function(response) {
	    				corpus.metadata.aggregations = response.data;
	    			})
	    			.then( function() {
	    				//console.log("Search.js " + JSON.stringify(searchContext,null,'\t'));
	    			})
	    			.then( function() {
	    				console.log("AUTHORIZED CONTEXT SET");
	    				//alert(JSON.stringify(corpus));
	    			}); 
			}
    	}
    	this.searchService.fetchHistory( false );
    }
    
    availableAggregations( corpus ) {
    	//gets aggregation filters based on user prefs 
    	return this.$http.get( APP.ROOT + '/config/corpusAggregationsByType/' + corpus.id, { cache:false } );
    	//https://coderwall.com/p/40axlq/power-up-angular-s-http-service-with-caching
    }
    
    clearResults() {
    	this.searchIconClass = "fa fa-search";
    	for ( let corpus of this.context.candidateCorpora ) {
    		corpus.results = {};
    		//corpus.metadata.aggregations = {};
    	}
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
    	this.clearResults();
    	this.register()
    		.then( function(response) {
    			me.assignRegistration(response.data);
    			me.execute();
    		});
    }
    
    conductPastSearch( registrationId ) {
    	//use registrationId to lookup registration to get the auth context label
    	var me = this;
    	this.clearResults();
    	this.searchService.fetchRegisteredSearch( registrationId )
    		.then( function( response ) { 
    			var registeredSearch = response.data;	//temp assignment hold return for use in the next  couple of .thens
    			me.uiService.fetchAuthorizedContextByLabel( registeredSearch.authorizedContext )
    				.then( function( response ) { 
    					me.setContext( response.data );
    					me.parsePastSearchRegistration( registeredSearch );
    					me.register()
    		    			.then( function(response) {
    		    				me.assignRegistration(response.data);
    		    			});
    					me.reExecute( registeredSearch );
    			});
    		});
    }
    
    parsePastSearchRegistration( searchRegistration ) {
    	
    	var elasticQuery = JSON.parse( searchRegistration.queries[0].query ); 	//doesn't matter which query, all will have used the same querystring query
    	console.log(elasticQuery);
    	this.userInput = elasticQuery.query.bool.must.query_string.query;
    	var filters = elasticQuery.query.bool.filter;	//returns an array
    	console.log(filters);
    	//this.prepareCorpora();//find corpus and put filters on it (corpus.appliedFilters=?)
    	//assign parsed query here
    }
    
    execute() {
    	//pagination.notesPerPage and .offset are set in DocumentQuery.js
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
	    					try {
	    						me.remoteError("docs",e);
	    					} catch(e) {
	    						throw(e)
	    					}
	    				})
						.finally( function() {
							corpus.status.searchingDocs = false;
						});
					
					this.fetchAggregations( corpus )
	    				.then( function(response) {
		    				me.assignAggregationsResponse( corpus,response );
	    				}).then( function( maxBuckets ) {
	    					var maxCount = corpus.results.aggs.total;
	    					console.log("max distinct count " + maxCount);
		    				if ( me.options.relatednessExpansion.on ) me.distinctCounts( corpus, maxCount );
		    			})
	    				.catch( function(e) {
	    					//catches non-200 status errors
	    					me.remoteError("aggs",e);
	    				})
						.finally( function() {
	    					corpus.status.computingAggs = false;
	    					//console.log(JSON.stringify(corpus.results.docs.hits[0],null,'\t'));
	    					//console.log(JSON.stringify(corpus.results.aggs.aggs,null,'\t'));
	    					//console.log(JSON.stringify(corpus.results.docs,null,'\t'));
	    				});
					
    			} catch(e) {
    				//javascript error
    				alert("ERROR");
					me.error("full",e);
				}
			}
		}
    	this.done();
    	this.searchService.fetchHistory( true );
    }
    
    reExecute( searchRegistration ) {
    	//notesPerPage [and .offset] need to be parsed from searchRegistration and put on uiState
    	var me = this;
    	var defaultCorpusSet = false;
    	for ( let corpus of this.context.candidateCorpora ) {
    		if ( corpus.metadata.searchable ) {}
    			for ( let q of searchRegistration.queries )	{
    				if ( q.corpus==corpus.name && q.type=="document" ) {
		    			try {
		    				this.$http.post( APP.ROOT + '/search/elastic/', JSON.stringify( {"registration.id":this.registration.id, "corpus": corpus.name, "type":"document", "url":corpus.metadata.url, "query": JSON.parse(q.query) } ) ) 
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
									corpus.status.searchingDocs = false;
								});
		    			} catch(e) {
		    				//need  better return from controller, json response if !=200
							me.error("full",e);
						}
    				} else if ( q.corpus==corpus.name && q.type=="aggregation" ) {
    					try {
		    				this.$http.post( APP.ROOT + '/search/elastic/', JSON.stringify( {"registration.id":this.registration.id, "corpus": corpus.name, "type":"aggregation", "url":corpus.metadata.url, "query": JSON.parse(q.query) } ) ) 
			    				.then( function(response) {
				    				me.assignAggregationsResponse( corpus,response );
			    				})
			    				.catch( function(e) {
			    					//catches non-200 status errors
			    					me.remoteError("aggs",e);
			    				})
								.finally( function() {
			    					corpus.status.computingAggs = false;
			    				});	
		    			} catch(e) {
		    				//need  better return from controller, json response if !=200
							me.error("full",e);
						}
    				}
    			}
			}
    	this.done();
    	this.searchService.fetchHistory( true );
    }
    
    error( div,e ) {
    	//alert(e.toString());
    	this.growl.error( e.toString(), {ttl:10000, referenceId:div} );
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
    	//console.log(JSON.stringify(corpus,null,'\t'));
    	corpus.status.searchingDocs = true;
    	var url = corpus.metadata.url;
    	var docsQuery = new DocumentQuery(corpus, this.userInput, this.pagination);
		return this.$http.post( APP.ROOT + '/search/elastic/', JSON.stringify( {"registration.id":this.registration.id, "corpus": corpus.name, "type":"document", "url":url, "query": docsQuery} ) );
	}
    fetchAggregations( corpus ) {
    	//return promise and let the client resolve it
    	corpus.status.computingAggs = true;
    	var url = corpus.metadata.url;
    	var aggsQuery = new AggregationQuery(corpus, this.userInput);
    	//alert(JSON.stringify(aggsQuery));
		return this.$http.post( APP.ROOT + '/search/elastic/', JSON.stringify( {"registration.id":this.registration.id, "corpus": corpus.name, "type":"aggregation", "url":url, "query": aggsQuery} ) );
    }
    
    assignDocumentsResponse(corpus,response) {
    	//console.log(JSON.stringify(response,null,'\t'));
    	corpus.results.docs = new DocumentsResponse(response.data);
    }
    assignAggregationsResponse(corpus,response) {
    	corpus.results.aggs = new AggregationsResponse(response.data);
    }
    
    distinctCounts( corpus, maxCount ) {
    	var url = corpus.metadata.url;
    	var aggregations = corpus.metadata.aggregations;
    	var me = this;
    	Object.keys(aggregations).map( function(key,index) {
    		var aggregationCategory = corpus.metadata.aggregations[key];
    		for (const aggregation of aggregationCategory) {
    			if ( aggregation.countDistinct ) {
    				var countType = ( maxCount<=15000000 ) ? "bucket" : "scroll";	//TODO externalize maxBuckets threshold
    				var query;
    				if ( countType=='scroll' ) query = new SingleFieldScrollCountQuery( corpus, me.userInput, aggregation.label, aggregation.fieldName );
    				if ( countType=='bucket' ) {
    					if ( aggregation.fieldName=='note_id' ) {
    						query = new CardinalityOnlyQuery( corpus, me.userInput, aggregation.label, aggregation.fieldName );
    					} else {
    						query = new BucketCountQuery( corpus, me.userInput, aggregation.label, aggregation.fieldName, maxCount );
    					}
    				}
    				var payload = { 
    					"registration.id": me.registration.id,
    					"corpus": corpus.name, 
    					"countType": countType, 
    					"label": aggregation.label, 
    					"url": url, 
    					"query": query
    				};
    				if ( countType=="bucket" ) {
    					alert(JSON.stringify( payload, null, '\t' ));
		    			if ( aggregation.fieldName=="note_id" ) {		    				
		    				me.$http.post( APP.ROOT + '/search/noteCount/', JSON.stringify( payload ) )
		    				.then( function(response) {
		    					me.assignDistinct( aggregation, response );
		    				});
		    			} else {
		    				me.$http.post( APP.ROOT + '/search/bucketCount/', JSON.stringify( payload ) )
		    				.then( function(response) {
		    					me.assignDistinct( aggregation, response );
		    				});
		    			}
    				} else { //scroll count
    					//add scrollUrl to payload
    					payload.scrollUrl = corpus.metadata.scrollUrl;
    					//alert(JSON.stringify( payload, null, '\t' ));
    					try {
    						me.$http.post( APP.ROOT + '/search/scrollCount', JSON.stringify( payload ) )
    							.then( function(response) {
    								me.assignDistinct( aggregation, response );
    							});
    					} catch(e) {
    						throw(e);
    					}
    				}
    			}
    		}
    		
    	});
    }
    assignDistinct( aggregation, response ) {
    	aggregation.countType = response.data.countType;
    	aggregation.count = response.data.count;
    	aggregation.cardinalityEstimate = response.data.cardinalityEstimate;
		console.log("distinct " + aggregation.label + " took " + response.data.took + " returning " + response.data.countType + "/cardinality counts of: " + response.data.count + "/" + response.data.cardinalityEstimate);
    }
    
}

Search.$inject = [ '$http', 'growl', 'searchService', 'uiService' ];

export default Search;