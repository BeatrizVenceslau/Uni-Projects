package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.CombinationAnswerGroup;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CombinationGroup;

public class CombinationAnswerGroupDto {
    private Integer itemId;
    private Integer order;
    private boolean correct;
    private Integer sequence;

    public CombinationAnswerGroupDto(CombinationGroup correctGroup) {
        itemId = correctGroup.getId();
        correct = true;
    }

    public CombinationAnswerGroupDto(CombinationAnswerGroup answercombinedGroup) {
        itemId = answercombinedGroup.getCombinationGroup().getId();
        order = answercombinedGroup.getAssignedOrder();
        sequence = answercombinedGroup.getCombinationGroup().getSequence();
    }

    public Integer getItemId() {
        return itemId;
    }
 
    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }
}
