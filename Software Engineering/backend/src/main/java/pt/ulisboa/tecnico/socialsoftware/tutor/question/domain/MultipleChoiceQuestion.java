package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.Updator;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDetailsDto;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@DiscriminatorValue(Question.QuestionTypes.MULTIPLE_CHOICE_QUESTION)
public class MultipleChoiceQuestion extends QuestionDetails {
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "questionDetails", fetch = FetchType.EAGER, orphanRemoval = true)
    private final List<Option> options = new ArrayList<>();

    private Boolean multipleSelection = false;    //NOVO
    private Boolean orderOfRelevance = false;

    private Boolean multipleSelection = false;    //NOVO
    private Boolean orderOfRelevance = false;      


    private Boolean multipleSelection = false;    //NOVO
    private Boolean orderOfRelevance = false;      


    public MultipleChoiceQuestion() {
        super();
    }

    public MultipleChoiceQuestion(Question question, MultipleChoiceQuestionDto questionDto) {
        super(question);
        setOrderOfRelevance(questionDto.getOrderOfRelevance());
        setOptions(questionDto.getOptions());
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<OptionDto> options) {
        //if (options.stream().filter(OptionDto::isCorrect).count() != 1) {
         //   throw new TutorException(ONE_CORRECT_OPTION_NEEDED);
        //}
        int numcorrect = (int)options.stream().filter(OptionDto::isCorrect).count();
        int numrelevance = (int)options.stream().filter(option -> option.getRelevance() != null).count();




        if (numrelevance > 0) {
            orderOfRelevance = true;
        }

        if (numcorrect == 0) {
            throw new TutorException(ONE_CORRECT_OPTION_NEEDED);
        }
        if (numcorrect > 1){
            multipleSelection = true;
        }
        else{
            multipleSelection = false;
            if (orderOfRelevance == true) throw new TutorException(RELEVANCE_ONLY_WITH_MULTIPLE_SELECTION);
        }
        if(multipleSelection && orderOfRelevance){
            if(numrelevance != numcorrect) throw new TutorException(ALL_CORRECT_OPTIONS_RELEVANCE);
        }

        int index = 0;
        for (OptionDto optionDto : options) {
            if (optionDto.getId() == null) {
                optionDto.setSequence(index++);
                new Option(optionDto).setQuestionDetails(this);
            } else {
                Option option = getOptions()
                        .stream()
                        .filter(op -> op.getId().equals(optionDto.getId()))
                        .findAny()
                        .orElseThrow(() -> new TutorException(OPTION_NOT_FOUND, optionDto.getId()));

                option.setContent(optionDto.getContent());
                option.setCorrect(optionDto.isCorrect());
                if(orderOfRelevance){
                    if(optionDto.getRelevance() != null && optionDto.isCorrect() == false){
                        //throw new TutorException(RELEVANCE_ONLY_WITH_CORRECT_OPTION);
                        continue;
                    }
                    if(optionDto.getRelevance()!= null)option.setRelevance(optionDto.getRelevance());
                }
            }
        }
    }

    public void addOption(Option option) {
        options.add(option);
    }


    public void setOrderOfRelevance(boolean value){
        orderOfRelevance = value;
    }



    public void setMultipleSelection(boolean value){
        multipleSelection = value;
    }

    public Boolean isMultipleSelection(){
        return multipleSelection;
    }

    public Boolean isOrderOfRelevance(){
        return orderOfRelevance;
    }



    public Integer getCorrectOptionId() {
        return this.getOptions().stream()
                .filter(Option::isCorrect)
                .findAny()
                .map(Option::getId)
                .orElse(null);
    }


    public List<Integer> getCorrectOptionsIds() { // NOVO - mais de um opcao correta
        return this.getOptions().stream()
                .filter(Option::isCorrect)
                .map(Option::getId)
                .collect(Collectors.toList());
    }



    public void update(MultipleChoiceQuestionDto questionDetails) {
        setOptions(questionDetails.getOptions());
    }

    @Override
    public void update(Updator updator) {
        updator.update(this);
    }

    @Override
    public String getCorrectAnswerRepresentation() {    // MODIFICADO
        if(multipleSelection == false) return convertSequenceToLetter(this.getCorrectAnswer());
        else return convertSequenceToLetter(this.getCorrectAnswers());
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitQuestionDetails(this);
    }

    public void visitOptions(Visitor visitor) {
        for (Option option : this.getOptions()) {
            option.accept(visitor);
        }
    }

    @Override
    public CorrectAnswerDetailsDto getCorrectAnswerDetailsDto() {
        return new MultipleChoiceCorrectAnswerDto(this);
    }

    @Override
    public StatementQuestionDetailsDto getStatementQuestionDetailsDto() {
        return new MultipleChoiceStatementQuestionDetailsDto(this);
    }

    @Override
    public StatementAnswerDetailsDto getEmptyStatementAnswerDetailsDto() {
        return new MultipleChoiceStatementAnswerDetailsDto();
    }

    @Override
    public AnswerDetailsDto getEmptyAnswerDetailsDto() {
        return new MultipleChoiceAnswerDto();
    }

    @Override
    public QuestionDetailsDto getQuestionDetailsDto() {
        return new MultipleChoiceQuestionDto(this);
    }

    public Integer getCorrectAnswer() {
        return this.getOptions()
                .stream()
                .filter(Option::isCorrect)
                .findAny().orElseThrow(() -> new TutorException(NO_CORRECT_OPTION))
                .getSequence();
    }


    public List<Integer> getCorrectAnswers() {    //  NOVO - mais de um opcao correta
        return this.getOptions()
                .stream()
                .filter(Option::isCorrect)
                .map(Option::getSequence)
                .collect(Collectors.toList());
    }



    @Override
    public void delete() {
        super.delete();
        for (Option option : this.options) {
            option.remove();
        }
        this.options.clear();
    }

    @Override
    public String toString() {
        return "MultipleChoiceQuestion{" +
                "options=" + options +
                '}';
    }

    public static String convertSequenceToLetter(Integer correctAnswer) {
        return correctAnswer != null ? Character.toString('A' + correctAnswer) : "-";
    }

    public static String convertSequenceToLetter(List<Integer> correctAnswers) {   //NOVO
        return correctAnswers != null ? 'A' + correctAnswers.toString() : "-";
    }

    @Override
    public String getAnswerRepresentation(List<Integer> selectedIds) {
        var result = this.options
                .stream()
                .filter(x -> selectedIds.contains(x.getId()))
                .map(x -> convertSequenceToLetter(x.getSequence()))
                .collect(Collectors.joining("|"));
        return !result.isEmpty() ? result : "-";
    }
}
