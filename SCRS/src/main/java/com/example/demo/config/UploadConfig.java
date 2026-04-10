/*
 * File: src/main/java/com/example/demo/config/UploadConfig.java
 * Role: Config
 * MVC Fit: Spring configuration and bean wiring.
 * Connects To: Bootstraps app behavior
 */

package com.example.demo.config;

import jakarta.servlet.MultipartConfigElement;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.servlet.MultipartConfigFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

// Class Summary: Config class that defines Spring configuration and beans.
// @Configuration marks this class as a source of Spring bean definitions.
@Configuration
public class UploadConfig {

// Field: stores UNLIMITED for this class.
    private static final int UNLIMITED = -1;

// @Bean tells Spring to manage the returned object as a bean.
    @Bean
// Configuration method: defines how Spring should create/manage a bean.
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofBytes(UNLIMITED));
        factory.setMaxRequestSize(DataSize.ofBytes(UNLIMITED));
        return factory.createMultipartConfig();
    }

// @Bean tells Spring to manage the returned object as a bean.
    @Bean
// Configuration method: defines how Spring should create/manage a bean.
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatUploadCustomizer() {
        return factory -> factory.addConnectorCustomizers(connector -> {
            connector.setMaxPostSize(UNLIMITED);
            connector.setMaxSavePostSize(UNLIMITED);
            if (connector.getProtocolHandler() instanceof AbstractHttp11Protocol<?> protocol) {
                protocol.setMaxSwallowSize(UNLIMITED);
            }
        });
    }
}
