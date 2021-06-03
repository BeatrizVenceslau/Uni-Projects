package pt.ulisboa.tecnico.socialsoftware.tutor.user.webservice

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.OpenEndedStatementAnswerDetailsDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementQuizDto
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
class TeacherCanSeeQuizResultsIT extends SpockTest {
    @LocalServerPort
    private int port

    def student
    def teacher
    def quiz
    def date
    def quizQuestion
    def response
    def course
    def courseExecution

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

        def quizAnswer = new QuizAnswer(student, quiz)
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

    def 'teacher sees student quiz results' () {
        given: 'a demo teacher'
        createdUserLogin(USER_2_EMAIL, USER_2_PASSWORD)

        when: 'the webservice is invoked'
        response = restClient.get(
                path: "/quizzes/" + quiz.getId() + "/answers",
                requestContentType: "application/json"
        )

        then: 'the request succeeds'
        response != null
        response.status == HttpStatus.SC_OK
        and: 'it responds with the correct data'
        def ret = response.data
        ret.size() == 3
        def quizAnswers = ret.get("quizAnswers")
        quizAnswers.size() == 1
        def questionAnswers = quizAnswers["questionAnswers"]
        questionAnswers.size() == 1
        def studAnswers = questionAnswers["answerDetails"]
        studAnswers != null
        def studAnswer = studAnswers["answer"]
        studAnswer.get(0).contains(OPEN_ENDED_QUESTION_1_ANSWER)
    }

    def 'non authorized user cannot see quiz results' () {
        given: 'a demo student'
        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        when: 'the webservice is invoked'
        response = restClient.get(
                path: "/quizzes/" + quiz.getId() + "/answers",
                requestContentType: "application/json"
        )

        then: 'the request fails'
        def exception = thrown(HttpResponseException)
        def response = exception.getResponse()
        response.status == HttpStatus.SC_FORBIDDEN
    }

    def cleanup() {
        persistentCourseCleanup()
        persistentCourseCleanup()
        courseExecutionRepository.dissociateCourseExecutionUsers(courseExecution.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())
        courseExecution.getUsers().remove(userRepository.findById(student.getId()).get())
        courseExecution.getUsers().remove(userRepository.findById(teacher.getId()).get())
        userRepository.deleteById(student.getId())
        userRepository.deleteById(teacher.getId())
    }
}