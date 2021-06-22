package com.afp.medialab.weverify.envisu4.tools.controller;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import com.afp.medialab.weverify.envisu4.tools.images.ICreateAnimatedGif;
import com.afp.medialab.weverify.envisu4.tools.models.AnimatedGif;
import com.afp.medialab.weverify.envisu4.tools.models.CreateAnimatedGifHistory;
import com.afp.medialab.weverify.envisu4.tools.models.CreateAnimatedGifRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Validated
@Tag(name = "Animated GIF API.", description = "Operation starts with /open are not authenticated.")
public class AnimatedGifController {

	@Autowired
	private ImageDbRepository imageDbRepository;

	@Autowired
	@Qualifier("alphagGifWriter")
	private ICreateAnimatedGif createAnimatedGif;

	private static Logger Logger = LoggerFactory.getLogger(AnimatedGifController.class);

	@Operation(summary = "Create animated Gif", description = "Create an animated Gif from several image URLs")
	@RequestMapping(path = {
			"/animated" }, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.IMAGE_GIF_VALUE)
	public ResponseEntity<Resource> createAnimatedGifForURL(
			@RequestBody @Valid CreateAnimatedGifRequest createAnimatedGifRequest) throws Exception {

		Set<String> urls = createAnimatedGifRequest.getInputURLs();
		int delay = createAnimatedGifRequest.getDelay();

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			byte[] content = createAnimatedGif.convert(urls, output, delay, true);
			String md5sum = DigestUtils.md5Hex(content);
			Optional<Image> storeImage = imageDbRepository.findByMd5sum(md5sum);
			if (storeImage.isEmpty()) {
				Image image = new Image();
				image.setContent(content);
				image.setMd5sum(md5sum);
				image.setCreationDate(Calendar.getInstance().getTime());
				imageDbRepository.save(image);
			}
			MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
			headers.add("imageId", md5sum);
			ResponseEntity<Resource> response = new ResponseEntity<Resource>(new ByteArrayResource(content), headers,
					HttpStatus.OK);
			return response;
			// return new ByteArrayResource(content);

		} catch (IIOException e) {
			Logger.error("IIOException", e);
			throw new AnimatedGifCreationException(ServiceErrorCode.ANIMATED_GIF_URL_LOAD_FAILED,
					"Fail loading input resource");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Logger.error("General error", e);
			throw new AnimatedGifCreationException(ServiceErrorCode.ANIMATED_GIF_CREATION_FAILED,
					"Error creating image ");
		} finally {
			output.close();
		}
	}

	@Operation(summary = "get stored animated Gif", description = "Get animated Gif generated from database")
	@RequestMapping(path = { "/animated/{imageId}" }, method = RequestMethod.GET, produces = MediaType.IMAGE_GIF_VALUE)
	public Resource getImageFromId(@PathVariable String imageId) {
		byte[] content = imageDbRepository.findByMd5sum(imageId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)).getContent();
		return new ByteArrayResource(content);

	}

	@Operation(summary = "get create animated gif history")
	@RequestMapping(path = {
			"/animated/history" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public CreateAnimatedGifHistory getCreateImageHistory(
			@RequestParam(value = "limit", required = false, defaultValue = "5") int limit) {
		Pageable limitQuery = PageRequest.of(0, limit);
		Page<Image> images = imageDbRepository.findAll(limitQuery);
		// List<Image> images = imageDbRepository.findAll(limitQuery);
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
}
