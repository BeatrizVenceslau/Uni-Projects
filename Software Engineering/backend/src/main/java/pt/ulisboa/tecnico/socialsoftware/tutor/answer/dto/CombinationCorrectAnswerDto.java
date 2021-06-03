package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CombinationQuestion;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CombinationCorrectAnswerDto extends CorrectAnswerDetailsDto {
    private List<CombinationAnswerGroupDto> correctOrder;

    public CombinationCorrectAnswerDto(CombinationQuestion question) {
        this.correctOrder = question.getCombinationGroup()
                .stream()
                .map(CombinationAnswerGroupDto::new)
                .collect(Collectors.toList());
        this.correctOrder.sort(Comparator.comparing(CombinationAnswerGroupDto::getOrder, Comparator.nullsLast(Comparator.naturalOrder())));
    }

    public List<CombinationAnswerGroupDto> getCorrectOrder() {
        return correctOrder;
    }
 
    public void setCorrectOrder(List<CombinationAnswerGroupDto> correctOrder) {
        this.correctOrder = correctOrder;
    }
}