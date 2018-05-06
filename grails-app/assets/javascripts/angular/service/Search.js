import DocumentsResponse from '../model/search/DocumentsResponse';
import AggregationsResponse from '../model/search/AggregationsResponse';
import DocumentQuery from '../model/search/elastic/DocumentQuery';
import AggregationQuery from '../model/search/elastic/AggregationQuery';
import PaginationQuery from '../model/search/elastic/PaginationQuery';
import BucketCountQuery from '../model/search/elastic/BucketCountQuery';
import CardinalityOnlyQuery from '../model/search/elastic/CardinalityOnlyQuery';
import SingleFieldScrollCountQuery from '../model/search/elastic/SingleFieldScrollCountQuery';
import TermsAggregation from '../model/search/elastic/TermsAggregation';
import Pagination from '../model/search/Pagination';
import TermFilter from '../model/search/elastic/TermFilter';
import EncounterDocQuery from '../model/search/elastic/clinical/EncounterDocQuery';
import EncounterAggQuery from '../model/search/elastic/clinical/EncounterAggQuery';
import AuthorizedContextsResponse from '../model/rest/response/AuthorizedContextsResponse';

class Search {
    constructor( $http, $q, growl, searchService ) {
    	this.$http = $http;
    	this.$q = $q;
    	this.growl = growl;
    	this.searchService = searchService;
    	
    	this.userInput = "iliitis";// "iliitis";
    	this.authorizedContexts = undefined;
    	this.context = undefined;
    	this.registration = undefined;
    	
		this.searchIconClass = "fa fa-search fa-spin";
		
        this.status = {
        	error: undefined,
        	dirty: false,
        	uuid: undefined,
        	sequence: 0
        }
        this.init();
        console.info("Search.js complete");

        
        
        this.similarityExpansionEnabled = false; 
        this.cuiExpansionEnabled = false;				
		this.cuiExpansion = {};				//{ heart:[], valve:[] } or { enabled:false, expansionMap: { heart:[], valve:[] } }
		this.relatednessExpansion = {}; 		//{ heart:[], valve:[] } or { enabled:false, expansionMap: { heart:[], valve:[] } 
		this.options = {
    		relatednessExpansion : {
    			on : false,
    			style: {}
    		}
    	}
        
    }
    
