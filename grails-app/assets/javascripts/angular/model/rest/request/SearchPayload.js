import DocumentQuery from '../../search/elastic/DocumentQuery';
import ExportQuery from '../../search/elastic/ExportQuery';
import AggregationQuery from '../../search/elastic/AggregationQuery';
import PaginationQuery from '../../search/elastic/PaginationQuery';
import EncounterDocQuery from '../../search/elastic/clinical/EncounterDocQuery';
import EncounterAggQuery from '../../search/elastic/clinical/EncounterAggQuery';

class SearchPayload { 
    
	constructor( corpus, search, discriminator, identifier ) {
		if ( !corpus || !search || !discriminator ) throw "Missing corpus, search, or type for SearchPayload";
		
		this.authorizedContext = search.instance.authorizedContext;
		this.mode = search.instance.mode;
		this.uuid = search.instance.uuid;
		this.corpus = corpus.name;
		this.userInput = search.userInput;
		this.query = undefined;
		this.url = corpus.metadata.url;
		this.scrollUrl = undefined;
		this.inputExpansion = search.inputExpansion.on;
		this.distinctCounts = search.instance.distinctCounts.on;
		this.expansionTerms = undefined;
		
		switch (discriminator) {
			case 'DOCS':
				this.query = new DocumentQuery( corpus, search.inputExpansion.expandUserInput(search.userInput) );
				break;
			case 'AGGS':
				this.query =  new AggregationQuery( corpus, search.inputExpansion.expandUserInput(search.userInput) );
		    	break;
			case 'ENC-DOCS':
				search.userInput = '*';
				this.query =  new EncounterDocQuery( corpus, search.inputExpansion.expandUserInput(search.userInput), identifier );
		    	break;
		    case 'ENC-AGGS':
		    	search.userInput = "*";
				this.query =  new EncounterAggQuery( corpus, search.inputExpansion.expandUserInput(search.userInput), identifier );
		    	break;
		    case 'PAGE':
				this.query =  new PaginationQuery( corpus, search.inputExpansion.expandUserInput(search.userInput) );
		    	break;
		    case "RECENT-DOCS":
		    	this.query = new DocumentQuery( corpus, search.inputExpansion.expandUserInput(search.userInput) );
				break;
		    case 'RECENT-AGGS':
				this.query =  new AggregationQuery( corpus, search.inputExpansion.expandUserInput(search.userInput) );
		    	break;
		    case 'EXPORT':
		    	this.scrollUrl = corpus.metadata.scrollUrl;
				this.query = new ExportQuery( corpus, search.inputExpansion.expandUserInput(search.userInput) );
				break;
		    	//need to figure out how to parse fetched query, and assign these parts to new doc and agg queries. Clear currentSearch and assign the parsed values  
			default:
				throw "Unsupported type for SearchPayload";
		}
		this.filterSummary = this.filterDistiller( this.query );
		this.type = this.query.constructor.name.toString();
		
		if ( this.inputExpansion ) {
			this.expansionTerms = search.inputExpansion.terms;
		}
//alert("SearchPayload\n"+JSON.stringify(this,null,'\t'));
	}
	
	//creates string to be used as filterSummary element in UI dropdown
	filterDistiller( query ) {
    	let terms = [];
    	let filterArray = query.query.bool.filter;
    	for ( let filter of filterArray ) {
    		Object.keys(filter).map( function(type,index) {
    			//type will be bool | range
    			if ( type=="bool" ) {
    				let shouldFilterArray = filter.bool.should;
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
    	return terms.join(', ');
    }

}

export default SearchPayload;