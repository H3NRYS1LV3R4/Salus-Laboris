package com.saluslaboris.api.config;


import com.saluslaboris.api.service.BootstrapService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;

@Configuration
@EnableConfigurationProperties(BootstrapProperties.class)
public class BootstrapConfig {
    @Bean
    @ConditionalOnProperty(name = "app.bootstrap.enabled", havingValue = "true")
    ApplicationRunner bootstrapRunner(BootstrapService service, BootstrapProperties properties) {
        return args -> service.initialize(properties);
    }
}
