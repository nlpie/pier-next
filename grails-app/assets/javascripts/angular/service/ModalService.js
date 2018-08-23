
class ModalService {

	constructor( $http, $q, growl, $uibModal ) {
		this.$http = $http;
		this.$q = $q;
		this.growl = growl;
		this.$uibModal = $uibModal;
		this.expansionInstance = undefined;
		this.helpInstance = undefined;
	}
	
	vectorExpansions( size, controllerName ) {
		var me = this;
	    me.expansionInstance = me.$uibModal.open({
	      animation: true,//$ctrl.animationsEnabled,
	      ariaLabelledBy: 'modal-title',
	      ariaDescribedBy: 'modal-body',
	      templateUrl: 'expansionSelection.template',
	      controller: controllerName,
	      controllerAs: 'ctrl',
	      bindToController: true,
	      size: size
	    });
	}
	
	queryHelp( size, controllerName ) {
		var me = this;
	    me.helpInstance = me.$uibModal.open({
	      animation: true,//$ctrl.animationsEnabled,
	      ariaLabelledBy: 'modal-title',
	      ariaDescribedBy: 'modal-body',
	      templateUrl: 'queryHelp.template',
	      controller: controllerName,
	      controllerAs: 'hCtrl',
	      bindToController: true,
	      size: size
	    });
	}
	
	ok() {
		this.expansionInstance.close();
	}
	
	cancel() {
		this.expansionInstance.dismiss();
	}
	
	helpOk() {
		this.helpInstance.close();
	}
	
	helpCancel() {
		this.helpInstance.dismiss();
	}
}

ModalService.$inject = [ '$http', '$q', 'growl', '$uibModal' ];

export default ModalService;