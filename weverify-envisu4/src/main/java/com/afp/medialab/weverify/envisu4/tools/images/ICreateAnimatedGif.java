package com.afp.medialab.weverify.envisu4.tools.images;

import java.io.ByteArrayOutputStream;

public interface ICreateAnimatedGif {

	public byte[] convert(String[] urls, ByteArrayOutputStream animatedGif, int delay, boolean loop) throws Exception;
}
