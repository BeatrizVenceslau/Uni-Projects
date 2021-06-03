package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.*;

public class OpenEndedQuestionDto extends QuestionDetailsDto {
    @JsonProperty("defaultAnswer")
    private String defaultCorrectAnswer;
    
    public OpenEndedQuestionDto() {}

    public OpenEndedQuestionDto(OpenEndedQuestion question) {
        this.defaultCorrectAnswer = question.getDefaultAnswer();
    }

    public String getDefaultCorrectAnswer() {
        return defaultCorrectAnswer;
    }

    public void setDefaultCorrectAnswer(String defaultCorrectAnswer) {
        this.defaultCorrectAnswer = defaultCorrectAnswer;
    }

    @Override
    public QuestionDetails getQuestionDetails(Question question) {
        return new OpenEndedQuestion(question, this);
    }

    @Override
    public void update(OpenEndedQuestion question) {
        question.update(this);
    }

    @Override
    public String toString() {
        return "OpenEndedQuestionDto{" +
                "defaultAnswer='" + defaultCorrectAnswer +
                "'}";
    }
}
