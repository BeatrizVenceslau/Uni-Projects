package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.webservice

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
class ExportOpenEndedQuestionsWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def course
    def courseExecution
    def student
    def teacher
    def response
    def mapper

    def setup() {
        given: 'a rest client'
        restClient = new RESTClient("http://localhost:" + port)

        and: 'an external execution course, where a course already exists'
        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)

        and: 'a student user'
        student = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.EXTERNAL)
        student.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        student.addCourse(courseExecution)
        courseExecution.addUser(student)
        userRepository.save(student)

        and: 'a teacher user'
        teacher = new User(USER_2_NAME, USER_2_EMAIL, USER_2_EMAIL, User.Role.TEACHER, false, AuthUser.Type.EXTERNAL)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_2_PASSWORD))
        teacher.addCourse(courseExecution)
        courseExecution.addUser(teacher)
        userRepository.save(teacher)

        and: 'a open-ended question'
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        def openEndedQuestionDto = new OpenEndedQuestionDto()
        openEndedQuestionDto.setDefaultCorrectAnswer(OPEN_ENDED_QUESTION_1_ANSWER)
        questionDto.setQuestionDetailsDto(openEndedQuestionDto)

        questionService.createQuestion(courseExecution.getId(), questionDto)
    }

    def 'a user with a not allowed role cannot export questions'() {
        given: "a demo student"
        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        restClient.handler.success = {buffer, input ->[response:buffer, reader:input]}
        restClient.handler.failure = {buffer, input ->[response:buffer, reader:input]}

        when: 'the webservice is invoked'
        mapper = restClient.get(
                path: '/courses/'+courseExecution.getId()+'/questions/export',
                requestContentType: 'application/json'
        )

        then: 'the request fails with 403'
        assert mapper['response'].status == HttpStatus.SC_FORBIDDEN
    }

    def 'a user with an allowed role exports a question'() {
        given: "a demo teacher"
        createdUserLogin(USER_2_EMAIL, USER_2_PASSWORD)

        restClient.handler.success = {buffer, input ->[response:buffer, reader:input]}
        restClient.handler.failure = {buffer, input ->[response:buffer, reader:input]}

        when: 'the webservice is invoked'
        mapper = restClient.get(
                path: '/courses/'+courseExecution.getId()+'/questions/export',
                requestContentType: 'application/json'
        )

        then: 'check the response status - should be ok'
        assert mapper['response'].status != null
        assert mapper['response'].status == HttpStatus.SC_OK
    }

    def 'a user with an allowed role exports 2 question'() {
        given: "a demo teacher"
        createdUserLogin(USER_2_EMAIL, USER_2_PASSWORD)

        and: 'another open-ended question'
        def questionDto = new QuestionDto()
        questionDto.setKey(2)
        questionDto.setTitle(QUESTION_2_TITLE)
        questionDto.setContent(QUESTION_2_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        def openEndedQuestionDto = new OpenEndedQuestionDto()
        openEndedQuestionDto.setDefaultCorrectAnswer(OPEN_ENDED_QUESTION_2_ANSWER)
        questionDto.setQuestionDetailsDto(openEndedQuestionDto)

        questionService.createQuestion(courseExecution.getId(), questionDto)

        restClient.handler.success = {buffer, input ->[response:buffer, reader:input]}
        restClient.handler.failure = {buffer, input ->[response:buffer, reader:input]}

        when: 'the webservice is invoked'
        mapper = restClient.get(
                path: '/courses/'+courseExecution.getId()+'/questions/export',
                requestContentType: 'application/json'
        )

        then: 'check the response status - should be ok'
        assert mapper['response'].status != null
        assert mapper['response'].status == HttpStatus.SC_OK
    }

    def cleanup() {
        persistentCourseCleanup()

        userRepository.deleteById(student.getId())
        userRepository.deleteById(teacher.getId())

        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())
    }
}