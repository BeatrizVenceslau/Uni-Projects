package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.Updator;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.*;

import java.util.List;
import javax.persistence.*;

@Entity
@DiscriminatorValue(Question.QuestionTypes.OPEN_ENDED_QUESTION)
public class OpenEndedQuestion extends QuestionDetails {
    @Column(name = "default_answer", columnDefinition = "TEXT", nullable = true)
    private String defaultAnswer;

    public OpenEndedQuestion() {
        super();
    }

    public OpenEndedQuestion(String answer) {
        super();
        setDefaultAnswer(answer);
    }

    public OpenEndedQuestion(Question question, OpenEndedQuestionDto questionDto) {
        super(question);
        setDefaultAnswer(questionDto.getDefaultCorrectAnswer());
    }

    public String getDefaultAnswer() {
        return defaultAnswer;
    }

    public void setDefaultAnswer(String answer) throws TutorException {
        if (answer == null || answer.strip().equals("")) {
            throw new TutorException(ErrorMessage.NO_CORRECT_ANSWER);
        } else {
            defaultAnswer = answer;
        }
    }

    public void update(OpenEndedQuestionDto questionDetails) {
        setDefaultAnswer(questionDetails.getDefaultCorrectAnswer());
    }

    @Override
    public void update(Updator updator) {
        updator.update(this);
    }

    @Override
    public String getCorrectAnswerRepresentation() {
        return getDefaultAnswer();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitQuestionDetails(this);
    }

    @Override
    public CorrectAnswerDetailsDto getCorrectAnswerDetailsDto() {
        return new OpenEndedCorrectAnswerDto(this);
    }

    @Override
    public StatementQuestionDetailsDto getStatementQuestionDetailsDto() {
        return new OpenEndedStatementQuestionDetailsDto(this);
    }

    @Override
    public StatementAnswerDetailsDto getEmptyStatementAnswerDetailsDto() {
        return new OpenEndedStatementAnswerDetailsDto();
    }

    @Override
    public AnswerDetailsDto getEmptyAnswerDetailsDto() {
        return new OpenEndedAnswerDto();
    }

    @Override
    public QuestionDetailsDto getQuestionDetailsDto() {
        return new OpenEndedQuestionDto(this);
    }

    @Override
    public void delete() {
        super.delete();
        this.defaultAnswer = null;
    }

    @Override
    public String toString() {
        // May be a good idea to also include the actual question
        // It's definitely possible for two different questions
        // To have the same default answer
        return "OpenEndedQuestion{" +
                "content='" + this.getQuestion().getContent() +
                "',defaultAnswer='" + defaultAnswer +
                "'}";
    }

    @Override
    public String getAnswerRepresentation(List<Integer> selectedIds) {
        return "";
    }
}
