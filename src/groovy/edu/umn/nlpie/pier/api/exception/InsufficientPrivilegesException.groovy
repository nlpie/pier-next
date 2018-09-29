package edu.umn.nlpie.pier.api.exception

import groovy.transform.InheritConstructors


@InheritConstructors
class InsufficientPrivilegesException extends PierApiException {
	int status = 403
	//protected String message
	
	InsufficientPrivilegesException(String m) {
		message = m
	}
}


