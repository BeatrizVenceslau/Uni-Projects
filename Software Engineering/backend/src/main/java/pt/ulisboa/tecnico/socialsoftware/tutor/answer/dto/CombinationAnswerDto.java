package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.CombinationAnswer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CombinationAnswerDto extends AnswerDetailsDto {
    private List<CombinationAnswerGroupDto> combinedGroups = new ArrayList<>();

    public CombinationAnswerDto() {
    }

    public CombinationAnswerDto(CombinationAnswer answer) {
        if (answer.getCombinedGroups() != null) {
            this.combinedGroups = answer.getCombinedGroups().stream().map(CombinationAnswerGroupDto::new).collect(Collectors.toList());
            this.combinedGroups.sort(Comparator.comparing(CombinationAnswerGroupDto::getOrder, Comparator.nullsLast(Comparator.naturalOrder())));
        }
    }
 
    public List<CombinationAnswerGroupDto> getCombinedGroups() {
        return combinedGroups;
    }

    public void setCombinedGroups(List<CombinationAnswerGroupDto> combinedGroups) {
        this.combinedGroups = combinedGroups;
    }
}
