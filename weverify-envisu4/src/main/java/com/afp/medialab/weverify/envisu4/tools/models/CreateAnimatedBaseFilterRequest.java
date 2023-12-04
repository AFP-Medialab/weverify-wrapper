package com.afp.medialab.weverify.envisu4.tools.models;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CreateAnimatedBaseFilterRequest {

	@NotNull(message = "URL is mndatory")
	private String originalURL;
	@NotNull(message = "Base64 content is mandator")
	private String maskFilter;
	/**
	 * delay in milliseconds
	 */
	private int delay;
	
	private boolean createVideo = false;

	public String getOriginalURL() {
		return originalURL;
	}

	public void setOriginalURL(String originalURL) {
		this.originalURL = originalURL;
	}

	public String getMaskFilter() {
		return maskFilter;
	}

	public void setMaskFilter(String maskFilter) {
		this.maskFilter = maskFilter;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public boolean isCreateVideo() {
		return createVideo;
	}

	public void setCreateVideo(boolean createVideo) {
		this.createVideo = createVideo;
	}
	
	
}
