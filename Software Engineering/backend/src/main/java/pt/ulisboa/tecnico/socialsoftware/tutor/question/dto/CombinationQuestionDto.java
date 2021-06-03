package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CombinationQuestionDto extends QuestionDetailsDto {
    private Languages language;

    private List<CombinationGroupDto> combinationGroup = new ArrayList<>();
    

    public CombinationQuestionDto() {
    }

    public CombinationQuestionDto(CombinationQuestion question) {
        this.language = question.getLanguage();
        this.combinationGroup = question
                .getCombinationGroup()
                .stream()
                .map(CombinationGroupDto::new)
                .collect(Collectors.toList());
    }

    public Languages getLanguage() {
        return language;
    }

    public void setLanguage(Languages language) {
        this.language = language;
    }

    public List<CombinationGroupDto> getCombinationGroup() {
        return combinationGroup;
    }

    public void setCombinationGroup(List<CombinationGroupDto> combinationGroup) {
        this.combinationGroup = combinationGroup;
    }

    @Override
    public String toString() {
        return "CombinationQuestionDto{" +
                "language='" + language + '\'' +
                ", groups=" + combinationGroup +
                '}';
    } 

    @Override
    public QuestionDetails getQuestionDetails(Question question) {
        return new CombinationQuestion(question, this);
    }

    @Override
    public void update(CombinationQuestion question) {
        question.update(this);
    }
}
