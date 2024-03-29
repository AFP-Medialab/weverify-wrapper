package com.afp.medialab.weverify.envisu4.tools.images;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.afp.medialab.weverify.envisu4.tools.controller.exception.IpolProxyException;
import com.afp.medialab.weverify.envisu4.tools.controller.exception.ServiceErrorCode;
import com.afp.medialab.weverify.envisu4.tools.controller.exception.VideoCreationException;
import com.google.common.io.Files;

@Component
public class FileUtils {

	private static Logger Logger = LoggerFactory.getLogger(FileUtils.class);

	private File tempDir;

	@PostConstruct
	public void init() {
        final Path path = Paths.get(org.apache.commons.io.FileUtils.getTempDirectory().getAbsolutePath(), UUID.randomUUID().toString());
        try {
			tempDir = java.nio.file.Files.createDirectories(path).toFile();
			tempDir.deleteOnExit();
		} catch (IOException e) {
			Logger.error("unable to create temporary folder in system {} ", e.getMessage());
		}
        
		/*tempDir = new File("./temp_image");
		if (!tempDir.exists()) {
			System.out.println("mkdir:" + tempDir.mkdirs());
		}*/
	}
	/**
	 * convert byte array to a temporaty file
	 * @param content byte content
	 * @param md5sumFileName file name
	 * @param format file format (gif, mp4, etc)
	 * @return temp file
	 * @throws VideoCreationException 
	 */
	public File convert(byte[] content, String md5sumFileName, String format) throws IOException {
		File tempFile = new File(this.tempDir, md5sumFileName + "." + format.toLowerCase());
		Files.write(content, tempFile);
		return tempFile;
	}
	
	public byte[] convertFileTobyte(File file) throws IOException {
		byte[] bytes = Files.toByteArray(file);
		file.delete();
		return bytes;
	}

	/**
	 * Create temporary images from url
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 * @throws IpolProxyException
	 */
	public File convert(URL url) throws IOException, IpolProxyException {
		ImageInputStream iis = null;
		try {
			iis = ImageIO.createImageInputStream(url.openStream());
			Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(iis);
			if (imageReaders.hasNext()) {
				ImageReader reader = (ImageReader) imageReaders.next();
				reader.setInput(iis);
				BufferedImage img = reader.read(0);
				String format = reader.getFormatName();
				Logger.debug("formatName: {}", format);
				String md5sumFileName = DigestUtils.md5Hex(url.getFile());
				File convFile = new File(this.tempDir, md5sumFileName + "." + format.toLowerCase());
				ImageIO.write(img, format, convFile);
				return convFile;

			} else {
				throw new IpolProxyException(ServiceErrorCode.IPOL_IMAGE_NOT_FOUND,
						"No Image found in URL " + url.toString());
			}

		} finally {
			if (iis != null)
				iis.close();
		}

	}

	/**
	 * Create temporary file
	 * 
	 * @param file
	 * @return
	 */
	public File convert(MultipartFile file) {
		File convFile = new File(this.tempDir, file.getOriginalFilename());
		try {
			convFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(convFile);
			fos.write(file.getBytes());
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return convFile;
	}
	
	public File getTempDir() {
		return tempDir;
	}
	
	

}
