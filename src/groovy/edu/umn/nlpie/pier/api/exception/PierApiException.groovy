package edu.umn.nlpie.pier.api.exception

class PierApiException extends RuntimeException {
	
	protected int status = 500 	//each subclass will have a unique HTTP status value
	String error				//can be set by 'throw new BadRequestException(error:"error msg here")
	
	@Override
	String getMessage() {
		error				//meant to provide same behavior as Exception.getMessage()
	}
}

