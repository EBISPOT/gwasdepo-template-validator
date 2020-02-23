package uk.ac.ebi.spot.gwas.template.validator.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ac.ebi.spot.gwas.deposition.dto.templateschema.TemplateSchemaDto;
import uk.ac.ebi.spot.gwas.template.validator.component.parser.CellValidationParserAdapterFactory;
import uk.ac.ebi.spot.gwas.template.validator.component.validator.TemplateValidatorAdapterFactory;

import java.io.IOException;

@Configuration
public class ValidatorConfig {

    private static final Logger log = LoggerFactory.getLogger(ValidatorConfig.class);

    @Bean
    public ServiceLocatorFactoryBean templateValidatorAdapterFactoryBean() {
        ServiceLocatorFactoryBean bean = new ServiceLocatorFactoryBean();
        bean.setServiceLocatorInterface(TemplateValidatorAdapterFactory.class);
        return bean;
    }

    @Bean
    public ServiceLocatorFactoryBean cellValidationParserAdapterFactoryBean() {
        ServiceLocatorFactoryBean bean = new ServiceLocatorFactoryBean();
        bean.setServiceLocatorInterface(CellValidationParserAdapterFactory.class);
        return bean;
    }

    @Bean
    public TemplateValidatorAdapterFactory templateValidatorAdapterFactory() {
        return (TemplateValidatorAdapterFactory) templateValidatorAdapterFactoryBean().getObject();
    }

    @Bean
    public CellValidationParserAdapterFactory cellValidationParserAdapterFactory() {
        return (CellValidationParserAdapterFactory) cellValidationParserAdapterFactoryBean().getObject();
    }

    @Value("${validator.ss-content-validation.enabled:false}")
    private boolean ssContentValidationEnabled;

    @Value("${validator.ss-content-validation.sheet:study}")
    private String ssContentValidationSheet;

    @Value("${validator.ss-content-validation.column:study_accession}")
    private String ssContentValidationColumn;

    @Value("${validator.default-schema.version:1.0}")
    private String defaultSchemaVersion;

    @Value("${validator.default-schema.file:schema_v1.json}")
    private String defaultSchemaFile;

    public String getDefaultSchemaVersion() {
        return defaultSchemaVersion;
    }

    public String getDefaultSchemaFile() {
        return defaultSchemaFile;
    }

    public boolean isSsContentValidationEnabled() {
        return ssContentValidationEnabled;
    }

    public String getSsContentValidationSheet() {
        return ssContentValidationSheet;
    }

    public String getSsContentValidationColumn() {
        return ssContentValidationColumn;
    }

    public TemplateSchemaDto readDefaultSchema() throws IOException {
        log.info("Reading default schema from: {}", defaultSchemaFile);
        String content = IOUtils.toString(getClass().getClassLoader().getResourceAsStream(defaultSchemaFile), "UTF-8");
        TemplateSchemaDto templateSchemaDto = new ObjectMapper().readValue(content, TemplateSchemaDto.class);
        log.info("Schema successfully processed.");
        return templateSchemaDto;
    }
}
