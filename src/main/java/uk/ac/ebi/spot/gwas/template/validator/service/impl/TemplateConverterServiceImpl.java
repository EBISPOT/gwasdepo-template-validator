package uk.ac.ebi.spot.gwas.template.validator.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.deposition.dto.templateschema.TemplateSchemaDto;
import uk.ac.ebi.spot.gwas.deposition.dto.templateschema.TemplateSheetDto;
import uk.ac.ebi.spot.gwas.template.validator.component.parser.CellValidationParser;
import uk.ac.ebi.spot.gwas.template.validator.component.parser.CellValidationParserAdapterFactory;
import uk.ac.ebi.spot.gwas.template.validator.component.parser.SheetMapper;
import uk.ac.ebi.spot.gwas.template.validator.component.validator.TemplateValidator;
import uk.ac.ebi.spot.gwas.template.validator.component.validator.TemplateValidatorAdapterFactory;
import uk.ac.ebi.spot.gwas.template.validator.config.ValidatorConstants;
import uk.ac.ebi.spot.gwas.template.validator.domain.SubmissionDocument;
import uk.ac.ebi.spot.gwas.template.validator.domain.ValidationConfiguration;
import uk.ac.ebi.spot.gwas.template.validator.service.TemplateConverterService;
import uk.ac.ebi.spot.gwas.template.validator.util.SchemaFromName;
import uk.ac.ebi.spot.gwas.template.validator.util.SubmissionTemplateReader;

import java.util.Iterator;

@Service
public class TemplateConverterServiceImpl implements TemplateConverterService {

    private static final Logger log = LoggerFactory.getLogger(TemplateConverterService.class);

    @Autowired
    private TemplateValidatorAdapterFactory templateValidatorAdapterFactory;

    @Autowired
    private CellValidationParserAdapterFactory cellValidationParserAdapterFactory;

    @Override
    public SubmissionDocument convert(SubmissionTemplateReader submissionTemplateReader, TemplateSchemaDto templateSchemaDto) {
        SubmissionDocument submissionDocument = new SubmissionDocument();
        if (submissionTemplateReader.isValid()) {
            submissionTemplateReader.reinitialize();
            Iterator<Sheet> sheets = submissionTemplateReader.sheets();
            while (sheets.hasNext()) {
                Sheet sheet = sheets.next();
                try {
                    convertSheet(sheet, sheet.getSheetName(), SchemaFromName.fromName(sheet.getSheetName(), templateSchemaDto), submissionDocument);
                } catch (Exception e) {
                    log.error("Unable to convert sheet [{}]: {}", sheet.getSheetName(), e.getMessage(), e);
                }
            }
        }

        return submissionDocument;
    }

    private void convertSheet(Sheet sheet, String validatorComponent, TemplateSheetDto templateSheetDto, SubmissionDocument submissionDocument) throws JsonProcessingException {
        log.info("Converting sheet: {}", sheet.getSheetName());

        TemplateValidator templateValidator = null;
        try {
            templateValidator = templateValidatorAdapterFactory.getAdapter(validatorComponent);
        } catch (Exception e) {
            log.error("Unable to find adapter for component: {}", validatorComponent);
        }
        if (templateValidator != null) {
            CellValidationParser cellValidationParser = cellValidationParserAdapterFactory.getAdapter(ValidatorConstants.SCHEMA);
            ValidationConfiguration validationConfiguration = cellValidationParser.parseCellValidations(new ObjectMapper().writeValueAsString(templateSheetDto));
            SheetMapper sheetMapper = new SheetMapper(sheet, validationConfiguration.getColumns());
            log.info("Sheet mapper configuration: {}", sheetMapper.getColumnIndex());
            templateValidator.convertSheet(sheet, validationConfiguration, sheetMapper.getColumnIndex(), submissionDocument);
        }
    }
}
