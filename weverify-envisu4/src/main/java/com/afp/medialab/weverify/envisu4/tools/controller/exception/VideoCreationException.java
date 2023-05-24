package com.afp.medialab.weverify.envisu4.tools.controller.exception;

public class VideoCreationException extends AServiceErrorException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4528078095221895034L;

	public VideoCreationException(ServiceErrorCode errorCode, String message) {
		super(errorCode, message);
	}


}
