import DocumentsResponse from '../model/rest/response/DocumentsResponse';
import AggregationsResponse from '../model/rest/response/AggregationsResponse';
import TermsAggregation from '../model/search/elastic/TermsAggregation';
import TermFilter from '../model/search/elastic/TermFilter';
import AuthorizedContextsResponse from '../model/rest/response/AuthorizedContextsResponse';
import AuthorizedContext from '../model/ui/AuthorizedContext';
import CorpusAggregationsResponse from '../model/rest/response/CorpusAggregationsResponse';
import InputExpansion from '../model/search/InputExpansion';
import SearchInstance from '../model/search/SearchInstance';
import SearchPayload from '../model/rest/request/SearchPayload';
import CountPayload from '../model/rest/request/CountPayload';

class Search {
    constructor( $http, $q, growl, searchService ) {
    	this.$http = $http;
    	this.$q = $q;
    	this.growl = growl;
    	this.searchService = searchService;
    	
    	this.authorizedContexts = undefined;

    	this.userInput = "iliitis";
    	this.context = undefined;
    	//this.payload = undefined; 
    	
    	this.inputExpansion = new InputExpansion(); 
    	this.instance = new SearchInstance();
		this.searchIconClass = "fa fa-search fa-spin";
		
        this.status = {
        	error: undefined,
        	dirty: false,
        	exporting: false
        }
        this.init();
        console.info("Search.js complete");
		
		this.template = "myPopoverTemplate.html"
        
    }
    
    init() {
    	let me = this;
    	this.searchService.fetchContexts()
    		.then ( function(response) {
    			me.authorizedContexts = new AuthorizedContextsResponse( response.data );
    			me.setContext( me.authorizedContexts[0] )
    			.then( function(response) {
    				me.searchService.fetchHistory();
    				me.searchService.fetchSavedQueries( me.context.label );
    			});
    		});
    }
    
    //TODO right level of abstraction? - move to Corpus?
    dirty( corpus ) {
    	//search and corpus need to be set to dirty
//alert("dirty: " + this.status.dirty);
    	if ( this.status.dirty==false ) {
    		this.status.dirty = true;
			this.searchIconClass = "fa fa-refresh fa-spin";
			if ( corpus ) {
				//dim only this corpus
				corpus.dim();
				corpus.removeCounts();
			} else {
				//dim doc results for all copora
				for (let corpus of this.context.corpora) {
					corpus.dim();
					corpus.removeCounts();
				}
			}
    	}
	}

    setContext(searchContext) {
//alert("setContext");
    	this.context = searchContext;
    	this.clearResults();
    	let activeSet = false;
    	let tasks = [];
    	let me = this;
    	let corpusThatNeedsAggs = undefined;
    	//identify active corpora and specific corpus needing aggregations 
    	for ( let corpus of this.context.corpora ) {
    		if ( corpus.metadata.searchable ) {
    			corpusThatNeedsAggs = corpus;
//alert("corpus in setContext\n"+JSON.stringify(corpus,null,'\t'));
    			if ( !activeSet ) {
    				this.searchService.setActiveCorpus( corpus, this.context.corpora );	//TODO refactor active assignments to a user pref?
    				activeSet = true;
    			}
    			if ( corpus.metadata.filtered ) {
    				corpus.contextFilter = new TermFilter(corpus.metadata.contextFilterField, this.context.contextFilterValue);	//contextFilter specific to each searchable corpus
//alert(JSON.stringify(corpus.contextFilter));
    			}
				tasks.push ( "placeholder"
					//this.corpusAggregations( corpus )
	    			/*.then( function(response) {
	    				corpus.metadata.aggregations = new CorpusAggregationsResponse( response.data );
	    				console.log("aggregations set for " + corpus.name);
	    			})*/
				);
			}
    	}
    	//tasks.push( this.searchService.fetchHistory() );
    	console.log("Auth context set to: " + searchContext.label);
    	
    	return this.corpusAggregations( corpusThatNeedsAggs )
    	.then( function(response) {
    		corpusThatNeedsAggs.metadata.aggregations = new CorpusAggregationsResponse( response.data );
//alert("corpus in corpusAggregations\n"+JSON.stringify(corpusThatNeedsAggs,null,'\t'));
	    	console.log("aggregations set for " + corpusThatNeedsAggs.name);
	    	me.searchService.fetchSavedQueries( searchContext.label );
	    	return me.$q.when( me );
	    });
    }
    

    //----------------- refactored search ------------------------------
    //each should return a promise? or be its own self-contained process that returns a promise?
    
