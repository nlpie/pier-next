package edu.umn.nlpie.pier.api.exception

class PierApiException extends RuntimeException {
	
	protected int status = 500 	//each subclass will have a unique HTTP status value
	String message				//can be set by 'throw new BadRequestException(message:"error msg here")
	
	@Override
	String getMessage() {
		message				//meant to provide same behavior as Exception.getMessage()
	}
}

