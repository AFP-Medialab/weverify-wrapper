package com.afp.medialab.weverify.envisu4.tools.controller.exception;

import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class FileUploadExceptionAdvice {

	private static Logger Logger = LoggerFactory.getLogger(FileUploadExceptionAdvice.class);
	
	@ExceptionHandler({ MaxUploadSizeExceededException.class })
	public ResponseEntity<Envisu4ServiceError> handle(FileSizeLimitExceededException ex) {
		Logger.error(ServiceErrorCode.IPOL_IMAGE_UPLOAD_EXCEEDED +" - " + ex.getMessage());
		Envisu4ServiceError ipolServiceError = new Envisu4ServiceError(ServiceErrorCode.IPOL_IMAGE_UPLOAD_EXCEEDED,
				ex.getMessage());
		return new ResponseEntity<Envisu4ServiceError>(ipolServiceError, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
