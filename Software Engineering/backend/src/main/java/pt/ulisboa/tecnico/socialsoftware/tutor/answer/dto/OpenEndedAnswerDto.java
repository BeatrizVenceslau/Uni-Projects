package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.OpenEndedAnswer;

public class OpenEndedAnswerDto extends AnswerDetailsDto {
    private String givenAnswer;

    public OpenEndedAnswerDto() {
    }

    public OpenEndedAnswerDto(OpenEndedAnswer answer) {
        String answerRep = answer.getAnswerRepresentation();
        this.setAnswer(answerRep);
    }

    public String getAnswer() {
        return givenAnswer;
    }

    public void setAnswer(String answer) {
        this.givenAnswer = answer.strip().equals("") ? null : answer;
    }
}
