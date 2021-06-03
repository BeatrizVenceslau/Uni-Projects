package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import org.apache.commons.lang3.tuple.Pair;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.AnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CombinationAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CombinationCorrectAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.Updator;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.CombinationQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.CombinationGroupDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CombinationStatementAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CombinationStatementQuestionDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementQuestionDetailsDto;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@DiscriminatorValue(Question.QuestionTypes.ITEM_COMBINATION_QUESTION)
public class CombinationQuestion extends QuestionDetails {

    private Languages language;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "questionDetails", fetch = FetchType.LAZY, orphanRemoval = true)
    private final List<CombinationGroup> combinationGroups = new ArrayList<>();


    public CombinationQuestion() {
        super();
    }

    public CombinationQuestion(Question question, CombinationQuestionDto combinationQuestionDto) {
        super(question);
        update(combinationQuestionDto);
    }

    public Languages getLanguage() {
        return language;
    }

    public void setLanguage(Languages language) {
        this.language = language;
    }

    public List<CombinationGroup> getCombinationGroup() {
        return combinationGroups;
    }

    public void setCombinationGroup(List<CombinationGroupDto> group) {
        if (group.isEmpty()) {
            throw new TutorException(TWO_GROUPS_NEEDED);
        }

        if (group.size() != 2 ){
            throw new TutorException(TWO_GROUPS_NEEDED);
        }

        if (group.get(0).getItems().isEmpty()|| group.get(1).getItems().isEmpty()) {
            throw new TutorException(TWO_GROUPS_NEEDED);
        }


        for (CombinationGroupDto combinationGroupDto : group) {
            if (combinationGroupDto.getId() == null) {
                CombinationGroup combinationGroup = new CombinationGroup(combinationGroupDto);
                combinationGroup.setQuestionDetails(this);
                this.combinationGroups.add(combinationGroup);
            } else {
                CombinationGroup item = getCombinationGroup()
                        .stream()
                        .filter(op -> op.getId().equals(combinationGroupDto.getId()))
                        .findFirst()
                        .orElseThrow(() -> new TutorException(NO_CORRECT_OPTION, combinationGroupDto.getId()));
             
                item.setItems(combinationGroupDto.getItems());
            }
        }
    }

    public void update(CombinationQuestionDto questionDetails) {
        setLanguage(questionDetails.getLanguage());
        setCombinationGroup(questionDetails.getCombinationGroup());
    }


    @Override
    public CorrectAnswerDetailsDto getCorrectAnswerDetailsDto() {
        return new CombinationCorrectAnswerDto(this);
    }

    @Override
    public StatementQuestionDetailsDto getStatementQuestionDetailsDto() {
        return new CombinationStatementQuestionDetailsDto(this);
    }

    @Override
    public StatementAnswerDetailsDto getEmptyStatementAnswerDetailsDto() {
        return new CombinationStatementAnswerDetailsDto();
    }

    @Override
    public AnswerDetailsDto getEmptyAnswerDetailsDto() {
        return new CombinationAnswerDto();
    }

    @Override
    public QuestionDetailsDto getQuestionDetailsDto() {
        return new CombinationQuestionDto(this);
    }

    @Override
    public void delete() {
        super.delete();
        for (var groups : this.combinationGroups) {
            groups.delete();
        }
        this.combinationGroups.clear();
    }

    @Override
    public String getCorrectAnswerRepresentation() {
        return this.combinationGroups.stream()
                .map(x -> String.valueOf(x.getSequence() + 1))
                .collect(Collectors.joining(" | "));
    }

    @Override
    public void update(Updator updator) {
        updator.update(this);
    }
 
    @Override
    public void accept(Visitor visitor) {
        visitor.visitQuestionDetails(this);
    }

    public void visitCombinationGroups(Visitor visitor) {
        for (var slot : this.getCombinationGroup()) {
            slot.accept(visitor);
        }
    }

    @Override
    public String getAnswerRepresentation(List<Integer> selectedIds) {
        return selectedIds.stream()
                .map(x -> String.valueOf(this.combinationGroups.stream().filter(co -> co.getId().equals(x)).findAny().get().getSequence() + 1))
                .collect(Collectors.joining(" | "));
    }

}