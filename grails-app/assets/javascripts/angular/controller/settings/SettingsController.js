
class SettingsController {
	
	constructor( settings ) {
		this.settings = settings;
	}
	
	//convenience method for easily proofing state of objects
	show( obj ){
		alert(JSON.stringify(obj,null,'\t'));
	}
}

SettingsController.$inject = [ 'settings' ];

export default SettingsController;