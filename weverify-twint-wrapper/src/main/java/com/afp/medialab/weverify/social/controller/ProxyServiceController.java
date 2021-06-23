package com.afp.medialab.weverify.social.controller;

import javax.annotation.PostConstruct;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ProxyServiceController {

	@Value("${application.elasticsearch.host}")
	private String esHost;

	@Value("${application.elasticsearch.port}")
	private String esPort;

	private String esURL;

	@PostConstruct
	private void init() {

		this.esURL = (esHost.startsWith("http") ? esHost : "http://" + esHost) + ":" + esPort;
	}

	@Autowired
	@Qualifier("communRestTemplate")
	private RestTemplate restTemplate;

	@RequestMapping(path = "/api/search/getTweets", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<JSONObject> elasticCallTweets(@RequestBody JSONObject jsonObject) {

		return restTemplate.postForEntity(esURL + "/tsnatweets/_search", jsonObject, JSONObject.class);
	}

	@RequestMapping(path = "/api/search/getUsers", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Resource> elasticCallUsers(@RequestBody Resource jsonObject) {
		return restTemplate.exchange(esURL + "/tsnausers/_search", HttpMethod.POST, new HttpEntity<Resource>(jsonObject),
				Resource.class);
		 //restTemplate.postForEntity(esURL + "/tsnausers/_search", jsonObject, JSONObject.class);
	}

}