    complete( search ) {
//alert("complete");
    	if ( !search ) search = this;	//invocation of this complete() may not be in promise chain that passes search object
    	search.searchIconClass = "fa fa-search";
    	search.status.dirty = false;
    	return search.$q.when( search );
	}
    
    e() { 
    	let me = this;
    	this.r("DEFAULT")
		.then( me.searchCorpora )
		.then( function(search) {
			me.finish( search );
		})
		.catch( function(e) {
			me.clearResults();
			me.remoteError("docs",e);
		});
    }
    
    finish( search ) {
//alert("finish");
		let me = this;
    	this.dec(search)	
		.then( me.complete )		
<<<<<<< HEAD
		.then( me.searchService.fetchHistory() );
=======
		.then( me.searchService.fetchHistory() )
>>>>>>> db35fe8c0db1ca3ccb2ffd2ad5a072252163706f
    }
    
    
    fetchAndParsePreviousQueryById( queryId ) {
    	//fetch past queries, set actual queries on currentSearch.instance.recent obj, fetch context, set context, parse
    	let me = this;	//currentSearch
    	let pastQueries = undefined;
    	let pastQueryInfo = undefined;
    	this.searchService.fetchPreviousQuery( queryId )	//returns docs and aggs queries
    		.then( me.parsePastQueryIntoInstance )
    		.then( me.$q.when( me ) );
    }
    
    parsePastQueryIntoInstance( pastQueriesResponse ) { 
    	//returns authorizedContext name

    	let me = this;
    	let pastQueries = pastQueriesResponse.data;
    	let pastQueryInfo = pastQueries.docsQuery;
    	let pastDocsQuery = JSON.parse(pastQueries.docsQuery.query);
    	let pastAggsQuery = JSON.parse(pastQueries.aggsQuery.query);
    	let recentExpansionTerms = JSON.parse(pastQueryInfo.expansionTerms);
    	
		me.userInput = pastQueryInfo.userInput;
		me.instance.recent.docsQuery = pastDocsQuery;
		me.instance.recent.aggsQuery = pastAggsQuery;
		me.inputExpansion.on = pastQueryInfo.inputExpansion;
		if ( me.inputExpansion.on ) {
			me.inputExpansion.terms = recentExpansionTerms;
		}

		if ( pastQueryInfo.distinctCounts ) me.instance.distinctCountsOn();
		return this.$q.when( me )
		//return me.$q.when( me );
    }
    
    applyPastFilters( search ) {
    	for ( let corpus of search.context.corpora ) {
    		corpus.parsePastQueryFilterArray( pastQuery.query.bool.filter )
    	}
    	return search.$q.when( search );
    }
    
    recentSearch( query ) {
    	let me = this;
    	this.clearResults();
    	this.inputExpansion = new InputExpansion();
    	let recentQuery = undefined;
    	let recentExpansionTerms = [];
    	this.searchService.fetchPreviousQueries( query.id )
		.then( function( response ) {
			let queries = response.data;
			recentQuery = JSON.parse(queries.docsQuery.query);
			recentExpansionTerms = JSON.parse(queries.docsQuery.expansionTerms);
			me.userInput = queries.docsQuery.userInput;
			me.instance.recent.docsQuery = recentQuery;
			me.instance.recent.aggsQuery = JSON.parse(queries.aggsQuery.query);
			me.inputExpansion.on = queries.docsQuery.inputExpansion;
			if ( me.inputExpansion.on ) me.inputExpansion.terms = recentExpansionTerms;
			if ( queries.docsQuery.distinctCounts ) me.instance.distinctCountsOn();
			me.searchService.fetchAuthorizedContextByLabel( query.authorizedContext )
			.then( function( response ) { 
				//alert("check on me\n"+JSON.stringify(me));
				me.setContext( new AuthorizedContext(response.data) )
				.then( function(response) {
					me.parsePastQuery( recentQuery ); 
					me.r("RECENT")	
					.then( me.searchCorpora )
					.then( function(search) {
						me.finish( search );
					});
				})
				.catch( function(e) {
					me.clearResults();
					me.remoteError("docs",e);
				});
			});
		})
		.catch( function(e) {
			me.clearResults();
			me.remoteError("docs",e);
		});
    }
    
    parsePastQuery( pastQuery ) { //TODO move to ??? (better abstraction level)
//alert(JSON.stringify(pastQuery,null,'\t'));
    	for ( let corpus of this.context.corpora ) {
    		corpus.parsePastQueryFilterArray( pastQuery.query.bool.filter )
    	}
    	//return this.$q.when( this );
    }
    
