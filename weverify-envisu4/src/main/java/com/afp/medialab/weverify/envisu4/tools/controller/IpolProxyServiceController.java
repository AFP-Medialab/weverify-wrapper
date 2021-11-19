package com.afp.medialab.weverify.envisu4.tools.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.afp.medialab.weverify.envisu4.tools.constraints.IpolResultEnum;
import com.afp.medialab.weverify.envisu4.tools.controller.exception.Envisu4ServiceError;
import com.afp.medialab.weverify.envisu4.tools.controller.exception.IpolProxyException;
import com.afp.medialab.weverify.envisu4.tools.controller.exception.ServiceErrorCode;
import com.afp.medialab.weverify.envisu4.tools.images.FileUtils;
import com.afp.medialab.weverify.envisu4.tools.models.ErrorCode;
import com.afp.medialab.weverify.envisu4.tools.models.IpolHomographicResponse;
import com.afp.medialab.weverify.envisu4.tools.models.Results;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Validated
@Tag(name = "IPOL Service wrapper", description = "Operations start with /open are not authenticated. /ipol/homographic/result/* are also open")
public class IpolProxyServiceController {

	private static Logger Logger = LoggerFactory.getLogger(IpolProxyServiceController.class);

	@Value("${application.envisu4.ipol.endpoint}")
	private String ipolEndpoint;

	@Value("${application.envisu4.ipol.demo_id}")
	private String demoId;

	private static String HOMOGRAPHIC_ENDPOINT = "/api/core/run";
	private static String RESULT_ENDPOINT = "/api/core/shared_folder/run/%s/";
	private static String CLIENT_DATA = "{\"demo_id\": %s, \"origin\": \"upload\", \"params\": {}}";

	private String clientData;
	private String resultEndpoint;

	@Autowired
	@Qualifier("communRestTemplate")
	private RestTemplate restTemplate;

	@Autowired
	private FileUtils fileUtils;

	@PostConstruct
	private void buildClientData() {
		this.clientData = String.format(CLIENT_DATA, this.demoId);
		this.resultEndpoint = String.format(RESULT_ENDPOINT, this.demoId);
	}

