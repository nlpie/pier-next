//TODO rename to UserTerm, InputTerm, TermInput?
class Term {
	
	constructor( term, expansionTerm ) {
		//alert( "new term: " + term + " | " + expansionTerm );
		this.target = term;
		this.expandUsing = [ expansionTerm ];
    }
	
	cardinality() {
		return this.expandUsing.length;
	}
	
}

export default Term;