package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.MultipleChoiceStatementAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.QuestionDetails;

import javax.persistence.ElementCollection;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Arrays;
import java.util.List;

@Entity
@DiscriminatorValue(Question.QuestionTypes.MULTIPLE_CHOICE_QUESTION)
public class MultipleChoiceAnswerItem extends QuestionAnswerItem {

    private Integer optionId;
    @ElementCollection
    private List<Integer> optionsIds;                    //NOVO


    public MultipleChoiceAnswerItem() {
    }

    public MultipleChoiceAnswerItem(String username, int quizId, StatementAnswerDto answer, MultipleChoiceStatementAnswerDetailsDto detailsDto) {
        super(username, quizId, answer);
        if(detailsDto.getOptionId() != null) this.optionId = detailsDto.getOptionId();
        if(detailsDto.getOptionsIds() != null) this.optionsIds = detailsDto.getOptionsIds();
    }

    @Override
    public String getAnswerRepresentation(QuestionDetails questionDetails) {
        return this.getOptionId() != null ? questionDetails.getAnswerRepresentation(Arrays.asList(optionId)) : "-";
    }

    public Integer getOptionId() {
        return optionId;
    }

    public List<Integer> getOptionsIds() {                                   //NOVO
        return optionsIds;
    }

    public void setOptionId(Integer optionId) {
        this.optionId = optionId;
    }

    public void setOptionsId(List<Integer> optionIds) {                      //NOVO
        this.optionsIds = optionIds;
    }
}
