package com.project.instagram.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ProfileManager {

    @Autowired
    private Environment environment;

    public void printActiveProfiles() {
        log.info("[printActiveProfiles] active Profiles size : {}", environment.getActiveProfiles().length);

        List<String> profiles = new ArrayList<>();
        for (String profile : environment.getActiveProfiles()) {
            profiles.add(profile);
        }
        log.info("[printActiveProfiles] profile : {}", profiles);
    }
}
