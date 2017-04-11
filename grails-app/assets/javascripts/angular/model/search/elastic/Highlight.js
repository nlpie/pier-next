
class Highlight {
	constructor() {	
		this.encoder = "html";
		this.fields = {"text": {
            "number_of_fragments": 15,
            "post_tags": ["<\/span>"],
            "pre_tags": ["<span class='hl'>"],
            "fragment_size": 300
		}}
    }
}

export default Highlight;