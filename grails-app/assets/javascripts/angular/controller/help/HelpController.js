
class HelpController {
	
	constructor( $scope, currentSearch, modalService, searchService ) {
		this.currentSearch = currentSearch;
		this.modalService = modalService;
		this.searchService = searchService;
		this.queries = [];
	}
	
	//convenience method for easily proofing state of objects
	show( obj ){
		alert(JSON.stringify(obj,null,'\t'));
	}
	
	$onInit() {
		let me = this;
		let queries= [
		              {query: "heart failure", name:"keyword search",  explanation:"Find notes containing both <span class=\"i\">heart</span> and <span class=\"i\">failure</span>; relative position does not matter"},
		              {query: '"heart failure"', name:"phrase search", explanation:"Find where the terms <span class=\"i\">heart</span> and <span class=\"i\">failure</span> occur next to one another in the same note"},
		              {query: 'heart AND failure', name:"", explanation:"Logical <span class=\"i\">AND</span> query; same as <span class=\"i\">heart failure</span>, default search behavior"}, 
		              {query: 'heart OR failure', name:"", explanation:"Logical <span class=\"i\">OR</span> query; find notes containing either <span class=\"i\">heart</span> or <span class=\"i\">failure</span>"},
		              {query: 'heart NOT failure', name:"", explanation:"Logical <span class=\"i\">NOT</span> query; find notes containing <span class=\"i\">heart</span> and missing (does not contain) <span class=\"i\">failure</span>"},
		              {query: '"heart failure"+female', name:"", explanation:"Find notes containing the phrase <span class=\"i\">\"heart failure\"</span> and the term <span class=\"i\">female</span>; <span class=\"i\">+</span> and <span class=\"i\">AND</span> are equivalant operators"},
		              {query: '"heart irregular"~10', name:"proximity searches", explanation:"Find notes containing the terms <span class=\"i\">heart</span> and <span class=\"i\">irregular</span> within 10 terms of each other"},
		              {query: 'mrn:xxxxxxxxxxx', name:"mrn search", explanation:"Restrict results to the specified MRN. Can be used in combination with other terms, e.g., keywords and/or service_date(s)"},
		              {query: 'service_date:[2018-07-07 TO 2018-07-14]', name:"range searches", explanation:"Restrict results to those with a service date within a range. Ranges using [] are inclusive; use {} for exclusive ranges; these can be used in combination. Wildcards can be used as upper or lower bounds, e.g., service_date:[* TO 2018-12-31]. Single service date values are also permitted, e.g., service_date:2012-06-02"},
		              {query: 'cuis:C0033213', name:"umls cui searches", explanation:"Find notes tagged with UMLS CUIs (Concept Unique Identifier). Can be combined using logical AND / OR operators, e.g., cuis:C0039796 OR cuis:C2137071"}
        ];
		me.queries = queries;
		me.searchService.fetchSavedQueries();
	}
}

HelpController.$inject = [ '$scope', 'currentSearch', 'modalService', 'searchService' ];

export default HelpController;