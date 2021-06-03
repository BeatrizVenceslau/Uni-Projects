package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.CombinationGroupDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "combination_items_group",
                uniqueConstraints = @UniqueConstraint(columnNames = {"question_details_id", "sequence"}))
public class CombinationGroup implements DomainEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer sequence;

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_details_id")
    private CombinationQuestion questionDetails;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "combinationGroup", fetch = FetchType.LAZY, orphanRemoval = true)
    private final List<CombinationItem> items = new ArrayList<>();

    public CombinationGroup() {
    }

    public CombinationGroup(CombinationGroupDto combinationGroupDto) {
        setTitle(combinationGroupDto.getTitle());
        setItems(combinationGroupDto.getItems());
        setSequence(combinationGroupDto.getSequence());        
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public CombinationQuestion getQuestionDetails() {
        return questionDetails;
    }

    public void setQuestionDetails(CombinationQuestion questionDetails) {
        this.questionDetails = questionDetails;
    }

    public List<CombinationItem> getItems() {
        return items;
    }

    public void setItems(List<OptionDto> items) {
        // Ensures some randomization when creating the options ids.
        Collections.shuffle(items);

        this.items.clear();

        int index = 0;
        for (OptionDto optionDto : items) {
            int sequence = optionDto.getSequence() != null ? optionDto.getSequence() : index++;
            optionDto.setSequence(sequence);
            CombinationItem combinationItem = new CombinationItem(optionDto);
            combinationItem.setCombinedGroup(this);
            this.items.add(combinationItem);
        }
    }

    public void delete() {
        this.questionDetails = null;
        for (CombinationItem item : this.items) {
            item.delete();
        }
        this.items.clear();
    }

    public void visitOptions(Visitor visitor) {
        for (CombinationItem item : this.getItems()) {
            item.accept(visitor);
        }
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitCombinationGroups(this);
    } 
}