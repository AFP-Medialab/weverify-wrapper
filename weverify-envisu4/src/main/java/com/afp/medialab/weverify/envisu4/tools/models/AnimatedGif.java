package com.afp.medialab.weverify.envisu4.tools.models;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnimatedGif {

	private String imageId;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date creationDate;

	public AnimatedGif(String imageId, Date creationDate) {
		this.imageId = imageId;
		this.creationDate = creationDate;
	}

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

}
