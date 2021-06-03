package pt.ulisboa.tecnico.socialsoftware.tutor.answer.webservice

import groovy.json.JsonOutput
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import groovyx.net.http.HttpResponseException
import org.apache.tools.ant.types.resources.comparators.Date
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.OpenEndedStatementAnswerDetailsDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.*
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenEndedQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentCanSeeQuizResultsIT extends SpockTest {
    @LocalServerPort
    private int port

    def user
    def course
    def courseExecution
    def quizQuestion
    def response
    def quiz
    def date

    def setup() {
        restClient = new RESTClient("http://localhost:" + port)

        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)

        user = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL,
                User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        user.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        user.addCourse(courseExecution)
        courseExecution.addUser(user)
        userRepository.save(user)

        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle("Quiz Title")
        quiz.setType(Quiz.QuizType.PROPOSED.toString())
        quiz.setCourseExecution(courseExecution)
        quiz.setAvailableDate(DateHandler.now())
        quiz.setConclusionDate(DateHandler.now().plusDays(2))
        quizRepository.save(quiz)

        def question = new Question()
        question.setCourse(course)
        question.setKey(1)
        question.setTitle(QUESTION_1_TITLE)
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        def questionDetails = new OpenEndedQuestion()
        question.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(question)

        date = DateHandler.now()

        quizQuestion = new QuizQuestion(quiz, question, 0)
        quizQuestionRepository.save(quizQuestion)

        def quizAnswer = new QuizAnswer(user, quiz)
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
    }

    def 'student can see the results for a quiz with a open-ended questions' () {
        given: 'a demo student'
        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        when: 'the webservice is invoked'
        response = restClient.get(
                path: "/executions/" + courseExecution.getId() + "/quizzes/solved",
                requestContentType: "application/json"
        )

        then: 'the request succeeds'
        response != null
        response.status == HttpStatus.SC_OK
        and: 'if it responds with the correct data'
        def solvedQuizzes = response.data
        solvedQuizzes.size() == 1
        def quizSolvedDto = solvedQuizzes.get(0)
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

    def 'student cannot see the results for quizzes in a course he does not audit' () {
        given: 'a demo student'
        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        and: 'a course not audited by the student'
        def course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        def courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)

        when: 'the webservice is invoked'
        response = restClient.get(
                path: "/executions/" + courseExecution.getId() + "/quizzes/solved",
                requestContentType: "application/json"
        )

        then: 'the request fails'
        def exception = thrown(HttpResponseException)
        def response = exception.getResponse()
        response.status == HttpStatus.SC_FORBIDDEN

        cleanup:
        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())
    }

    def cleanup() {
        persistentCourseCleanup()
        courseExecutionRepository.dissociateCourseExecutionUsers(courseExecution.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())
        courseExecution.getUsers().remove(userRepository.findById(user.getId()).get())
        userRepository.deleteById(user.getId())
    }
}