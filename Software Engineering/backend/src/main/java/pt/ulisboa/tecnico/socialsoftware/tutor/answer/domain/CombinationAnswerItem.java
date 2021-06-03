package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CombinationStatementAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.QuestionDetails;

import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@DiscriminatorValue(Question.QuestionTypes.ITEM_COMBINATION_QUESTION)
public class CombinationAnswerItem extends QuestionAnswerItem {

    @ElementCollection
    private List<CombinationGroupAnswerItem> combinedGroups;

    public CombinationAnswerItem() {
    }
 
    public CombinationAnswerItem(String username, int quizId, StatementAnswerDto answer, CombinationStatementAnswerDetailsDto detailsDto) {
        super(username, quizId, answer);
        this.combinedGroups = detailsDto.getCombinedGroups()
                .stream()
                .map(CombinationGroupAnswerItem::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getAnswerRepresentation(QuestionDetails questionDetails) {
        return questionDetails.getAnswerRepresentation(
                combinedGroups.stream()
                        .sorted(Comparator.comparing(CombinationGroupAnswerItem::getAssignedOrder))
                        .map(CombinationGroupAnswerItem::getGroupId)
                        .collect(Collectors.toList()));
    }
}
