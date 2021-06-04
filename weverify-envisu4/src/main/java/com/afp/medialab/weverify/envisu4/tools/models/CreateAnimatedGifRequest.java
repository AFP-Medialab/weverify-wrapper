package com.afp.medialab.weverify.envisu4.tools.models;

import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CreateAnimatedGifRequest {

	@NotNull(message = "URLs are mandatory")
	@Size(min = 2)
	private Set<String> inputURLs;
	
	
	private int delay;

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public Set<String> getInputURLs() {
		return inputURLs;
	}

	public void setInputURLs(Set<String> inputURLs) {
		this.inputURLs = inputURLs;
	}

}
