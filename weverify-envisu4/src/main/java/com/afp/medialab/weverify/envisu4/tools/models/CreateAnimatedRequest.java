package com.afp.medialab.weverify.envisu4.tools.models;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CreateAnimatedRequest {

	@NotNull(message = "URLs are mandatory")
	@Size(min = 2)
	private String[] inputURLs;
	
	/**
	 * delay in milliseconds
	 */
	private int delay;
	
	private boolean createVideo = false;

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public String[] getInputURLs() {
		return inputURLs;
	}

	public void setInputURLs(String[] inputURLs) {
		this.inputURLs = inputURLs;
	}

	public boolean isCreateVideo() {
		return createVideo;
	}

	public void setCreateVideo(boolean createVideo) {
		this.createVideo = createVideo;
	}

}
