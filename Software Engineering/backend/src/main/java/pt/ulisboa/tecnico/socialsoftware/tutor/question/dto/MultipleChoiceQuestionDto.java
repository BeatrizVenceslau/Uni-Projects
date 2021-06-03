package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.QuestionDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MultipleChoiceQuestionDto extends QuestionDetailsDto {
    private List<OptionDto> options = new ArrayList<>();


    
    private Boolean orderOfRelevance = false;   //NOVO


    public MultipleChoiceQuestionDto() {
    }

    public MultipleChoiceQuestionDto(MultipleChoiceQuestion question) {
        this.options = question.getOptions().stream().map(OptionDto::new).collect(Collectors.toList());
    }

    public List<OptionDto> getOptions() {
        return options;
    }

    public void setOptions(List<OptionDto> options) {
        this.options = options;
    }

    public void setOrderOfRelevance(Boolean value) {
        orderOfRelevance = value;
    }

    @Override
    public QuestionDetails getQuestionDetails(Question question) {
        return new MultipleChoiceQuestion(question, this);
    }


    public Boolean getOrderOfRelevance() {
        return orderOfRelevance;
    }


    @Override
    public void update(MultipleChoiceQuestion question) {
        question.update(this);
    }

    @Override
    public String toString() {
        return "MultipleChoiceQuestionDto{" +
                "options=" + options +
                '}';
    }

}
