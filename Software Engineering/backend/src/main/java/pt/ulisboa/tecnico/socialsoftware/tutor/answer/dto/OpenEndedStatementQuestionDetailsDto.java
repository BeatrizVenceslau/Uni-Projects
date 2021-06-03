package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenEndedQuestion;

public class OpenEndedStatementQuestionDetailsDto extends StatementQuestionDetailsDto {
    private String defaultCorrectAnswer;

    public OpenEndedStatementQuestionDetailsDto(OpenEndedQuestion question) {
        this.defaultCorrectAnswer = question.getCorrectAnswerRepresentation();
    }

    public String getDefaultCorrectAnswer() {
        return defaultCorrectAnswer;
    }

    public void setDefaultCorrectAnswer(String answer) {
        defaultCorrectAnswer = answer;
    }

    @Override
    public String toString() {
        return "OpenEndedStatementQuestionDetailsDto{" +
                "defaultCorrectAnswer='" + defaultCorrectAnswer +
                "'}";
    }
}
