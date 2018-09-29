import BucketCountQuery from '../../search/elastic/BucketCountQuery';
import CardinalityOnlyQuery from '../../search/elastic/CardinalityOnlyQuery';
import SingleFieldScrollCountQuery from '../../search/elastic/SingleFieldScrollCountQuery';

class CountPayload { 
    
	constructor( corpus, search, aggregation, maxCount ) {
		if ( !corpus || !search || !aggregation || !maxCount ) throw "Missing corpus, search, aggregation, or maxCount for CountPayload";
		
		this.corpus = corpus.name;
		this.uuid = search.instance.uuid;
		this.query = undefined;
		this.url = corpus.metadata.url;
		this.label = aggregation.label;
		
		/*
		var payload = { 
				"corpus": corpus.name, 
				"countType": countType, 	//derived
				"label": aggregation.label, 
				"url": url, 
				"query": query,
				"uuid": me.instance.uuid
			};
		*/
		
		let discriminator = ( maxCount<=15000000 ) ? "BUCKET" : "SCROLL";	//TODO externalize maxBuckets threshold
		this.countType = discriminator;

		/*if ( countType=='scroll' ) query = new SingleFieldScrollCountQuery( corpus, me.inputExpansion.expandUserInput(me.userInput), aggregation.label, aggregation.fieldName );
		if ( countType=='bucket' ) {
			if ( aggregation.field.fieldName=='note_id' ) {
				query = new CardinalityOnlyQuery( corpus, me.inputExpansion.expandUserInput(me.userInput), aggregation.label, aggregation.field.fieldName );
			} else {
				query = new BucketCountQuery( corpus, me.inputExpansion.expandUserInput(me.userInput), aggregation.label, aggregation.field.fieldName, maxCount );
			}
		}*/
		
		switch (discriminator) {
			case 'SCROLL':
				this.query = new SingleFieldScrollCountQuery( corpus, search.inputExpansion.expandUserInput(search.userInput), aggregation.label, aggregation.fieldName );
				break;
			case 'BUCKET':
				this.query = new BucketCountQuery( corpus, search.inputExpansion.expandUserInput(search.userInput), aggregation.label, aggregation.field.fieldName, maxCount );
		    	break;
			default:
				throw "Unsupported count type for CountPayload";
		}

	}

}

export default CountPayload;