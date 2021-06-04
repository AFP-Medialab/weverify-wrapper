package com.afp.medialab.weverify.envisu4.tools.models;

import java.util.List;

public class CreateAnimatedGifHistory {

	private List<AnimatedGif> animatedGifs;

	public CreateAnimatedGifHistory(List<AnimatedGif> animatedGifs) {
		this.animatedGifs = animatedGifs;
	}

	public List<AnimatedGif> getAnimatedGifs() {
		return animatedGifs;
	}

	public void setAnimatedGifs(List<AnimatedGif> animatedGifs) {
		this.animatedGifs = animatedGifs;
	}

}
