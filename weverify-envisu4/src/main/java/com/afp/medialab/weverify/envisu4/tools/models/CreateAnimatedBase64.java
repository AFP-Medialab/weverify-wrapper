package com.afp.medialab.weverify.envisu4.tools.models;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record CreateAnimatedBase64(
		@NotNull(message="URL is mndatory")
		String original,
		@NotNull(message="Base64 content is mandator")
		String maskFilter,
		Integer delay,
		Boolean createVideo)
{
	public CreateAnimatedBase64 {
		if(delay == null)
			delay = 500;
		if(createVideo == null)
			createVideo = false;
	}

}
