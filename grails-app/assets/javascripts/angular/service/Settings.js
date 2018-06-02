import CorpusAggregationsResponse from '../model/rest/response/CorpusAggregationsResponse';

class Settings {
	
	constructor( $http, $q, growl ) {
		this.$http = $http;
		this.$q = $q;
		this.growl = growl;
		
		this.styles = {
				filters: "",
				exports: ""
		}
		this.corpus = undefined;
		this.prefs = undefined;
		this.filterOptionSizes = [
		                          {value: 1, text: '1'},
		                          {value: 5, text: '5'},
		                          {value: 10, text: '10'},
		                          {value: 25, text: '25'},
		                          {value: 50, text: '50'},
		                          {value: 100, text: '100'},
		                          {value: 250, text: '250'},
		                          {value: 500, text: '500'}
		                        ]; //EXTERNALIZE as property on Field, eg, Field.choices or Field.preferenceChoices
		this.views = {
				"filters": "Filters",
				"exports": "Exports"
		                          
		}; //EXTERNALIZE as property on Field, eg, Field.choices or Field.preferenceChoices
		this.view = Object.keys(this.views)[0];		//filters
		
		this.swap('filters');
		console.info("Settings.js complete");
	}
	
	//TODO this method used?
	fetchPrefs() {
		var me = this;
		this.$http.get( APP.ROOT + '/settings/preferences/', { "noop": true } )
		.then( function(response) {
			alert("dpne");
				me.prefs = new CorpusAggregationsResponse( response.data );
				if (!me.corpus) me.corpus = Object.keys(response.data)[0];
    		});
    }
	
	remoteError( e ) {
    	console.log(JSON.stringify(e,null,'\t'));
    	this.growl.error( e.data.message + " (cause: " + e.data.cause + ") ", {ttl:2000} );
    }
	
	update( id,prop,val, successMsg ) {
		let me = this;
		let payload = { "id":id };
		payload[prop] = val;
		this.$http.post( APP.ROOT + '/settings/update/', payload )
		.then( function(response) {		
			me.growl.success( successMsg, {ttl:1000} );
		})
		.catch( function(e) {
			me.remoteError( e, {ttl:3000} );
		});
	}
	
	changeCorpus( corpus ) {
		this.corpus = corpus;
	}
	
	swap( view ) {
		this.fetchPrefs();
		this.changeClass( view );
		this.view = view;
	}
	
	changeClass( view ) {
		let styles = this.styles;
		for (var property in styles) {
		    if ( property==view ) {
		        styles[property] = 'active';
		    } else {
		    	styles[property] = '';
		    }
		}	
	}
	
	
	
}

Settings.$inject = [ '$http', '$q', 'growl' ];

export default Settings;