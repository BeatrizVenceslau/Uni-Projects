package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CombinationGroup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CombinationGroupDto implements Serializable {
    private Integer id;
    private String title;
    private Integer sequence;
    private List<OptionDto> items = new ArrayList<>();

    public CombinationGroupDto() {}

    public CombinationGroupDto(CombinationGroup combinationGroup) {
        this.id = combinationGroup.getId();
        this.title = combinationGroup.getTitle();
        this.sequence = combinationGroup.getSequence();
        this.items = combinationGroup.getItems().stream().map(OptionDto::new).collect(Collectors.toList());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }
    
    public List<OptionDto> getItems() {
        return items;
    }

    public void setItems(List<OptionDto> items) {
        this.items = items;
    }
 
    @Override
    public String toString() {
        return "CombinationGroupDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content=" + items +
                '}';
    }
}
