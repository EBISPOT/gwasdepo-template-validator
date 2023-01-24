package uk.ac.ebi.spot.gwas.template.validator.domain;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Sample {

    private String study_tag;

    private String stage;

    private Integer size;

    private Integer cases;

    private Integer controls;

    private String sample_description;

    private String ancestry_category;

    private String ancestry;

    private String ancestry_description;

    private String country_recruitement;

    private Boolean case_control_study;

    private String ancestry_method;

    public Sample() {

    }

    public String getStudy_tag() {
        return study_tag;
    }

    public void setStudy_tag(String study_tag) {
        this.study_tag = study_tag;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getCases() {
        return cases;
    }

    public void setCases(Integer cases) {
        this.cases = cases;
    }

    public Integer getControls() {
        return controls;
    }

    public void setControls(Integer controls) {
        this.controls = controls;
    }

    public String getSample_description() {
        return sample_description;
    }

    public void setSample_description(String sample_description) {
        this.sample_description = sample_description;
    }

    public String getAncestry_category() {
        return ancestry_category;
    }

    public void setAncestry_category(String ancestry_category) {
        this.ancestry_category = ancestry_category;
    }

    public String getAncestry() {
        return ancestry;
    }

    public void setAncestry(String ancestry) {
        this.ancestry = ancestry;
    }

    public String getAncestry_description() {
        return ancestry_description;
    }

    public void setAncestry_description(String ancestry_description) {
        this.ancestry_description = ancestry_description;
    }

    public String getCountry_recruitement() {
        return country_recruitement;
    }

    public void setCountry_recruitement(String country_recruitement) {
        this.country_recruitement = country_recruitement;
    }

    public Boolean getCase_control_study() {
        return case_control_study;
    }

    public void setCase_control_study(Boolean case_control_study) {
        this.case_control_study = case_control_study;
    }

    public String getAncestry_method() {
        return ancestry_method;
    }

    public void setAncestry_method(String ancestry_method) {
        this.ancestry_method = ancestry_method;
    }
}
