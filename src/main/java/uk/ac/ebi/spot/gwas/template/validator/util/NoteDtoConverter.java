package uk.ac.ebi.spot.gwas.template.validator.util;

import uk.ac.ebi.spot.gwas.deposition.dto.NoteDto;
import uk.ac.ebi.spot.gwas.template.validator.domain.Note;

public class NoteDtoConverter {

    public static NoteDto convert(Note note) {
        return new NoteDto(note.getStudy_tag(),
                note.getNote(),
                note.getNote_subject(),
                note.getStatus());
    }

}
