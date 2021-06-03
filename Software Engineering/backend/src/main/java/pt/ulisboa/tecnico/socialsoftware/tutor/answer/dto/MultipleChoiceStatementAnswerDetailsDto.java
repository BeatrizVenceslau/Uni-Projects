package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import java.util.stream.Collectors;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.AnswerDetails;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswerItem;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswerItem;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;


import javax.persistence.Transient;

public class MultipleChoiceStatementAnswerDetailsDto extends StatementAnswerDetailsDto {
    private Integer optionId;
    private List<Integer> optionsIds;                    //NOVO

    public MultipleChoiceStatementAnswerDetailsDto() {
    }

    public MultipleChoiceStatementAnswerDetailsDto(MultipleChoiceAnswer questionAnswer) {
        if (questionAnswer.getOption() != null) {
            this.optionId = questionAnswer.getOption().getId();
        }
        if (questionAnswer.getOptions() != null) {
            this.optionsIds = questionAnswer.getOptions().stream().map(Option::getId).collect(Collectors.toList());
        }
    }

    public Integer getOptionId() {
        return optionId;
    }

    public List<Integer> getOptionsIds() {
        return optionsIds;
    }

    public void setOptionId(Integer optionId) {
        this.optionId = optionId;
    }


    public void setOptionsIds(List<Integer> optionsIds) {           //NOVO
        this.optionsIds = optionsIds;
    }

    @Transient
    private MultipleChoiceAnswer createdMultipleChoiceAnswer;

    @Override
    public AnswerDetails getAnswerDetails(QuestionAnswer questionAnswer) {
        createdMultipleChoiceAnswer = new MultipleChoiceAnswer(questionAnswer);
        questionAnswer.getQuestion().getQuestionDetails().update(this);
        return createdMultipleChoiceAnswer;
    }

    @Override
    public boolean emptyAnswer() {
        return optionId == null;
    }

    @Override
    public QuestionAnswerItem getQuestionAnswerItem(String username, int quizId, StatementAnswerDto statementAnswerDto) {
        return new MultipleChoiceAnswerItem(username, quizId, statementAnswerDto, this);
    }

    @Override
    public void update(MultipleChoiceQuestion question) {
        /*if( question.isMultipleSelection() == false){
            createdMultipleChoiceAnswer.setOption(question, this);      //MODIFICADO
        }
        if(question.isMultipleSelection()){
            createdMultipleChoiceAnswer.setOptions(question, this);
        }*/
        if( getOptionId() != null){ 
            createdMultipleChoiceAnswer.setOption(question, this);      //MODIFICADO
        }
        else if(getOptionsIds() != null){
            createdMultipleChoiceAnswer.setOptions(question, this);      //MODIFICADO
        }
    }

    @Override
    public String toString() {
        return "MultipleChoiceStatementAnswerDto{" +
                "optionId=" + optionId +
                '}';
    }
}
