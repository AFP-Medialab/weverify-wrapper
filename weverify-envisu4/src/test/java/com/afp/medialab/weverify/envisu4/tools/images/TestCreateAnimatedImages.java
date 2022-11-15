package com.afp.medialab.weverify.envisu4.tools.images;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

class TestCreateAnimatedImages {

	private AlphaGifWriter alphaGifWriter = new AlphaGifWriter();
	
	private AnimatedGIFWriter animatedGIFWriter = new AnimatedGIFWriter();

	private String image1 = "/Users/bertrand/git/afp/weverify/weverify-wrapper/weverify-envisu4/src/test/resources/Tampered.jpg";
	private String image2 = "/Users/bertrand/git/afp/weverify/weverify-wrapper/weverify-envisu4/src/test/resources/reveal-mask-1.png";
	private String output1 = "/Users/bertrand/git/afp/weverify/weverify-wrapper/weverify-envisu4/src/test/resources/output-1.gif";
	private String video1 = "/Users/bertrand/git/afp/weverify/weverify-wrapper/weverify-envisu4/src/test/resources/output-video.mp4";
	private String video2 = "/Users/bertrand/git/afp/weverify/weverify-wrapper/weverify-envisu4/src/test/resources/output-2-video.mp4";

	private String url1 = "https://mever.iti.gr/envisu4/uploads/Tampered.jpg";
	private String url2 = "https://mever.iti.gr/envisu4/api/v4/images/77174351879669c966fb411143c86ab5/DQOutput_mask.png";
	private String output2 = "/Users/bertrand/git/afp/weverify/weverify-wrapper/weverify-envisu4/src/test/resources/output-2.gif";
	private String output7 = "/Users/bertrand/git/afp/weverify/weverify-wrapper/weverify-envisu4/src/test/resources/output-7.gif";

	private String output3 = "/Users/bertrand/git/afp/weverify/weverify-wrapper/weverify-envisu4/src/test/resources/output-3.gif";
	private String output4 = "/Users/bertrand/git/afp/weverify/weverify-wrapper/weverify-envisu4/src/test/resources/output-4.gif";
	private String output5 = "/Users/bertrand/git/afp/weverify/weverify-wrapper/weverify-envisu4/src/test/resources/conver-gueulle.g";
	private String output6 = "/Users/bertrand/git/afp/weverify/weverify-wrapper/weverify-envisu4/src/test/resources/conver-gueulle.webp";

	
	
	/*public void main(String args[]) {
		TestCreateAnimatedImages tcav = new TestCreateAnimatedImages();
		tcav.testVideoJcodec();
	}*/
	//@org.junit.jupiter.api.Test
	void test() {
		String inputs[] = new String[] { image1, image2 };
		alphaGifWriter.convert(inputs, output1, 500, true);
	}

	// @Test
	void testUrls() throws Exception {
		String inputs[] = new String[] { url1, url2 };
		// Set<String> targetSet = new HashSet<String>(Arrays.asList(inputs));
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		byte[] content = alphaGifWriter.convert(inputs, arrayOutputStream, 500, true);
		FileUtils.writeByteArrayToFile(new File(output2), content);

	}

	//@Test
	void testConverter() throws IOException {
		convertFormat(image1, output6, "WEBP");
	}
	

	public boolean convertFormat(String inputImagePath, String outputImagePath, String formatName) throws IOException {
		FileInputStream inputStream = new FileInputStream(inputImagePath);
		FileOutputStream outputStream = new FileOutputStream(outputImagePath);

		// reads input image from file
		BufferedImage inputImage = ImageIO.read(inputStream);

		// writes to the output image in specified format
		boolean result = ImageIO.write(inputImage, formatName, outputStream);

		// needs to close the streams
		outputStream.close();
		inputStream.close();

		return result;
	}

	//@Test
	void testAnimatesGifWriter() throws Exception {
		String inputs[] = new String[] { url1, url2 };
		// Set<String> targetSet = new HashSet<String>(Arrays.asList(inputs));
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		byte[] content = animatedGIFWriter.convert(inputs, arrayOutputStream, 500, true);
		FileUtils.writeByteArrayToFile(new File(output7), content);
	}
	//@Test
	void testGenerateGif() {
		 DataInputStream input = null;
		 try {
			 input = new DataInputStream(new FileInputStream(output7));
			 while(true) {
	                char num;
	                try {
	                    num = input.readChar();
	                   // System.out.println("Reading from file: " + num);
	                }
	                catch (EOFException ex1) {
	                	System.out.println("EOF");
	                    break; //EOF reached.
	                }
	                catch (IOException ex2) {
	                    System.err.println("An IOException was caught: " + ex2.getMessage());
	                    ex2.printStackTrace();
	                }
			 }
		 }
		 catch (IOException ex) {
	            System.err.println("An IOException was caught: " + ex.getMessage());
	            ex.printStackTrace();
	        }
	        finally {
	            try {
	                // Close the input stream.
	                input.close();
	            }
	            catch(IOException ex) {
	                System.err.println("An IOException was caught: " + ex.getMessage());
	                ex.printStackTrace();
	            }
	        }
	}
	
	
	//Œ@org.junit.jupiter.api.Test
	public void testFfmpeg() {
		try {
			FFmpeg fFmpeg = new FFmpeg("/usr/local/bin/ffmpeg");
			FFmpegBuilder builder = new FFmpegBuilder()
					.setInput(output1)
					.overrideOutputFiles(true)
					.addOutput(video2)
					.setFormat("mp4")
					.setVideoMovFlags("faststart")
					.setVideoPixelFormat("yuv420p")
					.setVideoFilter("scale=trunc(iw/2)*2:trunc(ih/2)*2")
					.setVideoFrameRate(1.0)
					.done();
			
			//ffmpeg -i output-1.gif -movflags faststart -pix_fmt yuv420p -vf "scale=trunc(iw/2)*2:trunc(ih/2)*2" -r 1 output-1.mp4
			
			FFmpegExecutor executor = new FFmpegExecutor(fFmpeg);
			executor.createJob(builder).run();

			// Or run a two-pass encode (which is better quality at the cost of being slower)
			//executor.createJob(builder).run();
			//executor.createTwoPassJob(builder).run();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalArgumentException e) {
			System.out.println("do nothing " + e.getMessage());
		}
		
	}
	
}