    init() {
    	let me = this;
    	this.searchService.fetchContexts()
    		.then ( function(response) {
    			me.authorizedContexts = new AuthorizedContextsResponse( response.data );	//response.data;
//alert(JSON.stringify(me.authorizedContexts[0],null,'\t'));
    			me.setContext(me.authorizedContexts[0]);	//set to first in list
    		});
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
    
    //TODO right level of abstraction? - move to Corpus?
    dirty( corpus ) {
    	//search and corpus need to be set to dirty
    	this.status.dirty = true;
		this.searchIconClass = "fa fa-refresh fa-spin";
		if ( corpus ) {
			//dim only this corpus
			corpus.dim();
		} else {
			//dim doc results for all copora
			for (let corpus of this.context.corpora) {
				corpus.dim();
			}
		}
	}

    setContext(searchContext) {
    	//if (!searchContext) return;	
    	//for some reason this function gets invoked with undefined searchContext on change of contexts dropdown; 
    	//this check and immediate return prevents console errors, otherwise the app appears to work as expected
    	this.context = searchContext;
    	this.status.sequence = 0;
    	this.clearResults();
    	let activeSet = false;
    	for ( let corpus of this.context.corpora ) {
    		if ( corpus.metadata.searchable ) {
    			if ( !activeSet ) {
    				this.searchService.setActiveCorpus( corpus, this.context.corpora );	//TODO refactor active assignments to a user pref?
    				activeSet = true;
    			}
    			//if ( corpus.metadata.filtered ) {
    			//	corpus.contextFilter = new TermFilter(corpus.metadata.contextFilterField, this.context.contextFilterValue);	//contextFilter specific to each searchable corpus
    			//	//alert(JSON.stringify(corpus.contextFilter));
    			//}
    			if ( !corpus.appliedFilters ) {
    				corpus.appliedFilters = {};
    				corpus.brighten();
    				//corpus.opacity = corpus.resultsOpacity.bright;
    				corpus.pagination = new Pagination();
    				corpus.results = {};
    				this.complete();
    			}
				this.corpusAggregations( corpus )
	    			.then( function(response) {
//alert(JSON.stringify(response.data,null,'\t'));
	    				corpus.metadata.aggregations = response.data;
	    				console.log("AUTHORIZED CONTEXT SET");
	    			});
	    				
			}
    	}
    	this.searchService.fetchHistory();
    }
    
    filterDistiller( obj ) {
    	let terms = [];
    	let filterArray = obj.query.bool.filter;
    	for ( let filter of filterArray ) {
    		Object.keys(filter).map( function(type,index) {
    			//type will be bool | range
    			if ( type=="bool" ) {
    				let shouldFilterArray = filter.bool.should;
    				//alert(JSON.stringify(shouldFilterArray));
    				for ( let termFilter of shouldFilterArray ) {
    					for ( let prop in termFilter.term ) {
    						terms.push( termFilter.term[prop] );
    					}
    				}
    			}
    			if ( type=="range" ) {
    				for ( let prop in filter.range ) {
    					let b = new Date(filter.range[prop].gte).toLocaleDateString();
    					let e = new Date(filter.range[prop].lte).toLocaleDateString();
    					terms.push( b + " - " + e);
    				}
    			}
    		});
    	}
    	//alert( terms.join(', '));
    	return terms.join(', ');
    }

    //----------------- refactored search ------------------------------
    //each should return a promise? or be its own self-contained process that returns a promise?
    
    //core search
    c() {
    	//context set
    	//returns this
    }
    
    complete( search ) {
    	if ( !search ) search = this;	//invocation of this complete() may not be in promise chain that passes search object
    	search.searchIconClass = "fa fa-search";
    	return search.$q.when( search );
	}
    
    e() { //TODO ADD A TYPE PARAM TO THIS METHOD AND A BUILDER METHOD FOR RETURNING THE TYPE OF QUERY TO BE PASSED TO searchCorpora
    	//use this as a template for encounter search - chaining will follow the same steps except searchCorpora replaced by pullEncounterDocuments( search )
    	//execute search 
    	let me = this;
    	this.r("initial")	//returns the current Search instance and passes down the promise chain
    		.then( me.searchCorpora )
    		.then( me.dec )	//decorate (results) as appropriate
    		.then( me.complete )		//LOOK into switching order of these this and next stmt and being able to fetch hist from Search.js (this); would be nice for complete to actally be the end of the promise chain.
    		.then( me.searchService.fetchHistory() )
    		//.then( me.searchService.fetchHistoryExcludingMostRecent() )
    		.catch( function(e) {
    			me.clearResults();
    			me.remoteError("docs",e);
    		});
    }
    
    recentSearch( queryId ) {
    	//user queryId to return authorized context string
    	//fetch authorized context
    	//fetch Query queries (document and aggregation?)
    	//set context - make sure aggretations are zeroed out
    	//register("recent") - responsible for creating/delegating creation of Query subclass to be used
    	//DocumentQuery << Query.query from API call
    	//pass DocumentQuery to searchCorpora
    	//rest is the same (except for possiblility of Aggs query pulling buckets for fields current disabled by user
    	var me = this;
    	this.clearResults();
    	
    	this.searchService.fetchPreviousQuery( queryId )
    		.then( function( response ) {
//alert("PREVIOUS QUERY\n"+JSON.stringify(response.data,null,'\t'));
    			var queries = response.data;
    			me.userInput = queries.docsQuery.terms;
    			me.recentDocsQuery = JSON.parse(queries.docsQuery.query);
    			me.recentAggsQuery = JSON.parse(queries.aggsQuery.query);
    			//get aggs query
    			//temp assignment hold return for use in the next  couple of .thens
    			me.searchService.fetchAuthorizedContextByLabel( queries.docsQuery.registration.authorizedContext )
    				.then( function( response ) { 
    					me.setContext( response.data );
    					me.r("recent")
    					.then( me.searchCorpora )
    					.then( me.dec )	//decorate (results) as appropriate
    					.then( me.complete )		//LOOK into switching order of these this and next stmt and being able to fetch hist from Search.js (this); would be nice for complete to actally be the end of the promise chain.
    		    		.then( me.searchService.fetchHistory() )
    		    		.catch( function(e) {
    		    			me.clearResults();
    		    			me.remoteError("docs",e);
    		    		});
    		    			
    			});
    		});
    }
    
    encSearch( serviceId ) { 
    	this.serviceId = serviceId;
    	let me = this;
    	this.userInput = "*";
    	this.r("encounter")	//returns the current Search instance and passes down the promise chain
    		.then( me.searchCorpora )
    		.then( me.dec )	//decorate (results) as appropriate
    		.then( me.complete )		//LOOK into switching order of these this and next stmt and being able to fetch hist from Search.js (this); would be nice for complete to actally be the end of the promise chain.
    		.then( me.searchService.fetchHistory() )
    		.catch( function(e) {
    			me.clearResults();
    			me.remoteError("docs",e);
    		});
    }
    
    r( searchType ) {
    	//register
    	//returns search obj/this
    	var me = this;
    	if ( this.registration && this.registration.initialUserInput==this.userInput && searchType!="recent" ) {
    		me.status.sequence++;
    		return this.$q.when( me );
    	}
    	if ( searchType=="encounter") this.userInput = "*";
    	//otherwise, register a new search
    	return this.$http.post( APP.ROOT + '/audit/register/', JSON.stringify( { "authorizedContext":this.context.label, "initialUserInput":this.userInput, "searchType":searchType } ) )
    		.then( function( registrationResponse ) {
    			me.registration = registrationResponse.data;
    			me.status.sequence = 0;
    			return me;
    		});
    }
    
    dec( search ) {
    	let decorators = [];
    	for ( let corpus of search.context.corpora ) {
//alert(JSON.stringify(corpus.results.aggs));
    		if ( corpus.results.aggs && corpus.results.aggs.aggs['Medical Concepts'] ) {
    			for ( let bucket of corpus.results.aggs.aggs['Medical Concepts'].buckets ) {
    				//console.log(bucket.key);
    				decorators.push( 
    					search.$http.get( APP.ROOT + '/umls/string/' + bucket.key )
    					.then ( function( response ) {
    						if (response.data.str) bucket.key=response.data.str;	//if umls str exits put in key prop
    					})
    				);
    			}
    		}
    	}
    	if ( decorators.length==0 ) {
    		//alert ("decorators is zero");
    		return search;
    	}
    	return search.$q.all( decorators )
    		.then( function( response ) {
    			return search;
    		});
    }
    

    d( registration, corpus ) { //TODO method calling this sends in fluffed-up DocumentQuery/EncounterQuery
    	//single corpus doc search
    	//returns es hits
    	corpus.status.searchingDocs = true;
    	var url = corpus.metadata.url;
    	var docsQuery = new DocumentQuery( corpus, this.userInput );
    	var filterDesc = this.filterDistiller( docsQuery );
//alert("DOC QUERY\n"+JSON.stringify(docsQuery,null,'\t'));
		return this.$http.post( APP.ROOT + '/search/elastic/', JSON.stringify( { "registration.id":registration.id, "corpus":corpus.name, "type":docsQuery.constructor.name.toString(), "url":url, "query":docsQuery, "filters":filterDesc, "sequence":this.status.sequence } ) )
			.then( function( docSearchResponse ) {
				let results = docSearchResponse.data;
				corpus.results.docs = new DocumentsResponse( results );
				corpus.pagination.update( corpus.results.docs.total );
				return results;
			})
			.finally( function() {
				corpus.status.searchingDocs = false;
				corpus.brighten();
			});
    }
    
    d_enc( registration, corpus, serviceId ) { //TODO method calling this sends in fluffed-up DocumentQuery/EncounterQuery
    	//single corpus doc search
    	//returns es hits
    	corpus.status.searchingDocs = true;
    	var url = corpus.metadata.url;
    	var encQuery = new EncounterDocQuery( corpus, this.userInput, serviceId  );
    	var filterDesc = this.filterDistiller( encQuery );
//alert("ENC QUERY\n"+JSON.stringify(encQuery,null,'\t'));
		return this.$http.post( APP.ROOT + '/search/elastic/', JSON.stringify( { "registration.id":registration.id, "corpus":corpus.name, "type":encQuery.constructor.name.toString(), "url":url, "query":encQuery, "filters":filterDesc, "sequence":this.status.sequence } ) )
			.then( function( docSearchResponse ) {
				let results = docSearchResponse.data;
				corpus.results.docs = new DocumentsResponse( results );
				corpus.pagination.update( corpus.results.docs.total );
				return results;
			})
			.finally( function() {
				corpus.status.searchingDocs = false;
				corpus.brighten();
			});
    }
    
    a_enc( registration, corpus, serviceId ) {
    	corpus.status.computingAggs = true;
    	var url = corpus.metadata.url;
    	var aggsQuery = new EncounterAggQuery( corpus, this.userInput, serviceId );
    	var filterDesc = this.filterDistiller( aggsQuery );
//alert("ENC AGG QUERY\n" + JSON.stringify(aggsQuery,null,'\t'));
		return this.$http.post( APP.ROOT + '/search/elastic/', JSON.stringify( {"registration.id":registration.id, "corpus":corpus.name, "type":aggsQuery.constructor.name.toString(), "url":url, "query":aggsQuery, "filters":filterDesc, "sequence":this.status.sequence  } ) )
			.then( function( aggsSearchResponse ) {
				let results = aggsSearchResponse.data;
				corpus.results.aggs = new AggregationsResponse( results, corpus );	//TODO refactoring that will create client-side objects for API data will eventually have a method for putting date slider on corpus.metadata.aggregations
				return results;
			})
			.finally( function() {
    			corpus.status.computingAggs = false;
    		});
    }
    
    d_recent( docsQuery, corpus, registration ) { 
    	corpus.status.searchingDocs = true;
    	var url = corpus.metadata.url;
    	var filterDesc = "TBD";
		return this.$http.post( APP.ROOT + '/search/elastic/', JSON.stringify( { "registration.id":registration.id, "corpus":corpus.name, "type":"DocumentQuery(recent)", "url":url, "query":docsQuery, "filters":filterDesc, "sequence":this.status.sequence } ) )
			.then( function( docSearchResponse ) {
				let results = docSearchResponse.data;
				corpus.results.docs = new DocumentsResponse( results );
				corpus.pagination.update( corpus.results.docs.total );
				return results;
			})
			.finally( function() {
				corpus.brighten();
				corpus.status.searchingDocs = false;
			});
    }
    
    a_recent( aggsQuery, corpus, registration ) { 
    	corpus.status.computingAggs = true;
    	var url = corpus.metadata.url;
    	var filterDesc = "TBD";
		return this.$http.post( APP.ROOT + '/search/elastic/', JSON.stringify( { "registration.id":registration.id, "corpus":corpus.name, "type":"AggregationQuery(recent)", "url":url, "query":aggsQuery, "filters":filterDesc, "sequence":this.status.sequence } ) )
			.then( function( aggsSearchResponse ) {
				let results = aggsSearchResponse.data;
				corpus.results.aggs = new AggregationsResponse( results, corpus );	//TODO refactoring that will create client-side objects for API data will eventually have a method for putting date slider on corpus.metadata.aggregations
				return results;
			})
			.finally( function() {
				corpus.status.computingAggs = false;
			});
    }
    
    p( registration, corpus ) { //TODO method calling this sends in fluffed-up DocumentQuery/EncounterQuery
    	//single corpus doc search
    	//returns es hits
    	corpus.status.searchingDocs = true;
    	var url = corpus.metadata.url;
    	var pageQuery = new PaginationQuery( corpus, this.userInput );
    	var filterDesc = this.filterDistiller( pageQuery );
//alert("PAGE QUERY\n"+JSON.stringify(pageQuery,null,'\t'));
		return this.$http.post( APP.ROOT + '/search/elastic/', JSON.stringify( {"registration.id":registration.id, "corpus":corpus.name, "type":pageQuery.constructor.name.toString(), "url":url, "query":pageQuery, "filters":filterDesc, "sequence":this.status.sequence  } ) )
			.then( function( pageSearchResponse ) {
				let results = pageSearchResponse.data;
				corpus.results.docs = new DocumentsResponse( results );
				corpus.pagination.update( corpus.results.docs.total );
				return results;
			})
			.finally( function() {
				corpus.brighten();
				corpus.status.searchingDocs = false;
			});
    }
    
    

    a( registration, corpus ) {
    	//single corpus agg computation
    	//returns es hits
    	corpus.status.computingAggs = true;
    	var url = corpus.metadata.url;
    	var aggsQuery = new AggregationQuery( corpus, this.userInput );
    	var filterDesc = this.filterDistiller( aggsQuery );
    	let me = this;
//alert("AGGS QUERY\n" + JSON.stringify(aggsQuery,null,'\t'));
		return this.$http.post( APP.ROOT + '/search/elastic/', JSON.stringify( {"registration.id":registration.id, "corpus":corpus.name, "type":aggsQuery.constructor.name.toString(), "url":url, "query":aggsQuery, "filters":filterDesc, "sequence":this.status.sequence  } ) )
			.then( function( aggsSearchResponse ) {
				let results = aggsSearchResponse.data;
				corpus.results.aggs = new AggregationsResponse( results, corpus );	//TODO refactoring that will create client-side objects for API data will eventually have a method for putting date slider on corpus.metadata.aggregations
				
				if ( me.options.relatednessExpansion.on ) {
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
    
    sh() {
    	//fetch history
    	//returns search history array
    }
  
    searchCorpora( search ) {
    	//search corpora, kicks off parallel ($q.all) documents and aggs search
    	//iterate over corpora and kick off parallel d() and a() searches
    	let corporaSearch = [];
    	for ( let corpus of search.context.corpora ) {
    		if ( corpus.metadata.searchable ) {
    			//alert( corpus.name );
    			corpus.pagination = new Pagination();
    			let searches = [];
    			if (search.registration.searchType=="recent") {
    				searches.push( search.d_recent( search.recentDocsQuery, corpus, search.registration ) );
    				searches.push( search.a_recent( search.recentAggsQuery, corpus, search.registration ) );
    			} else if ( search.registration.searchType=="encounter" ) {
    				searches.push( search.d_enc( search.registration, corpus, search.serviceId ) );
    				searches.push( search.a_enc( search.registration, corpus, search.serviceId ) );
    			} else {
    				searches.push( search.d( search.registration, corpus ) );
    				searches.push( search.a( search.registration, corpus ) );
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
    	if ( !corpus.isDirty() && corpus.pagination.next() ) {
    		let me = this;
    		this.p( this.registration, corpus )
	    	.catch( function(e) {
	    		me.clearResults();
	    		me.remoteError("docs",e);
			});
    		if ( corpus.pagination.truncated ) {
    			this.info( "docs", "Pagination limited to " + this.paginationLimit + " pages of results" );
    		}
    	}
    }
    
    lastPage( corpus ) {
		if ( !corpus.isDirty() && corpus.pagination.last() )  {
			let me = this;
			return this.p( this.registration, corpus )
			.catch( function(e) {
				me.clearResults();
				me.remoteError("docs",e);
			});
			if ( corpus.pagination.truncated ) {
    			this.info( "docs", "Pagination limited to " + this.paginationLimit + " pages of results" );
    		}
		}
    }
    
    previousPage( corpus ) {
    	//prev pg, composite
    	if ( !corpus.isDirty() && corpus.pagination.previous() ) this.p( this.registration, corpus );
    }
    
    firstPage( corpus ) {
    	if ( !corpus.isDirty() && corpus.pagination.first() ) this.p( this.registration, corpus );
    }
    
    forwardCursor( corpus ) {
		return ( !corpus.isDirty() && corpus.pagination.hasNext() ) ? "pointer" : "not-allowed";
	}
	backwardCursor( corpus ) {
		return ( !corpus.isDirty() && corpus.pagination.hasPrevious() ) ? "pointer" : "not-allowed";
	}
    
    
    //past searches
    ps() {
    	//search item is search history, past search
    	//returns es hits
    }
    
    ss() {
    	//saved search
    	//returns es hits
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
    	return this.$http.get( APP.ROOT + '/settings/corpusAggregations/' + corpus.id );
    }
    
    clearResults() {
    	this.searchIconClass = "fa fa-search";
    	for ( let corpus of this.context.corpora ) {
    		corpus.results = {};
    	}
    }
    
    distinctCounts( corpus, maxCount ) {
    	var url = corpus.metadata.url;
//alert(JSON.stringify(corpus.metadata.aggregations));
    	var aggregations = corpus.metadata.aggregations;
    	var me = this;
    	Object.keys(aggregations).map( function(key,index) {
    		var aggregationCategory = corpus.metadata.aggregations[key];
    		for (let aggPropName in aggregationCategory) {
    			let aggregation = aggregationCategory[aggPropName];
    			if ( aggregation.countDistinct ) {
//alert(JSON.stringify(aggregation,null,'\t'));
    				var countType = ( maxCount<=15000000 ) ? "bucket" : "scroll";	//TODO externalize maxBuckets threshold
    				var query;
    				if ( countType=='scroll' ) query = new SingleFieldScrollCountQuery( corpus, me.userInput, aggregation.label, aggregation.fieldName );
    				if ( countType=='bucket' ) {
    					if ( aggregation.field.fieldName=='note_id' ) {
    						query = new CardinalityOnlyQuery( corpus, me.userInput, aggregation.label, aggregation.field.fieldName );
    					} else {
    						query = new BucketCountQuery( corpus, me.userInput, aggregation.label, aggregation.field.fieldName, maxCount );
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
//alert(JSON.stringify( payload, null, '\t' ));
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

Search.$inject = [ '$http', '$q', 'growl', 'searchService' ];

export default Search;