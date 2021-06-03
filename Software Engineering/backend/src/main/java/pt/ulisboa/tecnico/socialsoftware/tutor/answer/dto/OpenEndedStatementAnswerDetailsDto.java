package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.*;

public class OpenEndedStatementAnswerDetailsDto extends StatementAnswerDetailsDto {
    private String givenAnswer;
    private OpenEndedAnswer createdOpenEndedAnswer;

    private void normalizeAnswer() {
        if (this.emptyAnswer()) {
            givenAnswer = null;
        }
    }

    public OpenEndedStatementAnswerDetailsDto() {
    }

    public OpenEndedStatementAnswerDetailsDto(OpenEndedAnswer questionAnswer) {
        this.givenAnswer = questionAnswer.getAnswer();
        this.normalizeAnswer();
    }

    public String getAnswer() {
        return givenAnswer;
    }

    public void setAnswer(String answer) {
        this.givenAnswer = answer;
        this.normalizeAnswer();
    }

    @Override
    public AnswerDetails getAnswerDetails(QuestionAnswer questionAnswer) {
        createdOpenEndedAnswer = new OpenEndedAnswer(questionAnswer);
        questionAnswer.getQuestion().getQuestionDetails().update(this);
        return createdOpenEndedAnswer;
    }

    @Override
    public boolean emptyAnswer() {
        return (givenAnswer == null || "".strip().equals(givenAnswer));
    }

    @Override
    public QuestionAnswerItem getQuestionAnswerItem(String username, int quizId, StatementAnswerDto statementAnswerDto) {
        return new OpenEndedAnswerItem(username, quizId, statementAnswerDto, this);
    }

    @Override
    public void update(OpenEndedQuestion question) {
        createdOpenEndedAnswer.setAnswer(question, this);
    }

    @Override
    public String toString() {
        return "OpenEndedStatementAnswerDto{" +
                "givenAnswer='" + givenAnswer +
                "'}";
    }
}
