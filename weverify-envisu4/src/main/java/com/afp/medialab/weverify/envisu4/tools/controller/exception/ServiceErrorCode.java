package com.afp.medialab.weverify.envisu4.tools.controller.exception;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum ServiceErrorCode {
	IPOL_SERVICE_ERROR, IPOL_FILE_IMAGE_ERROR, IPOL_IMAGE_NOT_FOUND, IPOL_BAD_URL_ERROR, IPOL_RESOURCE_NOT_FOUND, IPOL_REMOTE_SERVICE_ERROR, IPOL_IMAGE_UPLOAD_EXCEEDED,
	
	ANIMATED_GIF_CREATION_FAILED, ANIMATED_GIF_URL_LOAD_FAILED, VIDEO_CREATION_ERROR_FAILED, VIDEO_URL_LOAD_FAILED;

}
