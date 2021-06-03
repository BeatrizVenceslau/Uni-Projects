package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;

import javax.persistence.*;

import org.hibernate.annotations.ManyToAny;

import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "combination_items_options")
public class CombinationItem implements DomainEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer sequence;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "combination_items_id")
    private CombinationGroup combinationGroup;

    @OneToMany()
    @JoinColumn(nullable = true)
    private List<CombinationItem> combinationAnswers = new ArrayList<>();

    public CombinationItem() {
    }

    public CombinationItem(OptionDto option) {
        setSequence(option.getSequence());
        setContent(option.getContent());
        setCombination(option.getCombination());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public void setCombination(List<CombinationItem> combination) {
        this.combinationAnswers = combination;
    }

    public List<CombinationItem> getCombination() {
        return combinationAnswers;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public CombinationGroup getCombinationGroup() {
        return combinationGroup;
    }

    public void setCombinedGroup(CombinationGroup combinationGroup) {
        this.combinationGroup = combinationGroup;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitCombinationItem(this);
    }


    public void delete() {
        this.combinationGroup = null;
    }
}