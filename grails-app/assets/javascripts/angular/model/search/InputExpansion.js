import Term from './Term';

class InputExpansion {
	
	constructor() {	
		this.terms = []; 		//array of Term objects
		this.style = {};
		this.tip="tip";
    }
	
	parseUserInput( userInput ) {
		/*// term1 AND (term2 OR term3) NOT (term4 OR "multiterm phrase")
		userInput = userInput.replace(/\(/g,"").replace(/\)/g,"");
		userInput = userInput.replace(/AND/g,"").replace(/OR/g,"").replace(/NOT/g,"");
		let terms = userInput.split(" ");
		for ( let term of terms ) {
			term = term.trim();
		}
		return terms;
		*/
		//alert("parse in input expansion");
		let tokens = userInput.split(" ");
		//alert(JSON.stringify(tokens));
		let embeddingCandidates = [];
		let phrase = false;
		for ( let token of tokens ) {
			let field = false;
			if ( token.includes(":") ) field = true;
			if ( token.startsWith('\\"') ) phrase = true;
			if ( !phrase && !field && ( token!="AND" && token!="OR" && token!="NOT") ) {
				embeddingCandidates.push( token.replace(/\(/g,"").replace(/\)/g,"") );
			}
			if ( token.endsWith('\\"') || token.includes('\\"') ) {
				alert(token);
				phrase = false;
			}
		}
		//alert( JSON.stringify(embeddingCandidates) );
		return embeddingCandidates;
	}
	
	parseUserInputIntoEmbeddingCandidates( userInput ) {
		let tokens = userInput.split(" ");
		let embeddingCandidates = [];
		let phrase = false;
		for ( let token of tokens ) {
			let field = false;
			if ( token.includes(":")) field = true;
			if ( token.startsWith('\"') ) phrase = true;
			if ( !phrase && !field && ( token!="AND" && token!="OR" && token!="NOT") ) embeddingCandidates.push( token );
			if ( token.endsWith('\"') || token.includes('\"') ) phrase = false;
		}
		//alert( JSON.stringify(embeddingCandidates) );
		return embeddingCandidates;
	}
	
	cardinality() {
		let count = 0;
		for ( let term of this.terms ) {
			count+=term.expandUsing.length;
		}
		if ( count>0 ) {
			this.style = { 'color':'green' };
		}
		return count;
	}
	
	reset() {
		this.terms = [];
		this.style = {};
	}
	
	add( targetTerm, word ) {
		//targetTerm is a single token in the search box
		//word is the token/word to be added (or removed) as an expansion term
    	let existingTerm = this.findTermByTarget( targetTerm );
    	if ( existingTerm ) {
    		//alert("appending");
    		let expandedBy = false;
    		for ( let expansion of existingTerm.expandUsing ) {
    			if ( expansion==word ) expandedBy= true;
    		}
    		if ( !expandedBy ) {
    			existingTerm.expandUsing.push( word );
    		} else {
    			let idx = existingTerm.expandUsing.indexOf(word);
    			if (idx !== -1) {
    				existingTerm.expandUsing.splice(idx,1);
    				//alert("Expansion term [" + word + "] removed as expansion term for " + existingTerm.target);
    			}
    		}
    	} else {
    		//alert("adding");
    		this.terms.push ( new Term(targetTerm, word) );
    	}
    	//alert(JSON.stringify(this.terms,null,'\t'));
    }
	
	expandUserInput( userInput ) {
		for ( let term of this.terms ) {
			let expandedForm = "(" + term.target + " OR " + term.expandUsing.join(" OR ") + ")";
			userInput = userInput.replace( term.target, expandedForm );
		}
		console.log( userInput );
		return userInput;
	}
	
	findTermByTarget ( target ) {
		let existingTerm = undefined;
		for ( let term of this.terms ) {
    		if ( term.target==target ) {
    			existingTerm = term;
    		}
    	}
		return existingTerm;
	}
	
	targetHasExpansions( target ) {
		let expansions = false;
		if ( this.findTermByTarget( target ) ) expansions = true;
		return expansions;
	}
	
	flatten( target ) {
		let term = this.findTermByTarget( target );
		return term.expandUsing.join(", ");
	}
	
	targetLabel ( target ) {
		//alert(" targetlabel " + target);
		let term = this.findTermByTarget( target );
		if ( !term ) return target;
		//if term is found, it will have at least one expansion term
		return target + " (" + term.expandUsing.length + ")"; 
	}
	
}

export default InputExpansion;