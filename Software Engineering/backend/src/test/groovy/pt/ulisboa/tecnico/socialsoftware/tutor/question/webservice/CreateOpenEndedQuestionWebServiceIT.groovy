package pt.ulisboa.tecnico.socialsoftware.tutor.question.webservice

import com.fasterxml.jackson.databind.ObjectMapper
import groovyx.net.http.RESTClient
import groovyx.net.http.HttpResponseException
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OpenEndedQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateOpenEndedQuestionWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def course
    def courseExecution
    def student
    def teacher
    def response
    def jsonWriter

    def setup() {
        restClient = new RESTClient("http://localhost:" + port)
        jsonWriter = new ObjectMapper().writer().withDefaultPrettyPrinter()

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

        teacher = new User(USER_2_NAME, USER_2_EMAIL, USER_2_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_2_PASSWORD))
        teacher.addCourse(courseExecution)
        courseExecution.addUser(teacher)
        userRepository.save(teacher)
    }

    def "a teacher creates an open-ended question" () {
        given: "a demo teacher"
        createdUserLogin(USER_2_EMAIL, USER_2_PASSWORD)

        and: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        def oeqDto = new OpenEndedQuestionDto()
        oeqDto.setDefaultCorrectAnswer(OPEN_ENDED_QUESTION_1_ANSWER)

        questionDto.setQuestionDetailsDto(oeqDto)

        when: "the endpoint is invoked"
        response = restClient.post(
            path: "/courses/" + courseExecution.getId() + "/questions",
            body: jsonWriter.writeValueAsString(questionDto),
            requestContentType: "application/json"
        )

        then: "check the response status - should be OK"
        response != null
        response.status == HttpStatus.SC_OK
        and: "if it is OK, then the question is sent back"
        def question = response.data
        question.id != null
        question.title == questionDto.getTitle()
        question.content == questionDto.getContent()
        question.status == Question.Status.AVAILABLE.name()
        question.questionDetailsDto.type == Question.QuestionTypes.OPEN_ENDED_QUESTION
        question.questionDetailsDto.defaultAnswer == oeqDto.getDefaultCorrectAnswer()
    }

    def "cannot create an open-ended question with a empty or whitespace-only default correct answer" () {
        given: "a demo teacher"
        createdUserLogin(USER_2_EMAIL, USER_2_PASSWORD)

        and: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        def oeqDto = new OpenEndedQuestionDto()
        oeqDto.setDefaultCorrectAnswer("\t")

        questionDto.setQuestionDetailsDto(oeqDto)

        when: "the endpoint is invoked"
        response = restClient.post(
            path: "/courses/" + courseExecution.getId() + "/questions",
            body: jsonWriter.writeValueAsString(questionDto),
            requestContentType: "application/json"
        )

        then: "the request should fail"
        def exception = thrown(HttpResponseException)
        def response = exception.getResponse()
        response.status == HttpStatus.SC_BAD_REQUEST
        and: "if it fails with error 400, it should tell the default correct answer needs to exist"
        response.data.message == ErrorMessage.NO_CORRECT_ANSWER.label
    }

    def "a student cannot create a question" () {
        given: "a demo student"
        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        and: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        def oeqDto = new OpenEndedQuestionDto()
        oeqDto.setDefaultCorrectAnswer(OPEN_ENDED_QUESTION_1_ANSWER)

        questionDto.setQuestionDetailsDto(oeqDto)

        when: "the endpoint is invoked"
        response = restClient.post(
            path: "/courses/" + courseExecution.getId() + "/questions",
            body: jsonWriter.writeValueAsString(questionDto),
            requestContentType: "application/json"
        )

        then: "the request should be denied"
        def exception = thrown(HttpResponseException)
        def response = exception.getResponse()
        response.status == HttpStatus.SC_FORBIDDEN
        response.data.message == ErrorMessage.ACCESS_DENIED.label
    }

    def cleanup() {
        persistentCourseCleanup()

        userRepository.deleteById(student.getId())
        userRepository.deleteById(teacher.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())
    }
}
