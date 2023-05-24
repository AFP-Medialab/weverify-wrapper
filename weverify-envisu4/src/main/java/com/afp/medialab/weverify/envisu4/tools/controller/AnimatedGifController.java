package com.afp.medialab.weverify.envisu4.tools.controller;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.imageio.IIOException;
import javax.validation.Valid;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.afp.medialab.weverify.envisu4.dao.entities.Image;
import com.afp.medialab.weverify.envisu4.dao.repository.ImageDbRepository;
import com.afp.medialab.weverify.envisu4.tools.controller.exception.AnimatedGifCreationException;
import com.afp.medialab.weverify.envisu4.tools.controller.exception.Envisu4ServiceError;
import com.afp.medialab.weverify.envisu4.tools.controller.exception.ServiceErrorCode;
import com.afp.medialab.weverify.envisu4.tools.controller.exception.VideoCreationException;
import com.afp.medialab.weverify.envisu4.tools.images.ICreateAnimatedGif;
import com.afp.medialab.weverify.envisu4.tools.models.AnimatedGif;
import com.afp.medialab.weverify.envisu4.tools.models.CreateAnimatedGifHistory;
import com.afp.medialab.weverify.envisu4.tools.models.CreateAnimatedRequest;
import com.afp.medialab.weverify.envisu4.tools.video.FFmpegConvertor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Validated
@Tag(name = "Animated GIF API.", description = "Operation starts with /open are not authenticated.")
public class AnimatedGifController {

	@Autowired
	private ImageDbRepository imageDbRepository;

	@Autowired
	// @Qualifier("alphagGifWriter")
	@Qualifier("animatedGifWriter")
	private ICreateAnimatedGif createAnimatedGif;
	
	@Autowired
	private FFmpegConvertor ffmpegConvertor;

	private static Logger Logger = LoggerFactory.getLogger(AnimatedGifController.class);

	@Operation(summary = "Create animated Video", description = "Create an animated Video from several image URLs")
	@RequestMapping(path = {
			"/video" }, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = "video/mp4")
	public ResponseEntity<Resource> createAnimatedVideoforURL(
			@RequestBody @Valid CreateAnimatedRequest createAnimatedGifRequest, @AuthenticationPrincipal Jwt principal)
			throws Exception {
		Logger.info("POST /video - userId: {}", principal.getClaimAsString("sub"));
		try {
			byte[] content = createAnimatedGif(createAnimatedGifRequest);
			String md5sum = DigestUtils.md5Hex(content);
			storeMediaContent(content, md5sum);
			//create mp4
			byte[] videoContent = ffmpegConvertor.convert(content, md5sum, createAnimatedGifRequest.getDelay());
			MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
			headers.add("videoId", md5sum);
			ResponseEntity<Resource> response = new ResponseEntity<Resource>(new ByteArrayResource(videoContent), headers,
					HttpStatus.OK);
			return response;
		}
		catch (IIOException e) {
			Logger.error("Failed loading resource", e);
			throw new AnimatedGifCreationException(ServiceErrorCode.VIDEO_URL_LOAD_FAILED,
					"Fail loading input resource");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Logger.error("Video General error", e);
			throw new AnimatedGifCreationException(ServiceErrorCode.VIDEO_CREATION_ERROR_FAILED,
					"Error creating image ");
		}
	}

	@Operation(summary = "Create animated Gif", description = "Create an animated Gif from several image URLs")
	@RequestMapping(path = {
			"/animated" }, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.IMAGE_GIF_VALUE)
	public ResponseEntity<Resource> createAnimatedGifForURL(
			@RequestBody @Valid CreateAnimatedRequest createAnimatedGifRequest, @AuthenticationPrincipal Jwt principal)
			throws Exception {
		Logger.info("POST /animated - userId: {}", principal.getClaimAsString("sub"));
		try {
			byte[] content = createAnimatedGif(createAnimatedGifRequest);
			String md5sum = DigestUtils.md5Hex(content);
			storeMediaContent(content, md5sum);
			MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
			headers.add("imageId", md5sum);
			ResponseEntity<Resource> response = new ResponseEntity<Resource>(new ByteArrayResource(content), headers,
					HttpStatus.OK);
			return response;
			// return new ByteArrayResource(content);

		} catch (IIOException e) {
			Logger.error("Failed loading resource", e);
			throw new AnimatedGifCreationException(ServiceErrorCode.ANIMATED_GIF_URL_LOAD_FAILED,
					"Fail loading input resource");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Logger.error("General error", e);
			throw new AnimatedGifCreationException(ServiceErrorCode.ANIMATED_GIF_CREATION_FAILED,
					"Error creating image ");
		}
	}

