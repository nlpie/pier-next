package edu.umn.nlpie.pier.api.exception



//@InheritConstructors
class InsufficientPrivilegesException extends PierApiException {
	int status = 403
	//protected String message
	
	
	
	public InsufficientPrivilegesException(String m) {
		message = m
	}
}


