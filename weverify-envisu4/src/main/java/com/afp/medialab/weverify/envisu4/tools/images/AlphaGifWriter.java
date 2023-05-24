package com.afp.medialab.weverify.envisu4.tools.images;

import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component(value = "alphagGifWriter")
public class AlphaGifWriter implements ICreateAnimatedGif {
	private static Logger Logger = LoggerFactory.getLogger(AlphaGifWriter.class);

	/**
	 * Convert sequence diagram data into gif diagram and write to output stream
	 * 
	 * @param images
	 * @param outputStream
	 * @param delay        Frame delay, unit: milliseconds
	 * @param loop         Whether to play in loop
	 * @param width
	 * @param height
	 */
	public void convert(BufferedImage[] images, ImageOutputStream outputStream, int delay, boolean loop, Integer width,
			Integer height) {
		// Image type
		int imageType = images[0].getType();
		// Zoom parameters
		double sx = width == null ? 0.7 : ((double) width / images[0].getWidth());
		double sy = height == null ? 0.7 : ((double) height / images[0].getHeight());
		Map<RenderingHints.Key,Object> hints = new HashMap<>();
        hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(sx, sy), null);
		//RescaleOp op = new RescaleOp(1.0f, 1.0f, null);
		try {
			Gif gif = new Gif(outputStream, imageType, delay, loop);
			for (BufferedImage image : images) {
				gif.writeToSequence(op.filter(image, null));
			}
			gif.close();
			outputStream.close();
		} catch (Exception e) {
			throw new RuntimeException("GIF convert error", e);
		}
	}

	/**
	 * Convert sequence diagram data into gif diagram and write to output stream
	 * 
	 * @param images
	 * @param outputStream
	 * @param delay        Frame delay, unit: milliseconds
	 * @param loop         Whether to play in loop
	 */
	public void convert(BufferedImage[] images, ImageOutputStream outputStream, int delay, boolean loop) {
		convert(images, outputStream, delay, loop, null, null);
	}

	/**
	 * Convert sequence diagram to gif diagram
	 * 
	 * @param imagePaths
	 * @param gifPath
	 * @param delay      Frame delay, unit: milliseconds
	 * @param loop       Whether to play in loop
	 * @param width
	 * @param height
	 */
	public void convert(String[] imagePaths, String gifPath, int delay, boolean loop, Integer width, Integer height) {
		try {
			BufferedImage[] images = new BufferedImage[imagePaths.length];
			for (int i = 0; i < imagePaths.length; i++) {
				images[i] = ImageIO.read(new File(imagePaths[i]));
			}
			FileImageOutputStream fileImageOutputStream = new FileImageOutputStream(new File(gifPath));
			convert(images, fileImageOutputStream, delay, loop, width, height);
		} catch (Exception e) {
			throw new RuntimeException("GIF convert error", e);
		}
	}

	/**
	 * Convert sequence diagram to gif diagram
	 * 
	 * @param imagePaths
	 * @param gifPath
	 * @param delay      Frame delay, unit: milliseconds
	 * @param loop
	 */
	public void convert(String[] imagePaths, String gifPath, int delay, boolean loop) {
		convert(imagePaths, gifPath, delay, loop, null, null);
	}

	private class Gif {
		private ImageWriter writer;
		private ImageWriteParam params;
		private IIOMetadata metadata;

		private Gif(ImageOutputStream outputStream, int imageType, int delay, boolean loop) throws IOException {
			this.writer = ImageIO.getImageWritersBySuffix("gif").next();
			this.params = writer.getDefaultWriteParam();
			this.params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			this.metadata = writer.getDefaultImageMetadata(ImageTypeSpecifier.createFromBufferedImageType(imageType),
					params);
			// Configure metadata
			this.configureRootMetadata(delay, loop);
			// Set the output stream
			writer.setOutput(outputStream);
			writer.prepareWriteSequence(null);
		}

		private void configureRootMetadata(int delay, boolean loop) throws IIOInvalidTreeException {
			String metaFormatName = metadata.getNativeMetadataFormatName();
			IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(metaFormatName);
			IIOMetadataNode graphicsControlExtensionNode = getNode(root, "GraphicControlExtension");
			graphicsControlExtensionNode.setAttribute("disposalMethod", "none");
			graphicsControlExtensionNode.setAttribute("userInputFlag", "FALSE");
			graphicsControlExtensionNode.setAttribute("transparentColorFlag", "FALSE");
			graphicsControlExtensionNode.setAttribute("delayTime", Integer.toString(delay / 10));
			graphicsControlExtensionNode.setAttribute("transparentColorIndex", "0");
			IIOMetadataNode commentsNode = getNode(root, "CommentExtensions");
	        commentsNode.setAttribute("CommentExtension", "Created by Weverify");
			IIOMetadataNode appExtensionsNode = getNode(root, "ApplicationExtensions");
			IIOMetadataNode child = new IIOMetadataNode("ApplicationExtension");
			child.setAttribute("applicationID", "NETSCAPE");
			child.setAttribute("authenticationCode", "2.0");

			int loopContinuously = loop ? 0 : 1;
			child.setUserObject(
					new byte[] { 0x1, (byte) (loopContinuously & 0xFF), (byte) ((loopContinuously >> 8) & 0xFF) });
			appExtensionsNode.appendChild(child);
			metadata.setFromTree(metaFormatName, root);
		}

		private IIOMetadataNode getNode(IIOMetadataNode rootNode, String nodeName) {
			int nNodes = rootNode.getLength();
			for (int i = 0; i < nNodes; i++) {
				if (rootNode.item(i).getNodeName().equalsIgnoreCase(nodeName)) {
					return (IIOMetadataNode) rootNode.item(i);
				}
			}
			IIOMetadataNode node = new IIOMetadataNode(nodeName);
			rootNode.appendChild(node);
			return (node);
		}

		public void writeToSequence(RenderedImage img) throws IOException {
			writer.writeToSequence(new IIOImage(img, null, metadata), params);
		}

		public void close() throws IOException {
			writer.endWriteSequence();
		}
	}

	@Override
	public byte[] convert(String[] urls, ByteArrayOutputStream output, int delay, boolean loop) throws Exception {
		Logger.debug("alphaGifWriter");
		BufferedImage[] images = new BufferedImage[urls.length];
		int index = 0;
		for (String url : urls) {
			images[index++] = ImageIO.read(new URL(url));
		}
		MemoryCacheImageOutputStream imageOutputStream = new MemoryCacheImageOutputStream(output);
		convert(images, imageOutputStream, delay, loop);
		return output.toByteArray();
	}

}
