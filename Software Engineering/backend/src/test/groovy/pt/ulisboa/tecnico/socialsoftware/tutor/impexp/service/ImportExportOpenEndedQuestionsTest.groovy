package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.service

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.*
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;

@DataJpaTest
class ImportExportOpenEndedQuestionsTest extends SpockTest {
    Integer questionId

    def setup() {
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        def image = new ImageDto()
        image.setUrl(IMAGE_1_URL)
        image.setWidth(20)
        questionDto.setImage(image)

        def openQuestionDto = new OpenEndedQuestionDto()
        openQuestionDto.setDefaultCorrectAnswer(OPEN_ENDED_QUESTION_1_ANSWER)

        questionDto.setQuestionDetailsDto(openQuestionDto)

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

        def openEndedDetailsDto = (OpenEndedQuestionDto) questionResult.getQuestionDetailsDto()
        openEndedDetailsDto.getDefaultCorrectAnswer() == OPEN_ENDED_QUESTION_1_ANSWER
    }

    def 'cannot import open-ended questions from xml where the default correct answer is blank'() {
        given: 'a xml with questions'
        def questionsXml = new String(Files.readAllBytes(Paths.get(OPEN_ENDED_BAD_XML)), StandardCharsets.UTF_8)
        print questionsXml        

        and: 'a clean database'
        questionService.removeQuestion(questionId)

        when:
        questionService.importQuestionsFromXml(questionsXml)

        then: 'exception is thrown'
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.NO_CORRECT_ANSWER
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
