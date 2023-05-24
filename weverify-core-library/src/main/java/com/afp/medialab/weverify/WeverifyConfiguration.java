package com.afp.medialab.weverify;

import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.ClientConfiguration.MaybeSecureClientConfigurationBuilder;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import co.elastic.clients.elasticsearch.ElasticsearchClient;

@Configuration
public class WeverifyConfiguration {

	private static Logger Logger = LoggerFactory.getLogger(WeverifyConfiguration.class);

	@Value("${application.elasticsearch.host}")
	private String esHost;

	@Value("${application.elasticsearch.port}")
	private String esPort;

	@Value("${application.elasticsearch.user}")
	private String esUser;

	@Value("${application.elasticsearch.password}")
	private String esPassword;

	@Value("${application.elasticsearch.authentication}")
	private boolean isAuthenticate;

	@Value("${application.elasticsearch.secure}")
	private boolean isSecure;

	@Bean
	public RestHighLevelClient elasticsearchClient() {

		Logger.info("host: {}, port: {}", esHost, esPort);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Host", esHost);
		httpHeaders.add("Accept", "application/vnd.elasticsearch+json;compatible-with=7");
		httpHeaders.add("Content-Type", "application/vnd.elasticsearch+json;" + "compatible-with=7");
		String esURL = esHost + ":" + esPort;
		final ClientConfiguration.MaybeSecureClientConfigurationBuilder baseBuilder =
						(MaybeSecureClientConfigurationBuilder) ClientConfiguration.builder().connectedTo(esURL).withDefaultHeaders(httpHeaders);
		final ClientConfiguration.TerminalClientConfigurationBuilder terminalBuilder ;
		if(isSecure)
			terminalBuilder = baseBuilder.usingSsl();
		else
			terminalBuilder = baseBuilder;

		final ClientConfiguration clientConfiguration;
		if (isAuthenticate)
			clientConfiguration = terminalBuilder.withBasicAuth(esUser, esPassword).build();
		else
			clientConfiguration = terminalBuilder.build();

		return RestClients.create(clientConfiguration).rest();
	}

	@Bean(name = "communRestTemplate")
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

}
