package uk.ac.ebi.spot.gwas.template.validator.domain;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Note {

    private String study_tag;

    private String note;

    private String note_subject;

    private String status;

    public Note() {

    }

    public String getStudy_tag() {
        return study_tag;
    }

    public void setStudy_tag(String study_tag) {
        this.study_tag = study_tag;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNote_subject() {
        return note_subject;
    }

    public void setNote_subject(String note_subject) {
        this.note_subject = note_subject;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
