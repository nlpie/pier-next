//import UIService from '../../service/config/UIService';

class SettingsController {
	
	constructor( settings ) {
		this.settings = settings;
	}
}

SettingsController.$inject = [ 'settings' ];

export default SettingsController;