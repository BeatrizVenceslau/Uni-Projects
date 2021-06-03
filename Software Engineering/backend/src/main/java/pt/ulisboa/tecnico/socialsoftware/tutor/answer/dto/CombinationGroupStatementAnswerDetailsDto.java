package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.CombinationAnswerGroup;

import java.io.Serializable;

public class CombinationGroupStatementAnswerDetailsDto implements Serializable {
    private Integer itemId;
    private Integer order;

    public CombinationGroupStatementAnswerDetailsDto() {
    }

    public CombinationGroupStatementAnswerDetailsDto(Integer itemId, Integer order) {
        this.itemId = itemId;
        this.order = order;
    }

    public CombinationGroupStatementAnswerDetailsDto(CombinationAnswerGroup option) {
        this.order = option.getAssignedOrder();
        this.itemId = option.getCombinationGroup().getId();
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
}