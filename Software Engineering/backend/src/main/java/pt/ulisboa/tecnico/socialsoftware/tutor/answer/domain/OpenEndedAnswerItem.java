package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.OpenEndedStatementAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.QuestionDetails;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(Question.QuestionTypes.OPEN_ENDED_QUESTION)
public class OpenEndedAnswerItem extends QuestionAnswerItem {
    private String answer;

    public OpenEndedAnswerItem() {
    }

    public OpenEndedAnswerItem(String username, int quizId, StatementAnswerDto answer, OpenEndedStatementAnswerDetailsDto detailsDto) {
        super(username, quizId, answer);
        this.answer = detailsDto.getAnswer();
    }

    @Override
    public String getAnswerRepresentation(QuestionDetails questionDetails) {
        return answer.isBlank() ? answer : "";
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
