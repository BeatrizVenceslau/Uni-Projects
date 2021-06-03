package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.webservice

import groovy.json.JsonOutput
import groovyx.net.http.RESTClient
import groovyx.net.http.HttpResponseException
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.*
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.CombinationQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.CombinationGroupDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExportCombinationQuestionWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def course
    def courseExecution
    def response
    def student
    def teacher

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

        teacher = new User(USER_2_NAME, USER_2_EMAIL, USER_2_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_2_PASSWORD))
        teacher.addCourse(courseExecution)
        courseExecution.addUser(teacher)
        userRepository.save(teacher)
    }

    def "export combination question by teacher"() {
        given: "a demo teacher"
        createdUserLogin(USER_2_EMAIL, USER_2_PASSWORD)

        and: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        def combinationQuestionDto = new CombinationQuestionDto()
        combinationQuestionDto.setLanguage(CODE_QUESTION_1_LANGUAGE)

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

        when: "the webservice is invoked"
        response = restClient.get(
                path: '/courses/' + courseExecution.getId() + '/questions/export',
                requestContentType: 'application/json'
        )

        then: "check the response status"
        response != null
        response.status == 200
        and: "if it responds with the correct question exported"
        def question = response.data
        question.id != null
        question.title == questionDto.getTitle()
        question.content == questionDto.getContent()
        question.status == Question.Status.AVAILABLE.name()
    }

    def "cannot export combination question by student"() {
        given: "a demo student"
        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        and: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        def combinationQuestionDto = new CombinationQuestionDto()
        combinationQuestionDto.setLanguage(CODE_QUESTION_1_LANGUAGE)

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

        when: "the webservice is invoked"
        response = restClient.get(
                path: '/courses/' + courseExecution.getId() + '/questions/export',
                requestContentType: 'application/json'
        )

        then: 'the request fails'
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
