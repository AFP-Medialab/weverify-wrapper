package com.afp.medialab.weverify.social;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.afp.medialab.weverify.social.twint.ITwitieProcess;
import com.afp.medialab.weverify.social.twint.TwitieSnapshot;
import com.afp.medialab.weverify.social.twint.TwitieWithSpacy;

@Configuration
public class TwintWrapperConfiguration {

	@Bean
	@ConditionalOnProperty(name = "application.twint-wrapper.twitie.isSpacyVersion", havingValue = "false")
	public ITwitieProcess getTwitieLegacyRequestBuilder() {
		return new TwitieSnapshot();
	}

	@Bean
	@ConditionalOnProperty(name = "application.twint-wrapper.twitie.isSpacyVersion", havingValue = "true", matchIfMissing = true)
	public ITwitieProcess getTwitieSpacyRequestBuilder() {
		return new TwitieWithSpacy();
	}

}
