package com.jel.spys.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

@Service
@Slf4j
public class PdfGenerationService {

    @Autowired
    private FopFactory fopFactory;

    @Autowired
    private Configuration freemarkerConfig;

    public byte[] generatePdf(String templateName, Map<String, Object> data) throws Exception {
        log.info("Generating PDF from template: {}", templateName);
        
        try {
            // Process the Freemarker template
            Template template = freemarkerConfig.getTemplate(templateName);
            StringWriter stringWriter = new StringWriter();
            template.process(data, stringWriter);
            String foContent = stringWriter.toString();
            
            log.debug("Generated FO content length: {}", foContent.length());
            
            // Generate PDF using Apache FOP
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, outputStream);
            
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            
            Source src = new StreamSource(new StringReader(foContent));
            Result res = new SAXResult(fop.getDefaultHandler());
            
            transformer.transform(src, res);
            
            byte[] pdfBytes = outputStream.toByteArray();
            log.info("Successfully generated PDF with size: {} bytes", pdfBytes.length);
            
            return pdfBytes;
            
        } catch (IOException | TemplateException e) {
            log.error("Error processing template: {}", templateName, e);
            throw new RuntimeException("Failed to process template: " + templateName, e);
        } catch (Exception e) {
            log.error("Error generating PDF from template: {}", templateName, e);
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }
}
