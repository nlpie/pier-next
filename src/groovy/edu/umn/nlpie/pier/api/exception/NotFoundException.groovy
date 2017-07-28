package edu.umn.nlpie.pier.api.exception

import groovy.transform.InheritConstructors

class NotFoundException extends PierApiException {
	int status = 404
}

