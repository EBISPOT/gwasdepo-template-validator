package uk.ac.ebi.spot.gwas.template.validator;

import org.apache.commons.io.IOUtils;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.spot.gwas.template.validator.util.StreamSubmissionTemplateReader;
import uk.ac.ebi.spot.gwas.template.validator.util.SubmissionTemplateReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ValidatorApplication.class},
        initializers = ConfigFileApplicationContextInitializer.class)
public abstract class IntegrationTest {

    protected SubmissionTemplateReader getForFile(String[] files, int index) throws IOException {
        String inputFile = files[index];
        File file = new File(getClass().getClassLoader().getResource(inputFile).getFile());
        byte[] fileContent = IOUtils.toByteArray(new FileInputStream(file));
        return new StreamSubmissionTemplateReader(fileContent, file.getName());
    }

}
