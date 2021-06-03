package pt.ulisboa.tecnico.socialsoftware.tutor.question.webservice    //MODIFICADO


import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.*
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.*


//NOVO
import groovy.json.JsonOutput
import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import groovyx.net.http.HttpResponseException
import org.apache.http.HttpStatus
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DemoUtils
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution


import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser





@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateQuestionTestWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def course
    def courseExecution
    def student
    def teacher
    def questionSubmissionDto
    def response

    def setup() {
        restClient = new RESTClient("http://localhost:" + port)

        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)

        student = new User(USER_2_NAME, USER_2_EMAIL, USER_2_EMAIL,
                User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        student.authUser.setPassword(passwordEncoder.encode(USER_2_PASSWORD))
        student.addCourse(courseExecution)
        courseExecution.addUser(student)
        userRepository.save(student)

        //createdUserLogin(USER_2_EMAIL, USER_2_PASSWORD)


        teacher = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        teacher.addCourse(courseExecution)
        courseExecution.addUser(teacher)
        userRepository.save(teacher)

        //createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)



    }


    def "demo teacher creates a multiple choice question"() {
        
        given: 'a teacher'
        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)


        and: 'a questionDto'
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        
        and: 'four options'
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        optionDto.setRelevance(2)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)

        optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        optionDto.setRelevance(3)
        options.add(optionDto)

        optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)
        
	    questionDto.getQuestionDetailsDto().setOptions(options)
        
        

        when: 'the web service is invoked' 
        response = restClient.post(
                path:"/courses/" + courseExecution.getId() + "/questions",
                body: JsonOutput.toJson(questionDto),
                requestContentType: 'application/json'

        )

        then: 'the the request returns OK'
        response != null
        response.status == 200
        response.data.id != null
        response.data.status == Question.Status.AVAILABLE.name()
        response.data.title == QUESTION_1_TITLE
        response.data.content == QUESTION_1_CONTENT
        


        response.data.questionDetailsDto.options.size() == 4


        response.data.questionDetailsDto.options.get(0).content == OPTION_1_CONTENT
        response.data.questionDetailsDto.options.get(0).correct == true
        response.data.questionDetailsDto.options.get(0).relevance == 2

        response.data.questionDetailsDto.options.get(2).content == OPTION_1_CONTENT
        response.data.questionDetailsDto.options.get(2).correct == true
        response.data.questionDetailsDto.options.get(2).relevance == 3



    }



     def " student is not allowed"(){

        given: 'a student'
        createdUserLogin(USER_2_EMAIL, USER_2_PASSWORD)



        and: 'a questionDto'
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        
        and: 'four options'
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        optionDto.setRelevance(2)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)

        optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        optionDto.setRelevance(3)
        options.add(optionDto)

        optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)
        
	    questionDto.getQuestionDetailsDto().setOptions(options)
        
                
        when: 'the web service is invoked' 
        response = restClient.post(
                path:"/courses/" + courseExecution.getId() + "/questions",
                body: JsonOutput.toJson(questionDto),
                requestContentType: 'application/json'

        )


        then: 'the request returns 403'
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
