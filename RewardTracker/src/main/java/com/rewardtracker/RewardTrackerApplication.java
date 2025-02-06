package com.rewardtracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.orm.hibernate5.SpringSessionContext;
import org.springframework.security.core.SpringSecurityCoreVersion;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EntityScan(basePackages = "com.rewardtracker.model")
 // तुमच्या Entity package ची वाट दाखवा

public class RewardTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RewardTrackerApplication.class, args);
	}

}
