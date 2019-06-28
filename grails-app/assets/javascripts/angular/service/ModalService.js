
class ModalService {

	constructor( $http, $q, growl, $uibModal, currentSearch ) {
		this.$http = $http;
		this.$q = $q;
		this.growl = growl;
		this.$uibModal = $uibModal;
		this.expansionInstance = undefined;
		this.helpInstance = undefined;
		this.currentSearch = currentSearch;
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
	    
	    //https://stackoverflow.com/questions/30356844/angularjs-bootstrap-modal-closing-call-when-clicking-outside-esc
	    me.expansionInstance.result.then(
	      function(){
	    	  //OK
	          //alert("Exp Closed!!!");
	          me.expDetermineDirty();
	          
	      }, 
	      function(){
	    	  //esc, click off to close, or Cancel
	          //alert("Exp Dismissed!!!");
	          me.expDetermineDirty();
	      }
	    );
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
	
	expDetermineDirty() {
		//if ( this.currentSearch.inputExpansion.cardinality() ) {
      	  	this.currentSearch.dirty();
        //}
	}
	
	expOk() {
		this.expansionInstance.close();
	}
	
	expClear() {
		this.expansionInstance.dismiss();
	}
	
	helpOk() {
		this.helpInstance.close();
	}
	
	helpCancel() {
		this.helpInstance.dismiss();
	}
}

ModalService.$inject = [ '$http', '$q', 'growl', '$uibModal', 'currentSearch' ];

export default ModalService;