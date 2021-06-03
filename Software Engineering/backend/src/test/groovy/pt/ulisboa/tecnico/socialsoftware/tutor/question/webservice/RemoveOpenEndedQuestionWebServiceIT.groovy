package pt.ulisboa.tecnico.socialsoftware.tutor.question.webservice

import groovyx.net.http.HttpResponseException
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import groovyx.net.http.RESTClient
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OpenEndedQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RemoveOpenEndedQuestionWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def teacher
    def student
    def course
    def courseExecution
    def question
    def response

    def setup() {
        given: 'a rest client'
        restClient = new RESTClient("http://localhost:" + port)

        and: 'an external execution course, where a course already exists'
        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)

        and: 'a student user'
        student = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL,
                User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        student.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        student.addCourse(courseExecution)
        courseExecution.addUser(student)
        userRepository.save(student)

        and: 'a teacher user'
        teacher = new User(USER_2_NAME, USER_2_EMAIL, USER_2_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.EXTERNAL)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_2_PASSWORD))
        teacher.addCourse(courseExecution)
        courseExecution.addUser(teacher)
        userRepository.save(teacher)

        and: 'a open-ended question'
        def questionDto = new QuestionDto()
        questionDto.setKey()
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        def openEndedQuestionDto = new OpenEndedQuestionDto()
        openEndedQuestionDto.setDefaultCorrectAnswer(OPEN_ENDED_QUESTION_1_ANSWER)
        questionDto.setQuestionDetailsDto(openEndedQuestionDto)

        questionService.createQuestion(courseExecution.getId(), questionDto)
        question = questionRepository.findAll().get(0)
    }

    def 'a user with an allowed role removes a question'() {
        createdUserLogin(USER_2_EMAIL, USER_2_PASSWORD)

        when: 'the web service is invoked'
        response = restClient.delete(
                path: '/questions/'+question.getId(),
                requestContentType: 'application/json'
        )

        then: "check the response status"
        response != null
        response.status == HttpStatus.SC_OK

        and: 'course has no questions'
        course.getQuestions().size() == 0
    }

    def 'a user without an allowed role cannot remove a question'() {
        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        when: 'the web service is invoked'
        response = restClient.delete(
                path: '/questions/'+question.getId(),
                requestContentType: 'application/json'
        )

        then: 'the request fails'
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
    }

    def 'a user with an allowed role cannot remove a question that was already removed or doesnt exist'() {
        createdUserLogin(USER_2_EMAIL, USER_2_PASSWORD)
        def id = question.getId()

        when: 'the web service is invoked'
        response = restClient.delete(
                path: '/questions/'+id,
                requestContentType: 'application/json'
        )

        and: 'the web service is invoked again'
        response = restClient.delete(
                path: '/questions/'+id,
                requestContentType: 'application/json'
        )

        then: "request fails"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
    }

    def cleanup() {
        persistentCourseCleanup()

        userRepository.deleteById(student.getId())
        userRepository.deleteById(teacher.getId())

        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())
    }

}