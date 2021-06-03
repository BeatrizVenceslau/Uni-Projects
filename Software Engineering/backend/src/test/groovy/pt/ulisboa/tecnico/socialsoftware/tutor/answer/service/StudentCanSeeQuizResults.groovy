package pt.ulisboa.tecnico.socialsoftware.tutor.answer.service

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
class StudentCanSeeQuizResults extends SpockTest {
    def user
    def question
    def date
    def quiz
    def courseDto
    def quizQuestion

    def setup() {
        courseDto = new CourseExecutionDto(externalCourseExecution)

        user = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        user.addCourse(externalCourseExecution)
        userRepository.save(user)

        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle("Quiz Title")
        quiz.setType(Quiz.QuizType.PROPOSED.toString())
        quiz.setCourseExecution(externalCourseExecution)
        quiz.setAvailableDate(DateHandler.now())
        quizRepository.save(quiz)

        question = new Question()
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

        quizQuestion = new QuizQuestion(quiz, question, 0)
        quizQuestionRepository.save(quizQuestion)

        date = DateHandler.now()
    }

    @Unroll
    def 'returns results for solved open-ended answer question quiz with 2 question and half score'() {
        given: 'a solved quiz answered by the user'
        def quizAnswer = new QuizAnswer(user, quiz)
        quizAnswerRepository.save(quizAnswer)
        quizAnswer.setAnswerDate(DateHandler.now())
        quizAnswer.setCompleted(true)

        and: 'a open-ended answer'
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
        def quizSolved = answerService.getSolvedQuizzes(user.getId(), courseDto.getCourseExecutionId())

        then: 'returns results with one wrong and one correct answer'
        quizSolved.size() == 1
        def quizSolvedDto = quizSolved.get(0)
        def statementQuiz = quizSolvedDto.getStatementQuiz()
        statementQuiz.getQuestions().size() == 2
        statementQuiz.getAnswers().size() == quizSolvedDto.getCorrectAnswers().size()

        def answerGiven = statementQuiz.getAnswers().get(0)
        answerGiven.getSequence() == 0
        def correctAnswerDto = quizSolvedDto.getCorrectAnswers().get(0)
        correctAnswerDto.getSequence() == 0
        answerGiven.getAnswerDetails().getAnswer() != correctAnswerDto.getCorrectAnswerDetails().getDefaultCorrectAnswer()

        def answerGiven2 = statementQuiz.getAnswers().get(1)
        answerGiven2.getSequence() == 0
        def correctAnswerDto2 = quizSolvedDto.getCorrectAnswers().get(1)
        correctAnswerDto2.getSequence() == 0
        answerGiven2.getAnswerDetails().getAnswer() == correctAnswerDto2.getCorrectAnswerDetails().getDefaultCorrectAnswer()
    }

    @Unroll
    def 'unable to return results for quiz not yet completed'() {
        given: 'a quiz answered by the user, but not yet completed'
        def quizAnswer = new QuizAnswer(user, quiz)
        quizAnswerRepository.save(quizAnswer)

        and: 'a open-ended answer'
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
        def quizSolved = answerService.getSolvedQuizzes(user.getId(), courseDto.getCourseExecutionId())

        then: 'returns nothing, as quiz is not solved'
        !quizAnswer.isCompleted()
        quizSolved.size() == 0
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
