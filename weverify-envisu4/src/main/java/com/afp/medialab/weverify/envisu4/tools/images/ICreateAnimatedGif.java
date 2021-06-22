package com.afp.medialab.weverify.envisu4.tools.images;

import java.io.ByteArrayOutputStream;
import java.util.Set;

public interface ICreateAnimatedGif {

	public byte[] convert(Set<String> urls, ByteArrayOutputStream animatedGif, int delay, boolean loop) throws Exception;
}