	@Operation(summary = "get stored animated Gif", description = "Get animated Gif generated from database")
	@RequestMapping(path = { "/animated/{imageId}" }, method = RequestMethod.GET, produces = MediaType.IMAGE_GIF_VALUE)
	public Resource getImageFromId(@PathVariable String imageId, @AuthenticationPrincipal Jwt principal) {
		Logger.info("GET /animated/{} - user_id: {}", imageId, principal.getClaimAsString("sub"));
		byte[] content = getStoreContent(imageId);
		return new ByteArrayResource(content);

	}
	
	@Operation(summary = "create mp4 from stored animated gif", description = "Create mp4 from stored animated giffrom database")
	@RequestMapping(path = { "/video/{imageId}", "/video/{imageId}/{delay}" }, method = RequestMethod.GET, produces ="video/mp4")
	public Resource getVideofromId(@PathVariable String imageId, @PathVariable() Optional<Integer> delay, @AuthenticationPrincipal Jwt principal) throws AnimatedGifCreationException {
		Logger.info("GET /video/{} - user_id: {}", imageId, principal.getClaimAsString("sub"));
		int intDelay = 500;
		if(delay.isPresent())
			intDelay = delay.get().intValue();
			
		byte[] content = getStoreContent(imageId);
		//content exist
		try {
			byte[] videoContent = ffmpegConvertor.convert(content, imageId, intDelay);
			return new ByteArrayResource(videoContent);
		} catch (VideoCreationException e) {
			Logger.error("Video General error", e);
			throw new AnimatedGifCreationException(ServiceErrorCode.VIDEO_CREATION_ERROR_FAILED,
					"Error creating image ");
		}
		
	}

	@PreAuthorize("hasAuthority('WEVERIFY_MNG')")
	@RequestMapping(value = "/animated/history", params = "limit", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public CreateAnimatedGifHistory getCreateImageHistory(@RequestParam(defaultValue = "5") int limit,
			@AuthenticationPrincipal Jwt principal) {
		Logger.info("GET /animated/history - user_id: {}", principal.getClaimAsString("sub"));
		Pageable limitQuery = PageRequest.of(0, limit, Sort.by("id").descending());
		Page<Image> images = imageDbRepository.findAll(limitQuery);

		List<AnimatedGif> animatedGifs = new LinkedList<AnimatedGif>();
		for (Image image : images) {
			AnimatedGif animatedGif = new AnimatedGif(image.getMd5sum(), image.getCreationDate());
			animatedGifs.add(animatedGif);
		}
		CreateAnimatedGifHistory history = new CreateAnimatedGifHistory(animatedGifs);
		return history;
	}

	@ExceptionHandler({ AnimatedGifCreationException.class })
	public ResponseEntity<Envisu4ServiceError> handle(AnimatedGifCreationException ex) {
		return new ResponseEntity<Envisu4ServiceError>(ex.getIpolServiceError(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Create animated Gif
	 * @param createAnimatedGifRequest
	 * @return
	 * @throws Exception
	 */
	private byte[] createAnimatedGif(CreateAnimatedRequest createAnimatedGifRequest) throws Exception {
		String[] urls = createAnimatedGifRequest.getInputURLs();
		int delay = createAnimatedGifRequest.getDelay();

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			byte[] content = createAnimatedGif.convert(urls, output, delay, true);
			return content;
		} finally {
			output.close();
		}
	}
	
	/**
	 * Store produce media content
	 * @param content
	 * @param md5sum
	 * @param contentType  (gif or mp4)
	 */
	private boolean storeMediaContent(byte[] content, String md5sum) {
		boolean isStore = false;
		Optional<Image> storeImage = imageDbRepository.findByMd5sum(md5sum);
		if (storeImage.isEmpty()) {
			Image image = new Image();
			image.setContent(content);
			image.setMd5sum(md5sum);
			image.setCreationDate(Calendar.getInstance().getTime());			
			imageDbRepository.save(image);
			isStore = true;
		}
		return isStore;
	}
	
	private byte[] getStoreContent(String md5sum) {
		byte[]content = imageDbRepository.findByMd5sum(md5sum)
		.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)).getContent();
		return content;
	}
}

class MediaContent {

	byte[] content;
	String md5sum;

	public MediaContent(byte[] content, String md5sum) {
		this.content = content;
		this.md5sum = md5sum;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public String getMd5sum() {
		return md5sum;
	}

	public void setMd5sum(String md5sum) {
		this.md5sum = md5sum;
	}

}