
class Highlight {
	
	constructor(field) {	
		//this.encoder = "html";
		this.fields = {};
		this.fields[field] = {
            "number_of_fragments": 0,
            "post_tags": ["<\/span>"],
            "pre_tags": ["<span class='hl'>"],
            "fragment_size": 250
		};
    }
}

export default Highlight;