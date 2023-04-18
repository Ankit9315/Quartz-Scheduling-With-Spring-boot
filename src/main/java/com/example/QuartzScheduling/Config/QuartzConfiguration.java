package com.example.QuartzScheduling.Config;

import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableScheduling
public class QuartzConfiguration {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private PlatformTransactionManager transactionManager;

	@Autowired
	private ApplicationContext applicationContext;
	
	
	public Properties quartzProperties() throws IOException {
		PropertiesFactoryBean properties = new PropertiesFactoryBean();
		properties.setLocations(new ClassPathResource("/application.properties"));
		properties.afterPropertiesSet();
		return properties.getObject();
		
	}
	
	@Bean
	public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
		SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
		schedulerFactory.setDataSource(dataSource);
		schedulerFactory.setTransactionManager(transactionManager);
		schedulerFactory.setOverwriteExistingJobs(true);
		schedulerFactory.setSchedulerName("my first schedule");
		
		AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
		jobFactory.setApplicationContext(applicationContext);
		
		schedulerFactory.setQuartzProperties(quartzProperties());
		schedulerFactory.setWaitForJobsToCompleteOnShutdown(true);
		schedulerFactory.setAutoStartup(true);
		schedulerFactory.setJobFactory(jobFactory);
		
		return schedulerFactory;
		
	}
}
