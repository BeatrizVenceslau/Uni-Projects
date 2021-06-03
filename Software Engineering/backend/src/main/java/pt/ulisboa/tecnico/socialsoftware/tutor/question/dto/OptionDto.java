package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeFillInOption;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CombinationItem;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;

import java.io.Serializable;
import java.util.List;

public class OptionDto implements Serializable {
    private Integer id;
    private Integer sequence;
    private Integer relevance;  //NOVO
    private boolean correct;
    private List<CombinationItem> combination;
    private String content;


    public OptionDto() {
    }

    public OptionDto(Option option) {
        this.id = option.getId();
        this.sequence = option.getSequence();
        this.content = option.getContent();
        this.correct = option.isCorrect();
        if(option.getRelevance() != null) this.relevance = option.getRelevance();         //NOVO 

    }

    public OptionDto(CodeFillInOption option) {
        this.id = option.getId();
        this.sequence = option.getSequence();
        this.content = option.getContent();
        this.correct = option.isCorrect();
    }
 
    public OptionDto(CombinationItem option) {
        this.id = option.getId();
        this.content = option.getContent();
        this.sequence = option.getSequence();
        this.combination = option.getCombination();
    }

    public Integer getId() {
        return id;
    }

    public Integer getSequence() {
        return sequence;
    }

    public Integer getRelevance() {return relevance;}            //NOVO

    public void setRelevance(Integer relevance) {this.relevance = relevance;}    //NOVO

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public boolean isCorrect() {
        return correct;
    }


    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCombination(List<CombinationItem> combination) {
        this.combination = combination;
    }

    public List<CombinationItem> getCombination() {
        return combination;
    }

    @Override
    public String toString() {     //COMPLETAR
        return "OptionDto{" +
                "id=" + id +
                ", content='" + content + '\'' +
                '}';
    }
}