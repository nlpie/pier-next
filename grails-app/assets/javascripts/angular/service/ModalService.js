
class ModalService {

	constructor( $http, $q, growl, $uibModal ) {
		this.$http = $http;
		this.$q = $q;
		this.growl = growl;
		this.$uibModal = $uibModal;
		this.items = ['item1', 'item2', 'item3'];
		this.modalInstance = undefined;
	}
	
	vectorExpansions( size, controllerName ) {
		var me = this;
	    me.modalInstance = me.$uibModal.open({
	      animation: true,//$ctrl.animationsEnabled,
	      ariaLabelledBy: 'modal-title',
	      ariaDescribedBy: 'modal-body',
	      templateUrl: 'myModalContent.html',
	      controller: controllerName,
	      controllerAs: 'ctrl',
	      bindToController: true,
	      size: size
	    });
	}
	
	ok() {
		this.modalInstance.close();
	}
	
	cancel() {
		this.modalInstance.dismiss();
	}
}

ModalService.$inject = [ '$http', '$q', 'growl', '$uibModal' ];

export default ModalService;