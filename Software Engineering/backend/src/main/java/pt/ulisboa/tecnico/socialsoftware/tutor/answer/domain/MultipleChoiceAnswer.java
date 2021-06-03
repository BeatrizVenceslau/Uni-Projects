package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;

import javax.persistence.*;
import javax.management.openmbean.OpenDataException;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_OPTION_MISMATCH;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

@Entity
@DiscriminatorValue(Question.QuestionTypes.MULTIPLE_CHOICE_QUESTION)
public class MultipleChoiceAnswer extends AnswerDetails {
    @ManyToOne
    @JoinColumn(name = "option_id")
    private Option option;

    //@OneToMany(cascade = CascadeType.ALL, mappedBy = "answerDetails", fetch = FetchType.EAGER, orphanRemoval = true)    //NOVO
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "questionDetails", fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Option> options = new ArrayList<>();


    private Boolean multipleSelection = false;                  //NOVO
    private Boolean orderOfRelevance = false;

    public MultipleChoiceAnswer() {
        super();
    }

    public MultipleChoiceAnswer(QuestionAnswer questionAnswer){
        super(questionAnswer);
    }

    public MultipleChoiceAnswer(QuestionAnswer questionAnswer, Option option){
        super(questionAnswer);
        this.setOption(option);
    }

    public MultipleChoiceAnswer(QuestionAnswer questionAnswer, List<Option> options){           //NOVO
        super(questionAnswer);
        this.setOptions(options);
        multipleSelection = true;
        if(options.get(0).getRelevance() != null) orderOfRelevance = true;
    }

    public Option getOption() {
        return option;
    }

    public List<Option> getOptions() {
        return options;
    }

    public Boolean getOrderOfRelevance() {
        return orderOfRelevance;
    }

    public Boolean getMultipleSelection() {
        return multipleSelection;
    }

    public void setOption(Option option) {
        this.option = option;

        if (option != null)
            option.addQuestionAnswer(this);
    }


    public void setOptions(List<Option> options) {
        this.options = options;

        for(Option option : options){
            option.addQuestionAnswer(this);
        }
    }


    public void setOption(MultipleChoiceQuestion question, MultipleChoiceStatementAnswerDetailsDto multipleChoiceStatementAnswerDetailsDto) {
        if (multipleChoiceStatementAnswerDetailsDto.getOptionId() != null) {
            Option opt = question.getOptions().stream()
                    .filter(option1 -> option1.getId().equals(multipleChoiceStatementAnswerDetailsDto.getOptionId()))
                    .findAny()
                    .orElseThrow(() -> new TutorException(QUESTION_OPTION_MISMATCH, multipleChoiceStatementAnswerDetailsDto.getOptionId()));

            if (this.getOption() != null) {
                this.getOption().getQuestionAnswers().remove(this);
            }

            this.setOption(opt);
        } else {
            this.setOption(null);
        }
    }

    //NOVO
    public void setOptions(MultipleChoiceQuestion question, MultipleChoiceStatementAnswerDetailsDto multipleChoiceStatementAnswerDetailsDto) {
        if (multipleChoiceStatementAnswerDetailsDto.getOptionsIds() != null) {
            /*List<Integer> optionsIds1 = question.getOptions().stream().map(Option::getId).collect(Collectors.toList());
            List<Integer> optionsIds2 = multipleChoiceStatementAnswerDetailsDto.getOptionsIds();
            if(optionsIds1.equals(optionsIds2) == false){
                throw new TutorException(QUESTION_OPTION_MISMATCH);

            }*/
            List<Option> optionsAnswered = new ArrayList<>();
            for (Option option : question.getOptions()) {
                if (multipleChoiceStatementAnswerDetailsDto.getOptionsIds().contains(option.getId())){
                    optionsAnswered.add(option);
                }
            }
            
            //if(3>1)throw new TutorException(QUESTION_OPTION_MISMATCH);
            //List<Option> options = question.getOptions().stream()
            //        .filter(option1 -> multipleChoiceStatementAnswerDetailsDto.getOptionsIds().contains(option1.getId())).collect(Collectors.toList());
            if (this.getOptions() != null) {

                for(Option option : this.getOptions()){
                    option.getQuestionAnswers().remove(this);
                }
            }
            //if(optionsAnswered.size() == 2) throw new TutorException(QUESTION_OPTION_MISMATCH);
            this.setOptions(optionsAnswered);
        } else {
            this.setOptions(null);
        }
        
    }


    @Override
    public boolean isCorrect() {
        return getOption() != null && getOption().isCorrect();
    }


    public void remove() {
        if (option != null) {
            option.getQuestionAnswers().remove(this);
            option = null;
        }
    }

    @Override
    public AnswerDetailsDto getAnswerDetailsDto() {
        return new MultipleChoiceAnswerDto(this);
    }

    @Override
    public boolean isAnswered() {
        if(this.getOption() != null) return true;
        if(this.getOptions() != null) return true;
        return false;
        //return this.getOption() != null;
    }

    @Override
    public String getAnswerRepresentation() {
        return this.getOption() != null ? MultipleChoiceQuestion.convertSequenceToLetter(this.getOption().getSequence()) : "-";
    }

    @Override
    public StatementAnswerDetailsDto getStatementAnswerDetailsDto() {
        return new MultipleChoiceStatementAnswerDetailsDto(this);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitAnswerDetails(this);
    }
}
