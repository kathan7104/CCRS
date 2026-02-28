package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UploadConfigLogging {

    @Bean
    public CommandLineRunner uploadConfigRunner(
            @Value("${spring.servlet.multipart.max-file-size}") String maxFile,
            @Value("${spring.servlet.multipart.max-request-size}") String maxRequest,
            @Value("${server.tomcat.max-http-form-post-size}") String tomcatPost,
            @Value("${server.tomcat.max-swallow-size}") String tomcatSwallow,
            @Value("${server.tomcat.max-part-count}") String maxPartCount,
            @Value("${server.tomcat.max-parameter-count}") String maxParameterCount) {
        return args -> System.out.println("Upload limits => multipart.file=" + maxFile
                + ", multipart.request=" + maxRequest
                + ", tomcat.post=" + tomcatPost
                + ", tomcat.swallow=" + tomcatSwallow
                + ", tomcat.partCount=" + maxPartCount
                + ", tomcat.parameterCount=" + maxParameterCount);
    }
}
