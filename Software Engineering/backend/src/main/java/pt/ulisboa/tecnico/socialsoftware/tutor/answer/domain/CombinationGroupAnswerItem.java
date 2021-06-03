package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CombinationGroupStatementAnswerDetailsDto;

import javax.persistence.Embeddable;

@Embeddable
public class CombinationGroupAnswerItem {
    private Integer groupId;
    private Integer assignedOrder;

    public CombinationGroupAnswerItem() {}

    public CombinationGroupAnswerItem(CombinationGroupStatementAnswerDetailsDto combinationGroupStatementAnswerDto) {
        groupId = combinationGroupStatementAnswerDto.getItemId();
        assignedOrder = combinationGroupStatementAnswerDto.getOrder();
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }
 
    public Integer getAssignedOrder() {
        return assignedOrder;
    }

    public void setAssignedOrder(Integer assignedOrder) {
        this.assignedOrder = assignedOrder;
    }
}
