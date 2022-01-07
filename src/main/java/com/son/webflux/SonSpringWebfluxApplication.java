package com.son.webflux;

import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@SpringBootApplication
public class SonSpringWebfluxApplication {

	@Value("${spring.datasource.maximum-pool-size}")
	private int connectionPools;

	public static void main(String[] args) {
		SpringApplication.run(SonSpringWebfluxApplication.class, args);
	}

	@Bean
	public Scheduler jdbcScheduler(){
		return Schedulers.fromExecutor(Executors.newFixedThreadPool(connectionPools));
	}

	@Bean
	public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager){
		return new TransactionTemplate(transactionManager);
	}

}
