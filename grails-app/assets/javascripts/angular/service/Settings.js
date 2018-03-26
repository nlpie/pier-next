//import UIService from '../../service/config/UIService';

class Settings {
	
	constructor( $http, growl ) {
		this.$http = $http;
		this.growl = growl;
		this.styles = {
				filters: "",
				exports: ""
		}
		this.corpus = undefined;
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
	}
	
	fetchPrefs() {
		var me = this;
		this.$http.get( APP.ROOT + '/settings/preferences/', { "noop": true } )
		.then( function(response) {
				me.prefs = response.data;
				if (!me.corpus) me.corpus = Object.keys(response.data)[0];
    		});
		/*
    	this.uiService.fetchPreferences()
    		.then( function(response) {
    			//alert(me.hasOwnProperty("styles"));
    			me.prefs = response.data;
    			me.prefs = { f:'f' };
    			alert(JSON.stringify(me));
    		});*/
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
			me.remoteError( e, {ttl:1000} );
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

Settings.$inject = [ '$http', 'growl' ];

export default Settings;