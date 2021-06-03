package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;


import java.util.List;         //NOVO


public class MultipleChoiceCorrectAnswerDto extends CorrectAnswerDetailsDto {       //MODIFICADO
    private Integer correctOptionId;

    private List<Integer> correctOptionsIds;                    //NOVO
    private Boolean multipleSelection = false;                  //NOVO
    private Boolean orderOfRelevance = false;

    public MultipleChoiceCorrectAnswerDto(MultipleChoiceQuestion question) {
        if(question.isOrderOfRelevance()) orderOfRelevance = true;                                            //NOVO

       /* if(question.isMultipleSelection() == false) this.correctOptionId = question.getCorrectOptionId();     //MODIFICADO
        else{
            this.correctOptionsIds = question.getCorrectOptionsIds();                    //NOVO - Se pergunta tiver mais que uma opcao correta
            multipleSelection = true;
        }*/
        if (question.getCorrectOptionId() != null) this.correctOptionId = question.getCorrectOptionId();
        if (question.getCorrectOptionsIds() != null){
            this.correctOptionsIds = question.getCorrectOptionsIds();
            multipleSelection = true;
        }
    }

    public Integer getCorrectOptionId() {
        return correctOptionId;
    }

    public List<Integer> getCorrectOptionsIds() {                                   //NOVO
        return correctOptionsIds;
    }

    public void setCorrectOptionId(Integer correctOptionId) {
        this.correctOptionId = correctOptionId;
    }


    public void setCorrectOptionsIds(List<Integer> correctOptionsIds) {                //NOVO
        this.correctOptionsIds = correctOptionsIds;
    }


    public Boolean isMultipleSelection(){                                            //NOVO
        return multipleSelection;
    }

    public Boolean isOrderOfRelevance(){                                            //NOVO
        return orderOfRelevance;
    }


    @Override
    public String toString() {                                                         //MODIFICADO
        if(multipleSelection == false) return "MultipleChoiceCorrectAnswerDto{" + "correctOptionId=" + correctOptionId + '}';
        else return "MultipleChoiceCorrectAnswerDto{" + "correctOptionId=" + correctOptionsIds + '}';
    }
}