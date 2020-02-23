package uk.ac.ebi.spot.gwas.template.validator.util;

import uk.ac.ebi.spot.gwas.deposition.dto.*;
import uk.ac.ebi.spot.gwas.template.validator.domain.SubmissionDocument;

import java.util.List;
import java.util.stream.Collectors;

public class SubmissionConverter {

    public static SubmissionDataDto fromSubmissionDocument(SubmissionDocument submissionDocument) {
        List<StudyDto> studyDtoList = submissionDocument.getStudyEntries().stream()
                .map(StudyDtoCoverter::convert).collect(Collectors.toList());
        List<AssociationDto> associationDtos = submissionDocument.getAssociationEntries().stream()
                .map(AssociationDtoConverter::convert).collect(Collectors.toList());
        List<SampleDto> sampleDtos = submissionDocument.getSampleEntries().stream()
                .map(SampleDtoConverter::convert).collect(Collectors.toList());
        List<NoteDto> noteDtos = submissionDocument.getNoteEntries().stream()
                .map(NoteDtoConverter::convert).collect(Collectors.toList());

        return new SubmissionDataDto(studyDtoList, associationDtos, sampleDtos, noteDtos);
    }

}
