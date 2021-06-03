package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;


import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;

public class MultipleChoiceAnswerDto extends AnswerDetailsDto {
    private OptionDto option;
    private List<OptionDto> options = new ArrayList<>();         //NOVO


    private Boolean multipleSelection = false;                  //NOVO
    private Boolean orderOfRelevance = false;


    public MultipleChoiceAnswerDto() {
    }

    public MultipleChoiceAnswerDto(MultipleChoiceAnswer answer) {
        if (answer.getOption() != null)
            this.option = new OptionDto(answer.getOption());
        if (answer.getOptions() != null){
            multipleSelection = true;
            for (Option option : answer.getOptions()) {
                OptionDto optionDto = new OptionDto(option);
                this.options.add(optionDto);
            }
        }
    }

    public OptionDto getOption() {
        return option;
    }

    public List<OptionDto> getOptions() {
        return options;
    }

    public void setOption(OptionDto option) {
        this.option = option;
    }

    public void setOptions(List<OptionDto> options) {
        this.options = options;
    }

    public void setOrderOfRelevance(Boolean value) {
        this.orderOfRelevance = value;
    }
}
