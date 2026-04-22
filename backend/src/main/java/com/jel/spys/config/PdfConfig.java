package com.jel.spys.config;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import org.apache.fop.apps.FopFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@org.springframework.context.annotation.Configuration
@ComponentScan(basePackages = "com.jel.spys.service")
public class PdfConfig {

    @Bean
    public FopFactory fopFactory() throws Exception {
        ClassPathResource configResource = new ClassPathResource("fop.xconf");
        return FopFactory.newInstance(configResource.getURI(), configResource.getInputStream());
    }

    @Bean
    public Configuration freemarkerConfig() throws IOException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
        cfg.setClassForTemplateLoading(this.getClass(), "/templates");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setFallbackOnNullLoopVariable(false);
        return cfg;
    }
}
