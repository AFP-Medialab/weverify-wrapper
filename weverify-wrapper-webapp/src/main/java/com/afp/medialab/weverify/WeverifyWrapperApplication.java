package com.afp.medialab.weverify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication()
@ComponentScan(basePackages = {"com.afp.medialab.weverify"})
public class WeverifyWrapperApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(WeverifyWrapperApplication.class, args);
	}

}
