import Ontology from '../../ui/Ontology';

class CorpusAggregationsResponse extends Array {
	
	constructor( responseData ) {
		//responseData is a JSON array of objects (FieldPreference) from API via JSON marshaller
		super();
		for ( let obj of responseData ) {
			this.push( new Ontology( obj ) );
		}
//alert(JSON.stringify(this,null,'\t'));
	}

}

export default CorpusAggregationsResponse;

//iterable: for ( let value of [10, 20, 30] ) {}
//Object: for (var prop in obj) { obj[prop]; }
// if (obj.hasOwnProperty(prop)) {
