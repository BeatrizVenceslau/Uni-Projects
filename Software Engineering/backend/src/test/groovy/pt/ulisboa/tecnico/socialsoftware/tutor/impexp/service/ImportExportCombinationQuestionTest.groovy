package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CombinationQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.CombinationQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.CombinationGroupDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto

@DataJpaTest
class ImportExportCombinationQuestionsTest extends SpockTest {
    def questionId
    def teacher

    def setup() {
        def questionDto = new QuestionDto()
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new CombinationQuestionDto())

        def combinationQuestionDto = new CombinationQuestionDto()
        combinationQuestionDto.setLanguage(CODE_QUESTION_1_LANGUAGE)

        def image = new ImageDto()
        image.setUrl(IMAGE_1_URL)
        image.setWidth(20)
        questionDto.setImage(image)

        CombinationGroupDto groupDto1 = new CombinationGroupDto()
        groupDto1.setTitle(QUESTION_1_TITLE)
        def itemGroup1 = new ArrayList<OptionDto>()

        CombinationGroupDto groupDto2 = new CombinationGroupDto()
        groupDto2.setTitle(QUESTION_1_TITLE)
        def itemGroup2 = new ArrayList<OptionDto>()

        OptionDto optionDto1 = new OptionDto()
        optionDto1.setContent(OPTION_1_CONTENT)
        optionDto1.setSequence(0)

        OptionDto optionDto2 = new OptionDto()
        optionDto2.setContent(OPTION_2_CONTENT)
        optionDto2.setSequence(0)

        LIST_2_COMBINATION.add(optionDto2)
        optionDto1.setCombination(LIST_2_COMBINATION)
        LIST_1_COMBINATION.add(optionDto1);
        optionDto2.setCombination(LIST_1_COMBINATION)

        itemGroup1.add(optionDto1);
        itemGroup2.add(optionDto2);
        groupDto1.setItems(itemGroup1)
        groupDto2.setItems(itemGroup2)
        groupDto1.setSequence(0)
        groupDto2.setSequence(1)

        combinationQuestionDto.getCombinationGroup().add(groupDto1);
        combinationQuestionDto.getCombinationGroup().add(groupDto2);

        questionDto.setQuestionDetailsDto(combinationQuestionDto)

        questionId = questionService.createQuestion(externalCourse.getId(), questionDto).getId()
    }

    def 'export and import questions to xml'() {
        given: 'a xml with questions'
        def questionsXml = questionService.exportQuestionsToXml()
        print questionsXml
        and: 'a clean database'
        questionService.removeQuestion(questionId)

        when:
        questionService.importQuestionsFromXml(questionsXml)

        then:
        questionRepository.findQuestions(externalCourse.getId()).size() == 1
        def questionResult = questionService.findQuestions(externalCourse.getId()).get(0)
        questionResult.getKey() == null
        questionResult.getTitle() == QUESTION_1_TITLE
        questionResult.getContent() == QUESTION_1_CONTENT
        questionResult.getStatus() == Question.Status.AVAILABLE.name()
        def imageResult = questionResult.getImage()
        imageResult.getWidth() == 20
        imageResult.getUrl() == IMAGE_1_URL

        def combinationDetailsDto = (CombinationQuestionDto) questionResult.getQuestionDetailsDto()
        combinationDetailsDto.getCombinationGroup().size() == 2
        combinationDetailsDto.getLanguage() == CODE_QUESTION_1_LANGUAGE
        def resOption = combinationDetailsDto.getCombinationGroup().get(0).getItems.get(0)
        resOption.getContent() == OPTION_1_CONTENT
    }

    def 'export to latex'() {
        when:
        def questionsLatex = questionService.exportQuestionsToLatex()

        then:
        questionsLatex != null
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}