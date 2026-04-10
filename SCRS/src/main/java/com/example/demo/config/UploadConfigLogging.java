/*
 * File: src/main/java/com/example/demo/config/UploadConfigLogging.java
 * Role: Config
 * MVC Fit: Spring configuration and bean wiring.
 * Connects To: Bootstraps app behavior
 */

package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Class Summary: Config class that defines Spring configuration and beans.
// @Configuration marks this class as a source of Spring bean definitions.
@Configuration
public class UploadConfigLogging {

// @Bean tells Spring to manage the returned object as a bean.
    @Bean
// Configuration method: defines how Spring should create/manage a bean.
    public CommandLineRunner uploadConfigRunner(
// @Value injects a property value from application.properties.
            @Value("${spring.servlet.multipart.max-file-size}") String maxFile,
// @Value injects a property value from application.properties.
            @Value("${spring.servlet.multipart.max-request-size}") String maxRequest,
// @Value injects a property value from application.properties.
            @Value("${server.tomcat.max-http-form-post-size}") String tomcatPost,
// @Value injects a property value from application.properties.
            @Value("${server.tomcat.max-swallow-size}") String tomcatSwallow,
// @Value injects a property value from application.properties.
            @Value("${server.tomcat.max-part-count}") String maxPartCount,
// @Value injects a property value from application.properties.
            @Value("${server.tomcat.max-parameter-count}") String maxParameterCount) {
        return args -> System.out.println("Upload limits => multipart.file=" + maxFile
                + ", multipart.request=" + maxRequest
                + ", tomcat.post=" + tomcatPost
                + ", tomcat.swallow=" + tomcatSwallow
                + ", tomcat.partCount=" + maxPartCount
                + ", tomcat.parameterCount=" + maxParameterCount);
    }
}
