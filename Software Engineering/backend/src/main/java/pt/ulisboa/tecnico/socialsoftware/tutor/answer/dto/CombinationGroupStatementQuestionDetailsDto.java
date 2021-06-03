package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CombinationGroup;

import java.io.Serializable;

public class CombinationGroupStatementQuestionDetailsDto implements Serializable {
    private Integer id;
    private String content;

    public CombinationGroupStatementQuestionDetailsDto(CombinationGroup combinationGroup) {
        id = combinationGroup.getId();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }
 
    public void setContent(String content) {
        this.content = content;
    }
}