	/**
	 * Call ipol service from files
	 * 
	 * @param file_0
	 * @param file_1
	 * @return
	 */
	@Operation(summary = "homographic with files", description = "Call IPOL homographic service with uploaded files")
	@RequestMapping(path = {
			"/ipol/homographic" }, method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<IpolHomographicResponse> homographicProcessCall(@RequestParam("file_0") MultipartFile file_0,
			@RequestParam("file_1") MultipartFile file_1, @AuthenticationPrincipal Jwt principal) {
		Logger.info("POST /ipol/homographic - user_id: {}", principal.getClaimAsString("sub"));
		FileSystemResource fileSysRs0 = new FileSystemResource(fileUtils.convert(file_0));
		FileSystemResource fileSysRs1 = new FileSystemResource(fileUtils.convert(file_1));
		return callAndBuildIpolService(fileSysRs0, fileSysRs1);
	}

	/**
	 * Call ipol service from URL
	 * 
	 * @param url_0
	 * @param url_1
	 * @return
	 * @throws IpolProxyException
	 */

	@Operation(summary = "homographic with urls", description = "Call IPOL homographic service with image URL")
	@RequestMapping(path = {
			"/ipol/homographic/url" }, method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<IpolHomographicResponse> homographicProcessCall(@RequestParam("url_0") String url_0,
			@RequestParam("url_1") String url_1, @AuthenticationPrincipal Jwt principal) throws IpolProxyException {
		Logger.info("POST /ipol/homographic/url - user_id: {}", principal.getClaimAsString("sub"));
		Logger.info("fake image: {}", url_0);
		Logger.info("original image {}", url_1);
		try {
			URL url0 = new URL(url_0);
			URL url1 = new URL(url_1);
			FileSystemResource fileSysRs0 = new FileSystemResource(fileUtils.convert(url0));
			FileSystemResource fileSysRs1 = new FileSystemResource(fileUtils.convert(url1));
			return callAndBuildIpolService(fileSysRs0, fileSysRs1);
		} catch (MalformedURLException e) {
			Logger.error("Bad url format", e);
			throw new IpolProxyException(ServiceErrorCode.IPOL_BAD_URL_ERROR, e.getMessage());
		} catch (IOException e) {
			Logger.error("Error download image from url", e);
			throw new IpolProxyException(ServiceErrorCode.IPOL_FILE_IMAGE_ERROR, e.getMessage());
		}
	}

	@Operation(summary = "Ipol homographic result image", description = "Get images results from IPOL service")
	@RequestMapping(path = IpolPathConstant.FULL_IPOL_RESULTS_PATH, method = RequestMethod.GET, produces = {
			MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.TEXT_PLAIN_VALUE })
	public ResponseEntity<Resource> downloadHomographicResults(@PathVariable String key,
			@PathVariable IpolResultEnum image) throws IpolProxyException {
		Logger.info("GET /ipol/homographic/result/{}/{}", key, image.getCode());
		try {
			ResponseEntity<Resource> response = restTemplate
					.getForEntity(ipolEndpoint + this.resultEndpoint + key + "/" + image.getCode(), Resource.class);
			return response;
		} catch (HttpClientErrorException e) {

			switch (e.getRawStatusCode()) {
			case 404:
				Logger.error("resource not found found in IPOL Server: {}/{}", key, image.getCode());
				throw new IpolProxyException(ServiceErrorCode.IPOL_RESOURCE_NOT_FOUND,
						"Not resource found in IPOL Server with key " + key);
			default:
				Logger.error("Error contacting IPOL service", e);
				throw new IpolProxyException(ServiceErrorCode.IPOL_REMOTE_SERVICE_ERROR, e.getMessage());

			}

		}
	}

	/**
	 * Call ipol service with static clientData value
	 * 
	 * @param fileSysRs0
	 * @param fileSysRs1
	 * @return
	 */
	private ResponseEntity<IpolHomographicResponse> callAndBuildIpolService(FileSystemResource fileSysRs0,
			FileSystemResource fileSysRs1) {
		return callAndBuildIpolService(fileSysRs0, fileSysRs1, this.clientData);
	}

	/**
	 * Call ipol service with temporary images
	 * 
	 * @param fileSysRs0
	 * @param fileSysRs1
	 * @param clientData
	 * @return
	 */
	private ResponseEntity<IpolHomographicResponse> callAndBuildIpolService(FileSystemResource fileSysRs0,
			FileSystemResource fileSysRs1, String clientData) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("file_0", fileSysRs0);
		body.add("file_1", fileSysRs1);
		body.add("clientData", clientData);
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
		ResponseEntity<IpolHomographicResponse> response = restTemplate
				.postForEntity(ipolEndpoint + HOMOGRAPHIC_ENDPOINT, requestEntity, IpolHomographicResponse.class);

		IpolHomographicResponse customResponse = buildResponse(response);
		ResponseEntity<IpolHomographicResponse> responseEntity = new ResponseEntity<IpolHomographicResponse>(
				customResponse, response.getStatusCode());
		return responseEntity;
	}

	/**
	 * Add results path to response
	 * 
	 * @param response
	 * @return
	 */
	private IpolHomographicResponse buildResponse(ResponseEntity<IpolHomographicResponse> response) {
		IpolHomographicResponse ipolResponse = response.getBody();
		if (ipolResponse.getStatus().equals("OK")) {
			Results results = new Results();
			ipolResponse.setResults(results);
			String output0 = IpolPathConstant.IPOL_RESULTS_PATH + ipolResponse.getKey() + "/output_0.jpg";
			String output1 = IpolPathConstant.IPOL_RESULTS_PATH + ipolResponse.getKey() + "/output_1.jpg";
			String png0 = IpolPathConstant.IPOL_RESULTS_PATH + ipolResponse.getKey() + "/output_0.png";
			String png1 = IpolPathConstant.IPOL_RESULTS_PATH + ipolResponse.getKey() + "/output_1.png";
			String pano = IpolPathConstant.IPOL_RESULTS_PATH + ipolResponse.getKey() + "/pano.jpg";
			String stdout = IpolPathConstant.IPOL_RESULTS_PATH + ipolResponse.getKey() + "/stdout.txt";
			ipolResponse.getResults().setOutput0(output0);
			ipolResponse.getResults().setOutput1(output1);
			ipolResponse.getResults().setPng0(png0);
			ipolResponse.getResults().setPng1(png1);
			ipolResponse.getResults().setPano(pano);
			ipolResponse.getResults().setStdout(stdout);
		} else {
			// Status is something else
			String errorMessage = ipolResponse.getError();
			if (errorMessage.startsWith("No matches"))
				ipolResponse.setErrorCode(ErrorCode.NO_MATCHES_FOUND);
			else {
				ipolResponse.setErrorCode(ErrorCode.IPOL_GENERIC_ERROR);
			}

		}
		return ipolResponse;
	}

	@ExceptionHandler({ IpolProxyException.class })
	public ResponseEntity<Envisu4ServiceError> handle(IpolProxyException ex) {
		Envisu4ServiceError ipolServiceError = ex.getIpolServiceError();
		HttpStatus status;
		if (ipolServiceError.getErrorCode().equals(ServiceErrorCode.IPOL_RESOURCE_NOT_FOUND))
			status = HttpStatus.NOT_FOUND;
		else
			status = HttpStatus.INTERNAL_SERVER_ERROR;

		return new ResponseEntity<Envisu4ServiceError>(ipolServiceError, status);
	}
}
