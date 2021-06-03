package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CombinationQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Languages;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CombinationStatementQuestionDetailsDto extends StatementQuestionDetailsDto {
    private Languages language;
    private List<CombinationGroupStatementQuestionDetailsDto> combinationGroup;

    public CombinationStatementQuestionDetailsDto(CombinationQuestion question) {
        this.language = question.getLanguage();
        this.combinationGroup = question.getCombinationGroup().stream().map(CombinationGroupStatementQuestionDetailsDto::new).collect(Collectors.toList());
        Collections.shuffle(this.combinationGroup);
    }

    public Languages getLanguage() {
        return language;
    }

    public void setLanguage(Languages language) {
        this.language = language;
    }
 
    public List<CombinationGroupStatementQuestionDetailsDto> getCombinationGroup() {
        return combinationGroup;
    }

    public void setCombinationGroup(List<CombinationGroupStatementQuestionDetailsDto> combinationGroup) {
        this.combinationGroup = combinationGroup;
    }

    @Override
    public String toString() {
        return "CombinationStatementQuestionDetailsDto{" +
                "language='" + language + '\'' +
                ", combinationGroups=" + combinationGroup+
                '}';
    }
}