    encSearch( serviceId ) { 
    	this.serviceId = serviceId;
    	let me = this;
    	this.r("ENCOUNTER")	//returns the current Search instance and passes down the promise chain
    	.then( me.searchCorpora )
    	.then( function(search) {
			me.finish( search );
		})
    	.catch( function(e) {
    		me.clearResults();
    		me.remoteError("docs",e);
    	});
    }
    
    r( mode ) {
//alert( "register");
		this.instance.reset();
    	this.instance.mode = mode;
    	this.instance.authorizedContext = this.context.label;
    	var me = this;
//alert("r (search.instance)\n"+JSON.stringify(this.instance,null,'\t'));
    	return this.$q.when( me );
    }
    
    dec( search ) {
//alert("dec\n");
    	let decorators = [];
    	for ( let corpus of search.context.corpora ) {
    		let index = 0;
<<<<<<< HEAD
    		if ( corpus.results.aggs && !corpus.results.aggs.isEmpty() && corpus.results.aggs.aggs['Medical Concepts'] ) {
=======
    		if ( corpus.results.aggs && corpus.results.aggs.aggs['Medical Concepts'] ) {
>>>>>>> db35fe8c0db1ca3ccb2ffd2ad5a072252163706f
    			for ( let bucket of corpus.results.aggs.aggs['Medical Concepts'].buckets ) {
    				index++;
    				//console.log(bucket.key);
    				decorators.push( 
    					search.$http.get( APP.ROOT + '/umls/string/' + bucket.key )
    					.then ( function( response ) {
    						if (response.data.hits.total>0) {
    							bucket.label=response.data.hits.hits[0]._source.sui;	//if umls str exits put in key prop
    						} else {
    							//corpus.results.aggs.aggs['Medical Concepts'].buckets.splice(index,1);
    						}
    					})
    				);
    			}
    		}
    	}
    	if ( decorators.length==0 ) {
    		//decorators will not exist if concepts aggs are not set in user's prefs
//alert ("decorators is zero");
    		return search.$q.when( search );
    	}
    	return search.$q.all( decorators )
    		.then( function( response ) {
    			return search;
    		});
    }
    
    d( corpus ) {
    	//single corpus doc search
    	//returns es hits
    	corpus.status.searchingDocs = true;
    	let payload = new SearchPayload( corpus, this, "DOCS" );
//alert("PAYLOAD\n"+JSON.stringify(payload,null,'\t'));
		return this.$http.post( APP.ROOT + '/search/elastic/', JSON.stringify( payload ) )
			.then( function( docSearchResponse ) {
				let results = docSearchResponse.data;
				corpus.results.docs = new DocumentsResponse( results );
				corpus.results.pagination.update( corpus.results.docs.total );
				return results;
			})
			.finally( function() {
				corpus.status.searchingDocs = false;
				corpus.brighten();
			});
    }
    
    a( corpus ) {
    	//single corpus agg computation
    	//returns es hits
    	corpus.status.computingAggs = true;
    	let payload = new SearchPayload( corpus, this, "AGGS" );
    	let me = this;
//alert(JSON.stringify(payload.query,null,'\t'));
		return this.$http.post( APP.ROOT + '/search/elastic/', JSON.stringify( payload ) )
			.then( function( aggsSearchResponse ) {
				let results = aggsSearchResponse.data;
<<<<<<< HEAD
				corpus.results.aggs = new AggregationsResponse( results, corpus );
=======
				corpus.results.aggs = new AggregationsResponse( results, corpus );	//TODO refactoring that will create client-side objects for API data will eventually have a method for putting date slider on corpus.metadata.aggregations
>>>>>>> db35fe8c0db1ca3ccb2ffd2ad5a072252163706f
				
				if ( me.instance.distinctCounts.on ) {
					var maxCount = corpus.results.aggs.total;
					console.log("max distinct count " + maxCount);
					me.distinctCounts( corpus, maxCount );
				}
				
				return results;
			})
			.finally( function() {
    			corpus.status.computingAggs = false;
    		});
    }
    
    d_enc( corpus, serviceId ) { //TODO method calling this sends in fluffed-up DocumentQuery/EncounterQuery
    	corpus.status.searchingDocs = true;
    	let payload = new SearchPayload( corpus, this, "ENC-DOCS", serviceId );
		return this.$http.post( APP.ROOT + '/search/elastic/', JSON.stringify( payload ) )
			.then( function( docSearchResponse ) {
				let results = docSearchResponse.data;
				corpus.results.docs = new DocumentsResponse( results );
				corpus.results.pagination.update( corpus.results.docs.total );
				return results;
			})
			.finally( function() {
				corpus.status.searchingDocs = false;
				corpus.brighten();
			});
    }
    
    a_enc( corpus, serviceId ) {
    	let me = this;
    	corpus.status.computingAggs = true;
    	let payload = new SearchPayload( corpus, this, "ENC-AGGS", serviceId );
		return this.$http.post( APP.ROOT + '/search/elastic/', JSON.stringify( payload ) )
			.then( function( aggsSearchResponse ) {
				let results = aggsSearchResponse.data;
				corpus.results.aggs = new AggregationsResponse( results, corpus );	//TODO refactoring that will create client-side objects for API data will eventually have a method for putting date slider on corpus.metadata.aggregations
				
				if ( me.instance.distinctCounts.on ) {
					var maxCount = corpus.results.aggs.total;
					console.log("max distinct count " + maxCount);
					me.distinctCounts( corpus, maxCount );
				}
				
				return results;
			})
			.finally( function() {
    			corpus.status.computingAggs = false;
    		});
    }
    
    d_recent( docsQuery, corpus ) { 
    	corpus.status.searchingDocs = true;
    	let payload = new SearchPayload( corpus, this, "RECENT-DOCS" );
    	//var url = corpus.metadata.url;
    	//var filterDesc = "TBD";
		return this.$http.post( APP.ROOT + '/search/elastic/', JSON.stringify( payload ) )
			.then( function( docSearchResponse ) {
				let results = docSearchResponse.data;
				corpus.results.docs = new DocumentsResponse( results );
				corpus.results.pagination.update( corpus.results.docs.total );
				return results;
			})
			.finally( function() {
				corpus.brighten();
				corpus.status.searchingDocs = false;
			});
    }
    
    a_recent( aggsQuery, corpus ) { 
    	corpus.status.computingAggs = true;
    	let payload = new SearchPayload( corpus, this, "RECENT-AGGS" );
    	//var url = corpus.metadata.url;
    	//var filterDesc = "TBD";
    	let me = this;
		return this.$http.post( APP.ROOT + '/search/elastic/', JSON.stringify( payload ) )
			.then( function( aggsSearchResponse ) {
				let results = aggsSearchResponse.data;
				corpus.results.aggs = new AggregationsResponse( results, corpus );	//TODO refactoring that will create client-side objects for API data will eventually have a method for putting date slider on corpus.metadata.aggregations
				
				if ( me.instance.distinctCounts.on ) {
					var maxCount = corpus.results.aggs.total;
					console.log("max distinct count " + maxCount);
					me.distinctCounts( corpus, maxCount );
				}
				
				return results;
			})
			.finally( function() {
				corpus.status.computingAggs = false;
			});
    }
    
    p( corpus ) { //TODO method calling this sends in fluffed-up DocumentQuery/EncounterQuery
    	//single corpus doc search
    	//returns es hits
    	corpus.status.searchingDocs = true;
    	let payload = new SearchPayload( corpus, this, "PAGE" ); 
		return this.$http.post( APP.ROOT + '/search/elastic/', JSON.stringify( payload ) )
			.then( function( pageSearchResponse ) {
				let results = pageSearchResponse.data;
				corpus.results.docs = new DocumentsResponse( results );
				corpus.results.pagination.update( corpus.results.docs.total );
				return results;
			})
			.finally( function() {
				corpus.brighten();
				corpus.status.searchingDocs = false;
			});
    }
  
    searchCorpora( search ) {
    	let me = this;
//alert("searchCorpora\n"+JSON.stringify(search.instance,null,'\t'));
    	//search corpora, kicks off parallel ($q.all) documents and aggs search
    	//iterate over corpora and kick off parallel d() and a() searches
    	let corporaSearch = [];
    	for ( let corpus of search.context.corpora ) {
    		if ( corpus.metadata.searchable ) {
    			corpus.prepare();
    			let searches = [];
    			if (search.instance.mode=="RECENT" || search.instance.mode=="SAVED") {
    				searches.push( search.d_recent( search.instance.recent.docsQuery, corpus ) );
    				searches.push( search.a_recent( search.instance.recent.aggsQuery, corpus ) );
    			} else if ( search.instance.mode=="ENCOUNTER" ) {
    				searches.push( search.d_enc( corpus, search.serviceId ) );
    				searches.push( search.a_enc( corpus, search.serviceId ) );
    			} else if ( search.instance.mode=="DEFAULT" ) {
    				//search.instance.mode in { "DEFAULT" }
    				searches.push( search.d( corpus ) );
    				searches.push( search.a( corpus ) );
    			}
    			let corpusSearch = search.$q.all( searches )
	    			.then ( function( results ) {
	    				//results is an array mapped to searches array
	    				//return results;
	    			});
    			corporaSearch.push( corpusSearch );
			}
		}
    	return search.$q.all( corporaSearch )
    		.then( function(response) {
    			return search;
    		});
    }
    
    
    //pagination
    nextPage( corpus ) {
    	//next pg, no aggs
    	if ( !corpus.isDirty() && corpus.results.pagination.next() ) {
    		let me = this;
    		this.p( corpus )
	    	.catch( function(e) {
	    		me.clearResults();
	    		me.remoteError("docs",e);
			});
    		if ( corpus.results.pagination.truncated ) {
    			this.info( "docs", "Pagination limited to " + this.paginationLimit + " pages of results" );
    		}
    	}
    }
    
    lastPage( corpus ) {
		if ( !corpus.isDirty() && corpus.results.pagination.last() )  {
			let me = this;
			return this.p( corpus )
			.catch( function(e) {
				me.clearResults();
				me.remoteError("docs",e);
			});
			if ( corpus.results.pagination.truncated ) {
    			this.info( "docs", "Pagination limited to " + this.paginationLimit + " pages of results" );
    		}
		}
    }
    
    previousPage( corpus ) {
    	//prev pg, composite
    	if ( !corpus.isDirty() && corpus.results.pagination.previous() ) this.p( corpus );
    }
    
    firstPage( corpus ) {
    	if ( !corpus.isDirty() && corpus.results.pagination.first() ) this.p( corpus );
    }
    
    ps() {
    	//search item is search history, past search
    }
    
    ss() {
    	//saved search
    }
    
    sh() {
    	//search history
    }
    
    info( div,e ) {
    	this.growl.info( e.toString(), {ttl:5000, referenceId:div} );
    }
    error( div,e ) {
    	this.growl.error( e.toString(), {ttl:5000, referenceId:div} );
    }
    remoteError( div,e ) {
    	console.log(JSON.stringify(e,null,'\t'));
    	//this.growl.error( e.statusText + " (" + e.status + ") <<  " + e.data.error.root_cause[0].type + "::" + e.data.error.root_cause[0].reason, {ttl:5000, referenceId:div} );
    	//TODO ensure bad elastic queries are captured
    	this.growl.error( e.statusText + " (" + e.status + ") - " + e.data.message, {ttl:5000, referenceId:div} );
    }
    

    
  //----------------- end refactored search------------------------------

    //https://coderwall.com/p/40axlq/power-up-angular-s-http-service-with-caching
    corpusAggregations( corpus ) {
    	//gets filters based on user prefs 
    	let me = this;
    	return this.$http.get( APP.ROOT + '/settings/corpusAggregations/' + corpus.id );
    	/*.then( function(response) {
	    	corpus.metadata.aggregations = new CorpusAggregationsResponse( response.data );
alert("corpus in corpusAggregations\n"+JSON.stringify(corpus,null,'\t'));
	    	console.log("aggregations set for " + corpus.name);
	    	return me.$q.when( me );
	    });*/
    }
    
    clearResults() {
    	this.searchIconClass = "fa fa-search";
    	//this.inputExpansion = new InputExpansion();
    	for ( let corpus of this.context.corpora ) {
    		corpus.results = {};
    		corpus.removeCounts(); 
    	}
    }
  
    distinctCounts( corpus, maxCount ) {
    	//var url = corpus.metadata.url;
//alert(JSON.stringify(corpus.metadata.aggregations));
    	var me = this;
    	for ( let ontology of corpus.metadata.aggregations ) {
    		for ( let aggregation of ontology.aggregations ) {
    			if ( aggregation.countDistinct ) {
//alert(JSON.stringify(aggregation,null,'\t'));
    				aggregation.status.computingCounts = true;
    				let payload = new CountPayload( corpus, me, aggregation, maxCount )
    				
//alert(payload.countType + "\n" + JSON.stringify( payload, null, '\t' ));
    				if ( payload.countType=="BUCKET" ) {
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
    		
    	}
    }
    assignDistinct( aggregation, response ) {
    	aggregation.countType = response.data.countType;
    	aggregation.count = response.data.count;
    	aggregation.cardinalityEstimate = response.data.cardinalityEstimate;
    	aggregation.status.computingCounts = false;
		console.log("distinct " + aggregation.label + " took " + response.data.took + " returning " + response.data.countType + "/cardinality counts of: " + response.data.count + "/" + response.data.cardinalityEstimate);
    }
    
}

Search.$inject = [ '$http', '$q', 'growl', 'searchService' ];

export default Search;