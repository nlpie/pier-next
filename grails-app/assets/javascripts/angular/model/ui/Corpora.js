import Corpus from './Corpus';

class Corpora extends Array {
	
	constructor( obj ) {
		//obj is a nested JSON array of objects (Corpus) from API
		super();
		for ( let o of obj ) {
			this.push( new Corpus( o ) );
		}
//alert(JSON.stringify(this,null,'\t'));
	}

}

export default Corpora;

//iterable: for ( let value of [10, 20, 30] ) {}
//Object: for (var prop in obj) { obj[prop]; }
// if (obj.hasOwnProperty(prop)) {