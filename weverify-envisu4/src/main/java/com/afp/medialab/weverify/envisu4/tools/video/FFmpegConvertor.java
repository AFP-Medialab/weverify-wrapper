package com.afp.medialab.weverify.envisu4.tools.video;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.afp.medialab.weverify.envisu4.tools.controller.exception.ServiceErrorCode;
import com.afp.medialab.weverify.envisu4.tools.controller.exception.VideoCreationException;
import com.afp.medialab.weverify.envisu4.tools.images.FileUtils;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

@Component("FFmpegConvertor")
public class FFmpegConvertor {

	private static Logger Logger = LoggerFactory.getLogger(FFmpegConvertor.class);

	@Value("${application.envisu4.ffmpeg.path}")
	private String FFMPEG_COMMAND;
	private FFmpeg FFmpeg;
	private SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Autowired
	private FileUtils fileUtils;

	@PostConstruct
	public void initFFmpeg() throws IOException {
		Logger.info("init ffmpeg with path {}", FFMPEG_COMMAND);
		FFmpeg = new FFmpeg(FFMPEG_COMMAND);
	}

	public byte[] convert(byte[] animatedGif, String md5sum, int delay) throws VideoCreationException {
		File tempAnimatedGifFile = null;
		try {
			tempAnimatedGifFile = fileUtils.convert(animatedGif, md5sum, "gif");
		} catch (IOException e) {
			Logger.error("Error creating temporary file for animated gif {}", md5sum);
			throw new VideoCreationException(ServiceErrorCode.VIDEO_CREATION_ERROR_FAILED, "Fail to create video");
		}
		File videoFile = new File(fileUtils.getTempDir(), md5sum + ".mp4");
		Logger.debug("input file path {}", tempAnimatedGifFile.getAbsolutePath());
		Logger.debug("output file path {}", videoFile.getAbsolutePath());
		double rate = delayToRate(delay);
		String date = format1.format(Calendar.getInstance().getTime());
		Logger.debug("rate {}", rate);
		FFmpegBuilder builder = new FFmpegBuilder().setInput(tempAnimatedGifFile.getAbsolutePath())
				.overrideOutputFiles(true).addOutput(videoFile.getAbsolutePath()).setFormat("mp4")
				.addMetaTag("creation_time", date)
				.setVideoMovFlags("faststart").setVideoPixelFormat("yuv420p")
				.setVideoFilter("scale=trunc(iw/2)*2:trunc(ih/2)*2").setVideoFrameRate(rate).done();

		try {
			FFmpegExecutor executor = new FFmpegExecutor(FFmpeg);
			executor.createJob(builder).run();
		} catch (IOException e) {
			Logger.error("FFMPEG error for {}", md5sum);
			throw new VideoCreationException(ServiceErrorCode.VIDEO_CREATION_ERROR_FAILED, "Fail to create video");
		}

		try {
			return fileUtils.convertFileTobyte(videoFile);
		} catch (IOException e) {
			Logger.error("failed converting output video file {}", md5sum);
			throw new VideoCreationException(ServiceErrorCode.VIDEO_CREATION_ERROR_FAILED, "Fail to create video");
		} finally {
			if (tempAnimatedGifFile.exists())
				tempAnimatedGifFile.delete();
		}

	}

	/**
	 * convert delay in milliseconds to time rate
	 * 
	 * @param delay
	 * @return
	 */
	private double delayToRate(int delay) {
		BigDecimal bdelay = new BigDecimal(delay);
		BigDecimal decRate = new BigDecimal(1).divide(bdelay.divide(new BigDecimal(1000)), 2, RoundingMode.HALF_UP);
		double rate = decRate.doubleValue();
		if (rate < 0.5)
			rate = 0.5;
		else if (rate > 4.0)
			rate = 4.0;
		return rate;
	}

}
