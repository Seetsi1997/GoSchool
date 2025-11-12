package com.example.GoSchool;

import com.example.GoSchool.config.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.GoSchool.repository")
@EnableConfigurationProperties(FileStorageProperties.class)
@EntityScan(basePackages = "com.example.GoSchool.model")
public class   GoSchoolApplication {

	public static void main(String[] args) {
		SpringApplication.run(GoSchoolApplication.class, args);
	}

}
