package pt.ulisboa.tecnico.socialsoftware.tutor.user.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.OpenEndedStatementAnswerDetailsDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementQuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.dto.CourseExecutionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenEndedQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import spock.lang.Unroll

@DataJpaTest
class TeacherCanSeeQuizResults extends SpockTest {
    def student
    def teacher
    def question
    def date
    def courseDto

    def setup() {
        courseDto = new CourseExecutionDto(externalCourseExecution)

        student = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        student.addCourse(externalCourseExecution)
        userRepository.save(student)

        teacher = new User(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        teacher.addCourse(externalCourseExecution)
        userRepository.save(teacher)

        date = DateHandler.now()
    }

    def 'teacher sees students quiz results' () {
        given: 'a solved quiz'
        def quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle("Quiz Title")
        quiz.setType(Quiz.QuizType.PROPOSED.toString())
        quiz.setCourseExecution(externalCourseExecution)
        quiz.setAvailableDate(DateHandler.now())
        quizRepository.save(quiz)

        and: 'a question'
        def question = new Question()
        question.setKey(1)
        question.setTitle(QUESTION_1_TITLE)
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        question.setCourse(externalCourse)
        def questionDetails = new OpenEndedQuestion()
        questionDetails.setDefaultAnswer(OPEN_ENDED_QUESTION_1_ANSWER)
        question.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(question)

        def quizQuestion = new QuizQuestion(quiz, question, 0)
        quizQuestionRepository.save(quizQuestion)

        and: 'a student answer'
        def quizAnswer = new QuizAnswer(student, quiz)
        quizAnswerRepository.save(quizAnswer)
        quizAnswer.setAnswerDate(DateHandler.now())
        quizAnswer.setCompleted(true)

        def statementQuizDto = new StatementQuizDto()
        statementQuizDto.id = quiz.getId()
        statementQuizDto.quizAnswerId = quizAnswer.getId()
        def questionAnswer = new QuestionAnswer(quizAnswer, quizQuestion, 100, 0)
        def statementAnswerDto = new StatementAnswerDto()
        def openEndedAnswerDto = new OpenEndedStatementAnswerDetailsDto()
        openEndedAnswerDto.setAnswer(OPEN_ENDED_QUESTION_1_ANSWER)
        statementAnswerDto.setAnswerDetails(openEndedAnswerDto)
        statementAnswerDto.setSequence(0)
        statementAnswerDto.setTimeTaken(100)
        statementAnswerDto.setQuestionAnswerId(quizAnswer.getQuestionAnswers().get(0).getId())
        statementQuizDto.getAnswers().add(statementAnswerDto)
        def answerDetails = questionAnswer.setAnswerDetails(statementAnswerDto)
        quizRepository.save(quiz)
        quizAnswerRepository.save(quizAnswer)
        questionAnswerRepository.save(questionAnswer)
        answerDetailsRepository.save(answerDetails)

        when: 'quiz results are obtained'
        def quizAnswers = quizService.getQuizAnswers(quiz.getId())

        then: 'returns correct results with 1 empty and 1 correct answer'
        quizAnswers.getQuizAnswers().size() == 1
        def questionAnswers = quizAnswers.getQuizAnswers().get(0)
        questionAnswers.getQuestionAnswers().size() == 2
        def studAnswers = questionAnswers.getQuestionAnswers()
        studAnswers.size() == 2
        def studAnswer1 = studAnswers.get(0)
        studAnswer1.getAnswerDetails().getAnswer() == null
        def studAnswer2 = studAnswers.get(1)
        studAnswer2.getAnswerDetails().getAnswer() == OPEN_ENDED_QUESTION_1_ANSWER
    }

    def 'teacher cannot see results of future quizzes' () {
        given: 'an quiz with future availableDate'
        def quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle("Quiz Title")
        quiz.setType(Quiz.QuizType.PROPOSED.toString())
        quiz.setCourseExecution(externalCourseExecution)
        quiz.setAvailableDate(DateHandler.now().plusDays(2))
        quizRepository.save(quiz)

        and: 'a question'
        def question = new Question()
        question.setKey(1)
        question.setTitle(QUESTION_1_TITLE)
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        question.setCourse(externalCourse)
        def questionDetails = new OpenEndedQuestion()
        questionDetails.setDefaultAnswer(OPEN_ENDED_QUESTION_1_ANSWER)
        question.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(question)

        def quizQuestion = new QuizQuestion(quiz, question, 0)
        quizQuestionRepository.save(quizQuestion)

        when: 'quiz results are obtained'
        def quizAnswers = quizService.getQuizAnswers(quiz.getId())

        then: 'returns nothing, as quiz has no answers'
        quizAnswers.getQuizAnswers().size() == 0
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
