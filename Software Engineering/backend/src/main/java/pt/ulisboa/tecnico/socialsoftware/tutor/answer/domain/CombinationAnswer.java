package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import org.apache.commons.lang3.tuple.Pair;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.AnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CombinationAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CombinationStatementAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementAnswerDetailsDto;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_ORDER_SLOT_MISMATCH;

@Entity
@DiscriminatorValue(Question.QuestionTypes.ITEM_COMBINATION_QUESTION)
public class CombinationAnswer extends AnswerDetails {
    @OneToMany(mappedBy = "combinationAnswer", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private final Set<CombinationAnswerGroup> combinedGroup = new HashSet<>();

    public CombinationAnswer() {
        super();
    }

    public CombinationAnswer(QuestionAnswer questionAnswer) {
        super(questionAnswer);
    }

    @Override
    public boolean isCorrect() {
        return true;
    }

    @Override
    public void remove() {

    }

    @Override
    public AnswerDetailsDto getAnswerDetailsDto() {
        return new CombinationAnswerDto(this);
    }

    @Override
    public String getAnswerRepresentation() {
        /*int counter = 1;
        var slots = new ArrayList<>(((CodeOrderQuestion) this.getQuestionAnswer().getQuestion().getQuestionDetails()).getCodeOrderSlots());
        slots.sort(Comparator.comparing(CodeOrderSlot::getId));
        var slotsSequence = new HashMap<Integer, Integer>();
        for (var codeOrderSlot : slots) {
            slotsSequence.put(codeOrderSlot.getId(),counter++);
        }
        return this.getCombinedGroups().stream()
                .sorted(Comparator.comparing(CodeOrderAnswerSlot::getAssignedOrder))
                .map(x -> slotsSequence.get(x.getCodeOrderSlot().getId()).toString())
                .collect(Collectors.joining(" | "));*/
        return null;
    }

    @Override
    public StatementAnswerDetailsDto getStatementAnswerDetailsDto() {
        return new CombinationStatementAnswerDetailsDto(this);
    }

    @Override
    public boolean isAnswered() {
        return combinedGroup != null && !combinedGroup.isEmpty();
    }

    public Set<CombinationAnswerGroup> getCombinedGroups() {
        return combinedGroup;
    }

    public void setCombinedGroups(CombinationQuestion question,
                                CombinationStatementAnswerDetailsDto codeOrderStatementAnswerDetailsDto) {
        /*this.combinedGroup.clear();
        if (!codeOrderStatementAnswerDetailsDto.emptyAnswer()) {
            for (var slot : codeOrderStatementAnswerDetailsDto.getCombinedGroups()) {

                CombinationGroup combinationGroup = question
                                    .getCombinationGroupByGroupId(slot.getItemId());

                var answerSlot = new CombinationAnswerGroup(combinationGroup, this, slot.getOrder());
                this.combinedGroup.add(answerSlot);
            }
        }*/
        
    }
 

    @Override
    public void accept(Visitor visitor) {
        visitor.visitAnswerDetails(this);
    }
}

