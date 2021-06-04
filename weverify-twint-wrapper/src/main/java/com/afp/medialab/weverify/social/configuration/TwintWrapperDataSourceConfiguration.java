package com.afp.medialab.weverify.social.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.afp.medialab.weverify.social.dao.entity.CollectHistory;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {
		"com.afp.medialab.weverify.social.dao.repository" }, entityManagerFactoryRef = "twintWrapperEntityManagerFactory", transactionManagerRef = "twintWrapperTransactionManager")
public class TwintWrapperDataSourceConfiguration {

	@Bean
	@Primary
	@ConfigurationProperties("application.twint-wrapper.datasource")
	public DataSourceProperties twintWrapperDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	@Primary
	public DataSource twintWrapperDataSource() {
		return twintWrapperDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
	}

	@Primary
	@Bean(name = "twintWrapperEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean twintWrapperEntityManagerFactory(
			EntityManagerFactoryBuilder builder) {
		return builder.dataSource(twintWrapperDataSource()).packages(CollectHistory.class)
				.build();
	}

	@Primary
	@Bean
	public PlatformTransactionManager twintWrapperTransactionManager(
			final @Qualifier("twintWrapperEntityManagerFactory") LocalContainerEntityManagerFactoryBean twintWrapperEntityManagerFactory) {
		return new JpaTransactionManager(twintWrapperEntityManagerFactory.getObject());
	}
}
