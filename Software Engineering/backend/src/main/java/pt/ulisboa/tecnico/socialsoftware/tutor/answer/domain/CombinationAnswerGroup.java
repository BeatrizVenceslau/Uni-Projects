package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CombinationGroup;

import javax.persistence.*;

@Entity
public class CombinationAnswerGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private CombinationAnswer combinationAnswer;

    @ManyToOne(optional = false)
    private CombinationGroup combinationGroup;

    private Integer assignedOrder;

    public CombinationAnswerGroup() {
    }

    public CombinationAnswerGroup(CombinationGroup combineGroup, CombinationAnswer combinationAnswer, Integer assignedOrder) {
        setCombinationGroup(combineGroup);
        setCombinationAnswer(combinationAnswer);
        setAssignedOrder(assignedOrder);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CombinationAnswer getCombinationAnswer() {
        return combinationAnswer;
    }

    public void setCombinationAnswer(CombinationAnswer combinationAnswer) {
        this.combinationAnswer = combinationAnswer;
    }

    public CombinationGroup getCombinationGroup() {
        return combinationGroup;
    }
 
    public void setCombinationGroup(CombinationGroup combinationGroup) {
        this.combinationGroup= combinationGroup;
    }

    public Integer getAssignedOrder() {
        return assignedOrder;
    }

    public void setAssignedOrder(Integer assignedOrder) {
        this.assignedOrder = assignedOrder;
    }

}