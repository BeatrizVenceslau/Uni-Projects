package pt.ulisboa.tecnico.socialsoftware.tutor.answer.webservice

import groovy.json.JsonOutput
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import groovyx.net.http.HttpResponseException
import org.apache.tools.ant.types.resources.comparators.Date
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.OpenEndedAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.OpenEndedCorrectAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.OpenEndedStatementAnswerDetailsDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementQuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenEndedQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConcludeQuizWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def student
    def course
    def courseExecution
    def quizQuestion
    def response
    def quiz
    def quizAnswer
    def date
    def statementQuizDto

    def setup() {
        restClient = new RESTClient("http://localhost:" + port)

        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)

        student = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL,
                User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        student.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        student.addCourse(courseExecution)
        courseExecution.addUser(student)
        userRepository.save(student)

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
        questionDetails.setDefaultAnswer(OPEN_ENDED_QUESTION_1_ANSWER)
        question.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(question)

        date = DateHandler.now()

        quizQuestion = new QuizQuestion(quiz, question, 0)
        quizQuestionRepository.save(quizQuestion)

        quizAnswer = new QuizAnswer(student, quiz)
        quizAnswerRepository.save(quizAnswer)

        statementQuizDto = new StatementQuizDto()
        statementQuizDto.id = quiz.getId()
        statementQuizDto.quizAnswerId = quizAnswer.getId()
        def statementAnswerDto = new StatementAnswerDto()
        def openEndedAnswerDto = new OpenEndedStatementAnswerDetailsDto()
        openEndedAnswerDto.setAnswer(OPEN_ENDED_QUESTION_1_ANSWER)
        statementAnswerDto.setAnswerDetails(openEndedAnswerDto)
        statementAnswerDto.setSequence(0)
        statementAnswerDto.setTimeTaken(100)
        statementAnswerDto.setQuestionAnswerId(quizAnswer.getQuestionAnswers().get(0).getId())
        statementQuizDto.getAnswers().add(statementAnswerDto)
    }

    def 'student can answer a quiz with open-ended questions' () {
        given: 'a demo student'
        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        when: 'the endpoint is invoked'
        response = restClient.post(
                path: "/quizzes/" + quiz.getId() + "/conclude",
                body: JsonOutput.toJson(statementQuizDto),
                requestContentType: "application/json"
        )

        then: 'the request succeeds'
        response != null
        response.status == HttpStatus.SC_OK
        and: 'if it succeeds, it sends the correct data back'
        List<CorrectAnswerDto> answers = response.data
        answers.size() == 1
    }

    def 'teacher cannot answer a quiz with open-ended questions' () {
        given: 'a demo teacher'
        demoTeacherLogin()

        when: 'the endpoint is invoked'
        response = restClient.post(
                path: "/quizzes/" + quiz.getId() + "/conclude",
                body: JsonOutput.toJson(statementQuizDto),
                requestContentType: "application/json"
        )

        then: 'action is forbidden'
        def exception = thrown(HttpResponseException)
        def response = exception.getResponse()
        response.status == HttpStatus.SC_FORBIDDEN
    }

    def cleanup() {
        persistentCourseCleanup()
        courseExecutionRepository.dissociateCourseExecutionUsers(courseExecution.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())

        courseExecution.getUsers().remove(userRepository.findById(student.getId()).get())
        userRepository.deleteById(student.getId())
    }

}