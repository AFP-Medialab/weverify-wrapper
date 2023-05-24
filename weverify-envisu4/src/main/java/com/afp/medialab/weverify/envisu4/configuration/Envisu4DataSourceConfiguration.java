package com.afp.medialab.weverify.envisu4.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.afp.medialab.weverify.envisu4.dao.entities.Image;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {
		"com.afp.medialab.weverify.envisu4.dao.repository" }, entityManagerFactoryRef = "envisu4EntityManagerFactory", transactionManagerRef = "envisu4TransactionManager")
public class Envisu4DataSourceConfiguration {

	@Bean
	@ConfigurationProperties("application.envisu4.datasource")
	public DataSourceProperties envisu4DataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	//@ConfigurationProperties("application.envisu4.datasource.configuration")
	public DataSource envisu4DataSource() {
		return envisu4DataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
	}

	@Bean(name = "envisu4EntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean envisu4EntityManagerFactory(EntityManagerFactoryBuilder builder) {
		return builder.dataSource(envisu4DataSource()).packages(Image.class).build();
	}

	@Bean
	public PlatformTransactionManager envisu4TransactionManager(
			final @Qualifier("envisu4EntityManagerFactory") LocalContainerEntityManagerFactoryBean envisu4EntityManagerFactory) {
		return new JpaTransactionManager(envisu4EntityManagerFactory.getObject());
	}

}
