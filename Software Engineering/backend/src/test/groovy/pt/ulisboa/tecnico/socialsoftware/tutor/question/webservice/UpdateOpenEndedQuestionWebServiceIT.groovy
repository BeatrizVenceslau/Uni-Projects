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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenEndedQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OpenEndedQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UpdateOpenEndedQuestionWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def course
    def courseExecution
    def student
    def teacher
    def question
    def currentQuestionDto
    def response
    def jsonWriter

    def setup() {
        restClient = new RESTClient("http://localhost:" + port)
        jsonWriter = new ObjectMapper().writer().withDefaultPrettyPrinter()

        given: "a course"
        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)

        and: "users"
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

        and: "an image"
        def image = new Image()
        image.setUrl(IMAGE_1_URL)
        image.setWidth(20)
        imageRepository.save(image)

        given: "an open-ended question"
        question = new Question()
        question.setCourse(externalCourse)
        question.setKey(1)
        question.setTitle(QUESTION_1_TITLE)
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        question.setImage(image)
        def questionDetails = new OpenEndedQuestion()
        questionDetails.setDefaultAnswer(OPEN_ENDED_QUESTION_1_ANSWER)
        question.setQuestionDetails(questionDetails)
        currentQuestionDto = questionService.createQuestion(courseExecution.getId(), new QuestionDto(question))
    }

    def "Updating a question as a teacher" () {
        given: "a demo teacher"
        createdUserLogin(USER_2_EMAIL, USER_2_PASSWORD)

        and: "a questionDto (to update the question)"
        def questionDto = new QuestionDto()
        questionDto.setTitle(QUESTION_2_TITLE)
        questionDto.setContent(QUESTION_2_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        def oeqDto = new OpenEndedQuestionDto()
        oeqDto.setDefaultCorrectAnswer(OPEN_ENDED_QUESTION_2_ANSWER)

        questionDto.setQuestionDetailsDto(oeqDto)

        when: "the endpoint is invoked"
        response = restClient.put(
                path: "/questions/" + currentQuestionDto.getId(),
                body: jsonWriter.writeValueAsString(questionDto),
                requestContentType: "application/json"
        )

        then: "the request succeeds"
        response.status == HttpStatus.SC_OK
        and: "if it succeeds, it sends the updated question back"
        def newQuestion = response.data

        newQuestion.id == currentQuestionDto.getId()
        newQuestion.title == questionDto.getTitle()
        newQuestion.content == questionDto.getContent()
        newQuestion.status == Question.Status.AVAILABLE.name()
        newQuestion.questionDetailsDto.type == Question.QuestionTypes.OPEN_ENDED_QUESTION
        newQuestion.questionDetailsDto.defaultCorrectAnswer == oeqDto.getDefaultCorrectAnswer()
    }

    def "Cannot update an open-ended question to contain an empty default answer" () {
        given: "a demo teacher"
        createdUserLogin(USER_2_EMAIL, USER_2_PASSWORD)

        and: "a questionDto (to update the question)"
        def questionDto = new QuestionDto()
        questionDto.setTitle(QUESTION_2_TITLE)
        questionDto.setContent(QUESTION_2_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        def oeqDto = new OpenEndedQuestionDto()
        oeqDto.setDefaultCorrectAnswer("\t\t \t \n\t  \n   \t\n\r\n")

        questionDto.setQuestionDetailsDto(oeqDto)

        when: "the endpoint is invoked"
        response = restClient.put(
                path: "/questions/" + currentQuestionDto.getId(),
                body: jsonWriter.writeValueAsString(questionDto),
                requestContentType: "application/json"
        )

        then: "the request fails"
        def exception = thrown(HttpResponseException)
        def response = exception.getResponse()
        response.status == HttpStatus.SC_BAD_REQUEST

        and: "it is explained that there needs to be an answer that is not just-whitespace"
        response.data.message == ErrorMessage.NO_CORRECT_ANSWER.label
    }

    def "Students cannot update questions" () {
        given: "a demo student"
        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        and: "a questionDto (to update the question)"
        def questionDto = new QuestionDto()
        questionDto.setTitle(QUESTION_3_TITLE)
        questionDto.setContent(QUESTION_3_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        def oeqDto = new OpenEndedQuestionDto()
        oeqDto.setDefaultCorrectAnswer("")

        questionDto.setQuestionDetailsDto(oeqDto)

        when: "the endpoint is invoked"
        response = restClient.put(
                path: "/questions/" + currentQuestionDto.getId(),
                body: jsonWriter.writeValueAsString(questionDto),
                requestContentType: "application/json"
        )

        then: "the request is denied"
        def exception = thrown(HttpResponseException)
        def response = exception.getResponse()
        response.status == HttpStatus.SC_FORBIDDEN

        and: "it is explained that the student is not allowed to do this change"
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