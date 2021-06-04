package com.afp.medialab.weverify.envisu4.tools.controller.exception;

public class Envisu4ServiceError {

	private ServiceErrorCode errorCode;
	private String errorMessage;

	public Envisu4ServiceError(ServiceErrorCode serviceErrorCode, String message) {
		this.errorCode = serviceErrorCode;
		this.errorMessage = message;
	}

	public ServiceErrorCode getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(ServiceErrorCode errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
