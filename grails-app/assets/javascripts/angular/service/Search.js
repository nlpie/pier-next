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
    constructor( $http, $q, growl, searchService, userService, $cookies ) {
    	this.$http = $http;
    	this.$q = $q;
    	this.growl = growl;
    	this.searchService = searchService;
    	this.userService = userService;
    	this.$cookies = $cookies;
    	
    	this.authorizedContexts = undefined;

    	this.userInput = "";
    	this.context = undefined;
    	
    	this.bookmarkedQuery = false; 
    	//this.menuIconQualifier = "new";
    	
    	this.menuIcon = undefined;
    	this.menuIconOptions = {
			"new": { 
				"title":"new search", 
				"class":"fa fa-search fa-lg"
			},
			"resume": { 
				"title":"most-recent search", 
				"class":"fa fa-arrow-circle-o-left fa-lg"
			}
    	}
    	
    	this.searchIcon = undefined;
    	this.searchIconOptions = { 
    			"loading": { 
    				"text":"loading...", 
    				"class":"fa fa-arrow-circle-o-right fa-spin", 
    				"style": { "border":{},"color":{} }
    			},
    			"refresh": { 
    				"text":"Refresh", 
    				"class":"fa fa-refresh fix", 
    				"style": { 
    					"emphasis":{'border-color':'#4286f4'}, 
    					"color":{'color':'#4286f4'}
    				}
    			},	//"fa fa-refresh fa-spin",#428bca	, 'background':'rgba(66, 134, 244, 0.03)
    			"default": { 
    				"text":"Search", 
    				"class":"fa fa-lg fa-arrow-circle-o-right",
    				"style":{
    					"emphasis":{'color':'#ccc'},
    					"color":{'color':'#ccc'}
    				}
    			},
    			"go": { 
    				"text":"Search", 
    				"class":"fa fa-lg fa-arrow-circle-o-right",
    				"style":{
    					"emphasis":{'border-color':'green'}, 
    					"color":{'color':'green'}
    				}	//"fa fa-search"
    			}
    	}
    	
    	this.resetOptions();
		
        this.status = {
        	error: undefined,
        	dirty: false,
        	exporting: false
        }
        this.init();
        console.info("Search.js complete");
        
    }
    
    resetOptions() {
    	this.userInput = "";
    	this.inputExpansion = new InputExpansion(); 
    	this.instance = new SearchInstance();
    	this.menuIcon = this.menuIconOptions.new;
    	this.searchIcon = this.searchIconOptions.default;
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
    		}).
    		then( function() {
    			//TODO problem is this fires on settings page load b/c of ng-controller in menu item
    			let pathname = window.location.pathname;
    			if ( me.hasBookmark() ) {
    				me.bookmarkedQuery = true;
    				me.menuIcon = me.menuIconOptions.resume;
    				if ( pathname.includes("search") && me.bookmarkedQuery ) {
    					me.growl.info( "resuming with most-recent search", {ttl:2500, referenceId:"docs"} );
    					let query = JSON.parse( me.getBookmark() );
    					me.bookmarkSearch( query );
//alert(`Search.init()\n${me.bookmarkedQuery}`);
    				}
    			}
    		});
    }
    
    hasResults() {
    	if ( this.context==undefined ) return false;
    	return ( this.context.corpus.results.docs ) ? true : false;
    }
    
    //TODO Bookmarks in own class?
    setBookmark() {
    	this.$cookies.put( "bookmark", JSON.stringify( this.searchService.lastQuery ) );
    	this.bookmarkedQuery = true;
    	this.menuIcon = this.menuIconOptions.resume;
	}
	
	getBookmark() {
		return this.$cookies.get( "bookmark" );
	}
	
	hasBookmark() {
		let cookieBookmark = this.$cookies.get( "bookmark" );
		if ( cookieBookmark==undefined ) {
			return false;
		} else {
			return true;
		}
	}
	
	clearBookmark() {
		this.$cookies.remove( "bookmark" );
		this.bookmarkedQuery = false;
		this.menuIcon = this.menuIconOptions.new;
	}
	//-------end bookmark methods
	
	sliderMouseupChange( aggregation ) {
		//alert("mouse");
		if ( this.authenticationExpired() ) return;
		aggregation.currentSlider.updateAggregationFilter( aggregation );
		this.context.corpus.updateUiFilterInfo();
		this.filterChange();
	}
	
	sliderKeyupChange( $event, aggregation ) {
		//alert("key");
		if ( this.authenticationExpired() ) return;
		aggregation.currentSlider.fineAdjust( $event, aggregation );
		this.context.corpus.updateUiFilterInfo();
		this.filterChange();
	}
	
    
    inputChange( oldVal ) {
    	if ( this.authenticationExpired() ) return;
    	if ( this.userInput.length==0 ) {
    		this.searchIcon = this.searchIconOptions.default;
    		return;
    	}
    	if ( this.userInput!=oldVal ) {
    		//user input has changed
    		//if ( this.context.corpus.results.docs ) {
    		if ( this.hasResults() ) {
    			//edited the search field w/ previous results and there is something to search for (not empty input box)
    			this.searchIcon = this.searchIconOptions.refresh;
    			this.context.corpus.dim();
    			this.context.corpus.removeCounts();
    			//this.status.dirty = true;
    			return;
    		} else {
    			this.searchIcon = this.searchIconOptions.go;
    		}
    		return;
    	}
    }
    
    appendHelp( exampleQuery ) {
    	if ( this.authenticationExpired() ) return;
    	this.userInput = `${this.userInput} ${exampleQuery}`;
    }
    
    appendTemplate( fieldName ) {
    	alert(fieldName);
    	if ( this.authenticationExpired() ) return;
    	this.userInput = `${this.userInput} ${fieldName}:"replace-this"`;
    }
    
    /*appendFieldTerm( field, value ) {
    	if ( this.authenticationExpired() ) return;
    	this.userInput = `${this.userInput} ${field}:${value}`;
    }*/
    
    expansionChange() {
    	if ( this.authenticationExpired() ) return;
    	if ( this.inputExpansion.cardinality()>0 ) {
    		if ( this.hasResults() ) {
    			this.searchIcon = this.searchIconOptions.refresh;
    			this.context.corpus.dim();
    		} else {
    			
    		}
    	}		
    }
    
    countsChange() {
    	if ( this.authenticationExpired() ) return;
    	this.instance.toggleDistinctCounts();
    	if ( this.hasResults() ) {
    		//have results to refresh, counts are either being turned on or off, either way need to refresh
    		this.searchIcon = this.searchIconOptions.refresh;
    		this.context.corpus.dim();
    	} else {
    		//nothing to refresh, this.instance.countsOn() return value does not matter
    	}
    }
    
    authenticationExpired() {
    	if ( !this.userService.isLoggedIn() ) {
    		if ( this.hasResults() ) {
    			this.setBookmark();
    		}
    		return true;
    	} else {
    		return false;
    	}
    }
    
    
    //FILTERS
    //all 4 of these methods will only be called if there are results on the screen, so bookmark the last query if 
    toggleAggregationValue( aggregation, value ) {
    	//ORIG ng-click="aggregation.filters[bucket.key]=!aggregation.filters[bucket.key];rc.search.context.corpus.updateUiFilterInfo();rc.search.filterChange()"
    	if ( this.authenticationExpired() ) return;
    	aggregation.filters[value]=!aggregation.filters[value];
    	this.context.corpus.updateUiFilterInfo();
    	this.filterChange();
    }
    toggleSlider() {
    	//ORIG ng-click="rc.search.context.corpus.updateUiFilterInfo();rc.search.filterChange()" ng-model="aggregation.filters[bucket.key]"
    	if ( this.authenticationExpired() ) return;
    	this.context.corpus.updateUiFilterInfo();
    	rc.search.filterChange();
    }
    toggleAggregation( aggregation ) {
    	if ( this.authenticationExpired() ) return;
    	aggregation.toggle();
    	this.context.corpus.updateUiFilterInfo();
    	this.filterChange();
    }
    removeFilters() {
    	if ( this.authenticationExpired() ) return;
    	this.context.corpus.removeFilters();
    	this.filterChange();
    }
    
    navMenuChange() {
    	if ( this.hasResults() ) {
    		this.setBookmark();
    		
    	}
    }
    
    filterChange() {
    	if ( this.authenticationExpired() ) return;
    	//can only call this if there are results
    	this.searchIcon = this.searchIconOptions.refresh;
    	this.context.corpus.dim();
    }
    
    changeContext( searchContext ) {
    	if ( this.authenticationExpired() ) return;
    	this.resetOptions();
    	this.setContext( searchContext );
    }
    
    setContext( searchContext ) {
//alert(`Search.setContext\n${JSON.stringify(searchContext,null,'\t')}`);
    	if ( this.authenticationExpired() ) return;
    	this.context = searchContext;
    	this.clearResults();
    	let me = this;
		if ( this.context.filteredContext ) {
			this.context.corpus.filtered = true;
			this.context.corpus.contextFilter = new TermFilter(this.context.corpus.metadata.contextFilterField, this.context.contextFilterValue);	//contextFilter specific to each searchable corpus
		} else {
			this.context.corpus.filtered = false;
		}
    	console.log("Auth context set to: " + searchContext.label);
    	return this.corpusAggregations( this.context.corpus )
    	.then( function(response) {
    		me.context.corpus.metadata.aggregations = new CorpusAggregationsResponse( response.data );
	    	console.log("aggregations set for " + me.context.corpus.name);
	    	me.searchService.fetchSavedQueries( searchContext.label );
	    	return me.$q.when( me );
	    });
    }
    

    //----------------- refactored search ------------------------------
    //each should return a promise? or be its own self-contained process that returns a promise?
    
    complete( search ) {
//alert("complete");
    	if ( !search ) search = this;	//invocation of this complete() may not be in promise chain that passes search object
    	search.searchIcon = search.searchIconOptions.default;
    	search.status.dirty = false;
    	return search.$q.when( search );
	}
    
    /*e() {
    	if ( this.userInput.length==0 ) {
    		this.growl.error( "no search term(s) provided", {ttl:3000, referenceId:"docs"} );
    		return;
    	}
    	let li = this.loggedIn();
    	alert(JSON.stringify(li,null,'\t'));
    	if ( !li ) return;
    	let me = this;
    	this.r("DEFAULT")
		.then( me.searchCorpus )
		.then( function(search) {
			me.finish( search );
		})
		.catch( function(e) {
			me.clearResults();
			me.remoteError("docs",e);
		});
    }*/
    
    e() {
    	if ( this.userInput.length==0 ) {
    		this.growl.error( "no search term(s) provided", {ttl:3000, referenceId:"docs"} );
    		return;
    	}
    	if ( this.authenticationExpired() ) return;
    	let me = this;
    	this.r("DEFAULT")
		.then( me.searchCorpus )
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
		.then( me.searchService.fetchHistory() );
    }
    
    
    fetchAndParsePreviousQueryById( queryId ) {
    	//fetch past queries, set actual queries on currentSearch.instance.recent obj, fetch context, set context, parse
    	let me = this;	//currentSearch
    	//let pastQueries = undefined;
    	//let pastQueryInfo = undefined;
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
    
    recentSearch( query, mode ) {
    	//mode in { RECENT, BACK }
//alert(JSON.stringify(query));
    	if ( this.authenticationExpired() ) return;
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
			//me.searchService.fetchAuthorizedContextByQueryId( query.label )
			.then( function( response ) { 
				//alert("check on me\n"+JSON.stringify(me));
				me.setContext( new AuthorizedContext(response.data) )
				.then( function(response) {
					me.parsePastQuery( recentQuery ); 
					me.r( mode )	
					.then( me.searchCorpus )
					.then( function(search) {
						me.finish( search );
					});
				})
				.catch( function(e) {
					me.clearResults();
					me.remoteError("docs",e);
				})
				/*.finally( function() {
					if ( mode=="BACK" ) me.instance.backQuery = undefined;
				});*/
			});
		})
		.catch( function(e) {
			me.clearResults();
			me.remoteError("docs",e);
		});
    }
    
    parsePastQuery( pastQuery ) { //TODO move to ??? (better abstraction level)
    	this.context.corpus.parsePastQueryFilterArray( pastQuery.query.bool.filter )
    }

    encSearch( serviceId ) { 
    	if ( this.authenticationExpired() ) return;
    	this.serviceId = serviceId;
    	this.instance.lastQuery = this.searchService.lastQuery;
    	let me = this;
    	this.r("ENCOUNTER")	//returns the current Search instance and passes down the promise chain
    	.then( me.searchCorpus )
    	.then( function(search) {
			me.finish( search );
		})
    	.catch( function(e) {
    		me.clearResults();
    		me.remoteError("docs",e);
    	});
    } 
    
    lastSearch( query ) {
		this.instance.lastQuery = undefined;
    	this.recentSearch( query, "BACK" );
    }
    
    bookmarkSearch( query ) {
		this.clearBookmark();
    	this.recentSearch( query, "BOOKMARK" );
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
		let index = 0;
		if ( this.context.corpus.results.aggs && !this.context.corpus.results.aggs.isEmpty() && this.context.corpus.results.aggs.aggs['Medical Concepts'] ) {
			for ( let bucket of this.context.corpus.results.aggs.aggs['Medical Concepts'].buckets ) {
				index++;
				//console.log(bucket.key);
				decorators.push( 
					search.$http.get( APP.ROOT + '/umls/string/' + bucket.key )
					.then ( function( response ) {
						bucket.label=response.data.str;
						/*if (response.data.hits.total>0) {
							bucket.label=response.data.hits.hits[0]._source.sui;	//if umls str exits put in key prop
						} else {
							//corpus.results.aggs.aggs['Medical Concepts'].buckets.splice(index,1);
						}*/
					})
				);
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
    	let me = this;
//alert("PAYLOAD\n"+JSON.stringify(payload,null,'\t'));
		return this.$http.post( APP.ROOT + '/search/elastic/', JSON.stringify( payload ) )
			.then( function( docSearchResponse ) {
				let results = docSearchResponse.data;
				corpus.results.docs = new DocumentsResponse( results );
				corpus.results.pagination.update( corpus.results.docs.total );
				me.instance.auditedQuery = results.query;
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
				corpus.results.aggs = new AggregationsResponse( results, corpus );
				
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
    	let me = this;
    	let payload = new SearchPayload( corpus, this, "ENC-DOCS", serviceId );
		return this.$http.post( APP.ROOT + '/search/elastic/', JSON.stringify( payload ) )
			.then( function( docSearchResponse ) {
				let results = docSearchResponse.data;
				corpus.results.docs = new DocumentsResponse( results );
				corpus.results.pagination.update( corpus.results.docs.total );
				me.instance.auditedQuery = results.query;
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
    	let me = this;
    	let payload = new SearchPayload( corpus, this, "RECENT-DOCS" );
//alert(`D_RECENT PAYLOAD ${JSON.stringify(payload,null,'\t')}`);
    	//var url = corpus.metadata.url;
    	//var filterDesc = "TBD";
		return this.$http.post( APP.ROOT + '/search/elastic/', JSON.stringify( payload ) )
			.then( function( docSearchResponse ) {
				let results = docSearchResponse.data;
				corpus.results.docs = new DocumentsResponse( results );
				corpus.results.pagination.update( corpus.results.docs.total );
				me.instance.auditedQuery = results.query;
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
  
    searchCorpus( search ) {
    	let me = this;
//alert("searchCorpus\n"+JSON.stringify(search.instance,null,'\t'));
    	//kicks off parallel ($q.all) documents and aggs search
		let corpus = search.context.corpus;
		corpus.prepare();
		let searches = [];
		//if (search.instance.mode=="RECENT" || search.instance.mode=="SAVED" || search.instance.mode=="BACK") {
		if ( [ "RECENT","SHARED","SAVED","BACK","BOOKMARK" ].includes( search.instance.mode ) ) {
			searches.push( search.d_recent( search.instance.recent.docsQuery, corpus ) );
			searches.push( search.a_recent( search.instance.recent.aggsQuery, corpus ) );
		} else if ( search.instance.mode=="ENCOUNTER" ) {
			searches.push( search.d_enc( corpus, search.serviceId ) );
			searches.push( search.a_enc( corpus, search.serviceId ) );
		} else if ( search.instance.mode=="DEFAULT" ) {
			searches.push( search.d( corpus ) );
			searches.push( search.a( corpus ) );
		}
    	return search.$q.all( searches )
    		.then( function(response) {
    			return search;
    		});
    }
    
    
    //pagination
    nextPage( corpus ) {
    	//next pg, no aggs
    	if ( this.authenticationExpired() ) return;
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
    	if ( this.authenticationExpired() ) return;
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
    	if ( this.authenticationExpired() ) return;
    	if ( !corpus.isDirty() && corpus.results.pagination.previous() ) this.p( corpus );
    }
    
    firstPage( corpus ) {
    	if ( this.authenticationExpired() ) return;
    	if ( !corpus.isDirty() && corpus.results.pagination.first() ) this.p( corpus );
    }
    
    exportResults() {
    	if ( this.authenticationExpired() ) return;
    	if ( !this.hasResults() ) {
    		this.growl.error( "no results to export", {ttl:3000, referenceId:"docs"} );
    		return;
    	}
    	this.instance.mode = "EXPORT";
    	let me = this;
    	let payload = new SearchPayload( this.context.corpus, this, "EXPORT" );	//returns payload containing an ExportQuery with an empty fields array, need to specify which fields to return	
    	this.$http.get( APP.ROOT + '/settings/corpusExports/' + this.context.corpus.id )
			.then( function( exportsResponse ) {
				let fieldMetadata = exportsResponse.data;
				let fields = fieldMetadata.fields;
				payload.query.fields = fields;
				let postBody = {};
				postBody.payload = payload;
				postBody.fieldMetadata = fieldMetadata;
				
				//https://systemoverlord.com/2016/08/24/posting-json-with-an-html-form.html
				//https://stackoverflow.com/questions/6964927/how-to-create-a-form-dynamically-via-javascript
				var f = document.createElement("form");
				f.setAttribute('method',"POST");
				f.setAttribute('action',"search/export");
				f.setAttribute('enctype', "text/plain");
				f.setAttribute('id', "download-form")

				var i = document.createElement("input"); //input element, text
				i.setAttribute('type',"hidden");
				i.setAttribute('name', JSON.stringify( postBody ) );

				var s = document.createElement("input"); //input element, Submit button
				s.setAttribute('type',"submit");
				s.setAttribute('value',"");

				f.appendChild(i);
				f.appendChild(s);
				
				document.body.appendChild(f);
		        f.submit();
		        var downloadForm = document.getElementById("download-form");
		        document.body.removeChild(downloadForm);
		        
				//me.searchService.exportResults( payload, fieldMetadata );
			});
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
//alert("CLEAR_RESULTS::CONTEXT\n " + JSON.stringify(this.context,null,'\t'));
    	this.searchIcon = this.searchIconOptions.default;
    	//this.inputExpansion = new InputExpansion();
    	if ( this.context.corpus ) {
    		this.context.corpus.results = {};
    		this.context.corpus.removeCounts(); 
    	}
    }
  
    distinctCounts( corpus, maxCount ) {
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

Search.$inject = [ '$http', '$q', 'growl', 'searchService', 'userService', '$cookies' ];

export default Search;