package com.example.blog.config;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.ServletRegistration;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[] { RootConfig.class };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[] { WebConfig.class };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }
    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        String location = System.getProperty("java.io.tmpdir"); // временная папка
        long maxFileSize = 10 * 1024 * 1024; // 10 MB
        long maxRequestSize = 20 * 1024 * 1024; // 20 MB
        int fileSizeThreshold = 0; // записывать сразу в файл

        MultipartConfigElement multipartConfigElement =
                new MultipartConfigElement(location, maxFileSize, maxRequestSize, fileSizeThreshold);

        registration.setMultipartConfig(multipartConfigElement);
    }
}
