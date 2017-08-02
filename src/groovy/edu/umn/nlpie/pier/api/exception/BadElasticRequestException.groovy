package edu.umn.nlpie.pier.api.exception

import groovy.transform.InheritConstructors;


@InheritConstructors
class BadElasticRequestException extends BadRequestException {
	int status = 400
}


