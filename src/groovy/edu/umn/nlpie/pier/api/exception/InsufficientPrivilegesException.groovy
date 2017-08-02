package edu.umn.nlpie.pier.api.exception

import groovy.transform.InheritConstructors;


@InheritConstructors
class InsufficientPrivilegesException extends PierApiException {
	int status = 403
	String message
	
	InsufficientPrivilegesException(String m) {
		status = s
		message = m
	}
}


