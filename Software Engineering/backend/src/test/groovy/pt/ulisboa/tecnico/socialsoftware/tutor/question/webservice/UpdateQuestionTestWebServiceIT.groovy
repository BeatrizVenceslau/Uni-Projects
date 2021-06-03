package pt.ulisboa.tecnico.socialsoftware.tutor.question.webservice

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User


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
class UpdateQuestionTestWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def course
    def courseExecution
    def student
    def teacher
    def questionSubmissionDto
    def response


    def question
    def optionOK
    def optionKO



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




        given: "create a question"
        question = new Question()
        question.setCourse(course)
        question.setKey(1)
        question.setTitle(QUESTION_1_TITLE)
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        question.setNumberOfAnswers(2)
        question.setNumberOfCorrect(1)


        def questionDetails = new MultipleChoiceQuestion()
        question.setQuestionDetails(questionDetails)
        questionRepository.save(question)

        and: 'two options'
        optionOK = new Option()
        optionOK.setContent(OPTION_1_CONTENT)
        optionOK.setCorrect(true)
        optionOK.setSequence(0)
        optionOK.setQuestionDetails(questionDetails)
        optionRepository.save(optionOK)

        optionKO = new Option()
        optionKO.setContent(OPTION_1_CONTENT)
        optionKO.setCorrect(false)
        optionKO.setSequence(1)
        optionKO.setQuestionDetails(questionDetails)
        optionRepository.save(optionKO)



    }




     def "update question with two options true and with order of relevance"() {

       
        given: 'a teacher'
        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)
        
       
        question.setNumberOfCorrect(2)
        questionRepository.save(question)
        
        def questionDto = new QuestionDto(question)
        questionDto.setTitle(QUESTION_2_TITLE)
        questionDto.setContent(QUESTION_2_CONTENT)
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())


        def optionDto = new OptionDto(optionOK)
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        optionDto.setRelevance(2)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto(optionKO)
        optionDto.setContent(OPTION_2_CONTENT)
        optionDto.setCorrect(true)
        optionDto.setRelevance(3)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        
        

        when: 'the web service is invoked' 
        response = restClient.put(
                path:"/questions/" + question.getId(),
                body: JsonOutput.toJson(questionDto),
                requestContentType: 'application/json'

        )
      

        then: 'the the request returns OK'
        response.status == 200

        and: "the question is changed"
        response.data.id != null

        

        response.data.status == Question.Status.AVAILABLE.name()
        response.data.title == QUESTION_2_TITLE
        response.data.content == QUESTION_2_CONTENT

        
        response.data.numberOfAnswers == 2
        response.data.numberOfCorrect == 2



        and: 'an option is changed'


        response.data.questionDetailsDto.options.size() == 2
        response.data.questionDetailsDto.options.get(0).content == OPTION_1_CONTENT
        response.data.questionDetailsDto.options.get(0).correct == true
        //response.data.questionDetailsDto.options.get(0).relevance == 2


        response.data.questionDetailsDto.options.get(1).content == OPTION_2_CONTENT
        response.data.questionDetailsDto.options.get(1).correct == true
        //response.data.questionDetailsDto.options.get(1).relevance == 3



    }


    def " student is not allowed"(){

        given: 'a student'
        createdUserLogin(USER_2_EMAIL, USER_2_PASSWORD)


        question.setNumberOfCorrect(2)
        questionRepository.save(question)
        
        def questionDto = new QuestionDto(question)
        questionDto.setTitle(QUESTION_2_TITLE)
        questionDto.setContent(QUESTION_2_CONTENT)
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())


        def optionDto = new OptionDto(optionOK)
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        optionDto.setRelevance(2)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto(optionKO)
        optionDto.setContent(OPTION_2_CONTENT)
        optionDto.setCorrect(true)
        optionDto.setRelevance(3)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)




        when: 'the web service is invoked' 
        response = restClient.put(
                path:"/questions/" + question.getId(),
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

      
        optionRepository.delete(optionOK)
        optionRepository.delete(optionKO)
        questionRepository.delete(question)  
    }



}

