package com.beadando.beadandoapi;

import com.beadando.beadandoapi.config.FileManagerConfig;
import com.beadando.beadandoapi.config.KeyCloakConfig;
import com.beadando.beadandoapi.service.FileManagerService;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({KeyCloakConfig.class, FileManagerConfig.class})
public class BeadandoApiApplication implements CommandLineRunner {
    @Resource
    FileManagerService managerService;

    public static void main(String[] args) {
        SpringApplication.run(BeadandoApiApplication.class, args);
    }

    @Override
    public void run(String... arg) throws Exception {
        managerService.initRoot();
    }
}
