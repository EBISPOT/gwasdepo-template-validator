package uk.ac.ebi.spot.gwas.template.validator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.spot.gwas.deposition.dto.templateschema.TemplateSchemaDto;
import uk.ac.ebi.spot.gwas.template.validator.domain.SubmissionDocument;
import uk.ac.ebi.spot.gwas.template.validator.service.TemplateConverterService;
import uk.ac.ebi.spot.gwas.template.validator.util.StreamSubmissionTemplateReader;
import uk.ac.ebi.spot.gwas.template.validator.util.SubmissionTemplateReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class TemplateConverterServiceTest extends IntegrationTest {

    private final static String VALID_FILE = "valid.xlsx";

    private final static String VALID_WITH_SPACE_FILE = "valid_wspace.xlsx";

    private final static String SCHEMA_FILE = "schema_v1.json";

    @Autowired
    private TemplateConverterService templateConverterService;

    private TemplateSchemaDto templateSchemaDto;

    private SubmissionTemplateReader submissionTemplateReader;

    @Before
    public void setup() throws IOException {
        File file = new File(getClass().getClassLoader().getResource(SCHEMA_FILE).getFile());
        String content = FileUtils.readFileToString(file);
        templateSchemaDto = new ObjectMapper().readValue(content, TemplateSchemaDto.class);
    }

    @Test
    public void shouldValidateValidSubmission() throws Exception {
        File file = new File(getClass().getClassLoader().getResource(VALID_FILE).getFile());
        byte[] fileContent = IOUtils.toByteArray(new FileInputStream(file));
        submissionTemplateReader = new StreamSubmissionTemplateReader(fileContent, file.getName());

        SubmissionDocument submissionDocument = templateConverterService.convert(submissionTemplateReader, templateSchemaDto);
        assertEquals(2, submissionDocument.getStudyEntries().size());
        assertEquals(4, submissionDocument.getAssociationEntries().size());
        assertEquals(7, submissionDocument.getSampleEntries().size());
        assertEquals(0, submissionDocument.getNoteEntries().size());
        submissionTemplateReader.close();
    }

    @Test
    public void shouldValidateValidWithSpaceSubmission() throws Exception {
        File file = new File(getClass().getClassLoader().getResource(VALID_WITH_SPACE_FILE).getFile());
        byte[] fileContent = IOUtils.toByteArray(new FileInputStream(file));
        submissionTemplateReader = new StreamSubmissionTemplateReader(fileContent, file.getName());

        SubmissionDocument submissionDocument = templateConverterService.convert(submissionTemplateReader, templateSchemaDto);
        assertEquals(2, submissionDocument.getStudyEntries().size());
        assertEquals("ss_file_1", submissionDocument.getStudyEntries().get(0).getSummary_statistics_file());
        assertEquals("GRCh38", submissionDocument.getStudyEntries().get(0).getSummary_statistics_assembly());
        assertEquals("md511", submissionDocument.getStudyEntries().get(0).getChecksum());
        submissionTemplateReader.close();
    }
}