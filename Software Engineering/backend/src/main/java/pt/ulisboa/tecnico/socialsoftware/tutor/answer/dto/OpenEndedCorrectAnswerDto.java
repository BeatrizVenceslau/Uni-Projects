package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenEndedQuestion;

public class OpenEndedCorrectAnswerDto extends CorrectAnswerDetailsDto {
    private String defaultCorrectAnswer;

    public OpenEndedCorrectAnswerDto(OpenEndedQuestion question) {
        this.defaultCorrectAnswer = question.getCorrectAnswerRepresentation();
    }

    public String getDefaultCorrectAnswer() {
        return defaultCorrectAnswer;
    }

    public void setDefaultCorrectAnswer(String correctAnswer) {
        this.defaultCorrectAnswer = correctAnswer;
    }

    @Override
    public String toString() {
        return "OpenEndedCorrectAnswerDto{" +
                "defaultCorrectAnswer='" + defaultCorrectAnswer +
                "'}";
    }
}
