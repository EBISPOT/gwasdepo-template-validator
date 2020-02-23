package uk.ac.ebi.spot.gwas.template.validator.util;

import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.spot.gwas.template.validator.util.SubmissionTemplateReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;

public class StreamSubmissionTemplateReader implements SubmissionTemplateReader {

    private static final Logger log = LoggerFactory.getLogger(SubmissionTemplateReader.class);

    private final static String META_SHEET = "meta";

    private final static String SCHEMA_VERSION = "schemaVersion";

    private final static String SUBMISSION_TYPE = "submissionType";

    private final static String HEADER_SIZE = "headerSize";

    private Workbook importedWorkbook;

    private boolean valid;

    private String schemaVersion;

    private String submissionType;

    private int headerSize;

    private byte[] fileContent;

    public StreamSubmissionTemplateReader(byte[] fileContent, String fileUploadId) {
        valid = true;
        headerSize = 0;
        try {
            log.info("[{}] Reading template file ...", fileUploadId);
            this.fileContent = fileContent;
            initialize();
            log.info("[{} - {}] Template file successfully read ...");
        } catch (Exception e) {
            log.error("[{}] Unable to read submission document: {}", fileContent, e.getMessage(), e);
            valid = false;
        }
    }

    private void initialize() {
        importedWorkbook = StreamingReader.builder()
                .rowCacheSize(100)
                .bufferSize(4096)
                .open(new ByteArrayInputStream(fileContent));
        log.info("Looking for the META sheet ...");
        try {
            Sheet sheet = importedWorkbook.getSheet(META_SHEET);
            if (sheet != null) {
                Iterator<Row> rowIterator = sheet.rowIterator();
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    Cell cell = row.getCell(0);
                    if (cell.getStringCellValue() != null) {
                        if (cell.getStringCellValue().trim().equalsIgnoreCase(SCHEMA_VERSION)) {
                            schemaVersion = row.getCell(1).getStringCellValue().trim();
                            log.info("Found schema version: {}", schemaVersion);
                        }
                        if (cell.getStringCellValue().trim().equalsIgnoreCase(SUBMISSION_TYPE)) {
                            submissionType = row.getCell(1).getStringCellValue().trim();
                            log.info("Found submission type: {}", submissionType);
                        }
                        if (cell.getStringCellValue().trim().equalsIgnoreCase(HEADER_SIZE)) {
                            if (row.getCell(1).getStringCellValue() != null) {
                                String sHeaderSize = row.getCell(1).getStringCellValue();
                                try {
                                    headerSize = Integer.parseInt(sHeaderSize);
                                } catch (Exception e) {
                                }
                            } else {
                                Double numericValue = cell.getNumericCellValue();
                                if (numericValue != null) {
                                    headerSize = numericValue.intValue();
                                }
                            }
                            log.info("Found header size: {}", headerSize);
                        }
                    }
                }
            }
            this.reinitialize();
        } catch (Exception e) {
            log.error("Unable to read metadata: {}", e.getMessage(), e);
            this.valid = false;
            this.close();
        }
    }

    public String getSchemaVersion() {
        return schemaVersion;
    }

    public String getSubmissionType() {
        return submissionType;
    }

    public int getHeaderSize() {
        return headerSize;
    }

    @Override
    public Iterator<Sheet> sheets() {
        log.info("Returning sheets ...");
        return importedWorkbook.sheetIterator();
    }

    @Override
    public void reinitialize() {
        try {
            importedWorkbook.close();
            importedWorkbook = StreamingReader.builder()
                    .rowCacheSize(100)
                    .bufferSize(4096)
                    .open(new ByteArrayInputStream(fileContent));
        } catch (IOException e) {
            log.error("Unable to close submission document: " + e.getMessage());
        }
    }

    @Override
    public void close() {
        try {
            importedWorkbook.close();
        } catch (IOException e) {
            log.error("Unable to close submission document: " + e.getMessage());
        }
    }

    @Override
    public boolean isValid() {
        return valid;
    }
}
