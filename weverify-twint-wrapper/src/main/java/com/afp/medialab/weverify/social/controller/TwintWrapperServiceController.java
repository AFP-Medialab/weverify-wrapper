package com.afp.medialab.weverify.social.controller;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.afp.medialab.weverify.social.dao.entity.CollectHistory;
import com.afp.medialab.weverify.social.dao.service.CollectService;
import com.afp.medialab.weverify.social.model.CollectRequest;
import com.afp.medialab.weverify.social.model.CollectResponse;
import com.afp.medialab.weverify.social.model.CollectUpdateRequest;
import com.afp.medialab.weverify.social.model.HistoryRequest;
import com.afp.medialab.weverify.social.model.HistoryResponse;
import com.afp.medialab.weverify.social.model.Status;
import com.afp.medialab.weverify.social.model.StatusRequest;
import com.afp.medialab.weverify.social.model.StatusResponse;
import com.afp.medialab.weverify.social.util.RequestCacheManager;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Twitter scraping API.", description = "Twitter scrapping API")
public class TwintWrapperServiceController {

	private static Logger Logger = LoggerFactory.getLogger(TwintWrapperServiceController.class);

	@Autowired
	private CollectService collectService;

	@Autowired
	private RequestCacheManager requestService;
	
	@Value("${application.twint-wrapper.home.msg}")
	private String homeMsg;

	@RequestMapping(path = "/", method = RequestMethod.GET)
	public @ResponseBody String home() {
		return homeMsg;
	}

	@Operation(summary = "Trigger a Twitter Scraping")
	@RequestMapping(path = "/collect", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody CollectResponse collect(@RequestBody @Valid CollectRequest collectRequest,
			BindingResult result, @AuthenticationPrincipal Jwt principal) {
		Logger.info("POST /collect - user_id: {} ", principal.getClaimAsString("sub") );
		Logger.debug(result.getAllErrors().toString());
		if (result.hasErrors()) {
			String str = "";
			for (ObjectError r : result.getAllErrors()) {
				str += r.getDefaultMessage() + "; ";
			}
			Logger.info(str);
			return new CollectResponse(null, Status.Error, str, null);
		}
		if (!collectRequest.isValid())
			return new CollectResponse(null, Status.Error,
					"from must be before until", null);

		Set<String> and_list = collectRequest.getKeywordList();
		Set<String> not_ist = collectRequest.getBannedWords();
		if (and_list != null)
			Logger.info("and_list : " + and_list.toString());
		if (not_ist != null)
			Logger.info("not_list : " + not_ist.toString());
		if (!collectRequest.isDisableTimeRange()) {
			Logger.info("from : {}", collectRequest.getFrom().toString());
			Logger.info("until : {}", collectRequest.getUntil().toString());
		}
		Logger.info("language : {}", collectRequest.getLang());
		Logger.info("user : {}", collectRequest.getUserList());
		Logger.info("verified : {}", collectRequest.isVerified());
		Logger.info("Retweets : {}", collectRequest.getRetweetsHandling());
		Logger.info("Media : {}", collectRequest.getMedia());
		Logger.info("disableTimeRange {}", collectRequest.isDisableTimeRange());

		return requestService.useCache(collectRequest);
	}

	@Operation(summary = "Trigger a status check")
	@RequestMapping(path = "/status", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody StatusResponse status(@RequestBody StatusRequest statusRequest) {
		Logger.debug("POST status " + statusRequest.getSession());
		return requestService.getStatusResponse(statusRequest.getSession());
	}

	@Operation(summary = "Trigger a status check")
	@RequestMapping(path = "/status/{id}", method = RequestMethod.GET)
	public @ResponseBody StatusResponse status(@PathVariable("id") String id) {
		Logger.debug("GET status " + id);
		return requestService.getStatusResponse(id);
	}


	@PreAuthorize("hasAuthority('WEVERIFY_MNG')")
	@RequestMapping(value = "/collect-history", method = RequestMethod.GET)
	public @ResponseBody HistoryResponse collectHistory(
			@RequestParam(value = "limit", required = false, defaultValue = "5") int limit,
			@RequestParam(value = "asc", required = false, defaultValue = "false") boolean asc,
			@RequestParam(value = "desc", required = false, defaultValue = "false") boolean desc,
			@RequestParam(value = "status", required = false) String status, 
			@AuthenticationPrincipal Jwt principal) {
		List<CollectHistory> last;

		Logger.info("GET /collect-history - user_id {}", principal.getClaimAsString("sub"));

		if (status == null) {
			if (!asc && !desc)
				last = collectService.getLasts(limit, true);
			else if (asc)
				last = collectService.getLasts(limit, !asc);
			else
				last = collectService.getLasts(limit, desc);
		} else {
			if (!asc && !desc)
				last = collectService.getByStatus(status, limit, true);
			else if (asc)
				last = collectService.getByStatus(status, limit, !asc);
			else
				last = collectService.getByStatus(status, limit, desc);

		}
		return new HistoryResponse(last);
	}


	@PreAuthorize("hasAuthority('WEVERIFY_MNG')")
	@RequestMapping(path = "/collect-history", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody HistoryResponse status(@RequestBody @Valid HistoryRequest historyRequest, @AuthenticationPrincipal Jwt principal) {
		Logger.info("POST /collect-history - user_id {}",principal.getClaimAsString("sub"));
		Logger.info("/collect history request: {}", historyRequest.toString());
		List<CollectHistory> collectHistoryList = collectService.getHistory(historyRequest.getLimit(),
				historyRequest.getStatus(),
				(historyRequest.getSort() == null ? false : historyRequest.getSort().equals("desc")),
				historyRequest.getProcessStart(), historyRequest.getProcessTo());
		return new HistoryResponse(collectHistoryList);
	}

	@Operation(summary = "Update an old request")
	@RequestMapping(path = "/collect-update/{id}", method = RequestMethod.GET)
	public @ResponseBody StatusResponse collectUpdate(@PathVariable("id") String id)
			throws ExecutionException, InterruptedException, IOException {
		return requestService.collectUpdateFunction(id);
	}

	@Operation(summary = "Update an old request")
	@RequestMapping(path = "/collect-update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody StatusResponse collectUpdate(@RequestBody @Valid CollectUpdateRequest collectUpdateRequest)
			throws ExecutionException, InterruptedException, IOException {
		String id = collectUpdateRequest.getSession();
		return requestService.collectUpdateFunction(id);
	}

	
}