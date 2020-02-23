package uk.ac.ebi.spot.gwas.template.validator.domain;

import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode
public class ValidationConfiguration implements Serializable {

    private static final long serialVersionUID = 5321563088751089244L;

    private String triggerRow;

    private String studyTagColumnName;

    private Map<String, CellValidation> columns;

    public ValidationConfiguration() {

    }

    public String getStudyTagColumnName() {
        return studyTagColumnName;
    }

    public void setStudyTagColumnName(String studyTagColumnName) {
        this.studyTagColumnName = studyTagColumnName;
    }

    public String getTriggerRow() {
        return triggerRow;
    }

    public void setTriggerRow(String triggerRow) {
        this.triggerRow = triggerRow;
    }

    public Map<String, CellValidation> getColumns() {
        return columns;
    }

    public void setColumns(Map<String, CellValidation> columns) {
        this.columns = columns;
    }
}
