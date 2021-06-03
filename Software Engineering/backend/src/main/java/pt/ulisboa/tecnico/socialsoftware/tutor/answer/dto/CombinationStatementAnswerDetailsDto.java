package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.AnswerDetails;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.CombinationAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CombinationQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.CombinationAnswerItem;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswerItem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CombinationStatementAnswerDetailsDto extends StatementAnswerDetailsDto {
    private List<CombinationGroupStatementAnswerDetailsDto> combinedGroups = new ArrayList<>();

    public CombinationStatementAnswerDetailsDto() {
    }

    public CombinationStatementAnswerDetailsDto(CombinationAnswer questionAnswer) {
        if (questionAnswer.getCombinedGroups() != null) {
            this.combinedGroups = questionAnswer.getCombinedGroups()
                    .stream()
                    .map(CombinationGroupStatementAnswerDetailsDto::new)
                    .collect(Collectors.toList());
            this.combinedGroups.sort(Comparator.comparing(CombinationGroupStatementAnswerDetailsDto::getOrder, Comparator.nullsLast(Comparator.naturalOrder())));
        }
    }

    public List<CombinationGroupStatementAnswerDetailsDto> getCombinedGroups() {
        return combinedGroups;
    }

    public void setCombinedGroups(List<CombinationGroupStatementAnswerDetailsDto> combinedGroups) {
        this.combinedGroups = combinedGroups;
    }

    private CombinationAnswer combinationAnswer;

    @Override
    public AnswerDetails getAnswerDetails(QuestionAnswer questionAnswer) {
        combinationAnswer = new CombinationAnswer(questionAnswer);
        questionAnswer.getQuestion().getQuestionDetails().update(this);
        return combinationAnswer;
    }

    @Override
    public void update(CombinationQuestion question) {
        combinationAnswer.setCombinedGroups(question, this);
    }
 
    @Override
    public boolean emptyAnswer() {
        return combinedGroups == null || combinedGroups.isEmpty();
    }

    @Override
    public QuestionAnswerItem getQuestionAnswerItem(String username, int quizId, StatementAnswerDto statementAnswerDto) {
        return new CombinationAnswerItem(username, quizId, statementAnswerDto, this);
    }

    @Override
    public String toString() {
        return "CombinationStatementAnswerDetailsDto{" +
                "orderedSlots=" + combinedGroups +
                '}';
    }
}
