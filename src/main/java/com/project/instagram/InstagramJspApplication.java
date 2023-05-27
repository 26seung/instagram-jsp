package com.project.instagram;

import com.project.instagram.config.ProfileManager;
import com.project.instagram.config.env.EnvConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class InstagramJspApplication {

	@Autowired
	public void Application(EnvConfiguration envConfiguration, ProfileManager profileManager) {
		log.info(envConfiguration.getMessage());
//		envConfiguration.getMessage();
		profileManager.printActiveProfiles();
	}

	public static void main(String[] args) {
		SpringApplication.run(InstagramJspApplication.class, args);
	}

}
