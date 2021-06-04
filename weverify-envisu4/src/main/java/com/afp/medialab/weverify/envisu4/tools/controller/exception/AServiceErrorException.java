package com.afp.medialab.weverify.envisu4.tools.controller.exception;

public abstract class AServiceErrorException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8061991785019248123L;

	private Envisu4ServiceError ipolServiceError;

	public AServiceErrorException() {
	}

	public AServiceErrorException(ServiceErrorCode errorCode, String message) {
		super(message);
		this.ipolServiceError = new Envisu4ServiceError(errorCode, message);
	}

	public AServiceErrorException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public AServiceErrorException(String message, Throwable cause) {
		super(message, cause);
	}

	public AServiceErrorException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public Envisu4ServiceError getIpolServiceError() {
		return ipolServiceError;
	}

	public void setIpolServiceError(Envisu4ServiceError ipolServiceError) {
		this.ipolServiceError = ipolServiceError;
	}

}
