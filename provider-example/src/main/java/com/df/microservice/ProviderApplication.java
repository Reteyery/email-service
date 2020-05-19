package com.df.microservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 *
 */
// @SpringBootApplication(scanBasePackages = "com.df.microservice", exclude = DataSourceAutoConfiguration.class)
@SpringBootApplication(scanBasePackages = "com.df.microservice")
@MapperScan("com.dfe.*.dao")
@EnableDiscoveryClient
public class ProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProviderApplication.class, args);
    }

}
