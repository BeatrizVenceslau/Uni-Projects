describe('Student Walkthrough', () => {
    beforeEach(() => {
      //create quiz
      cy.demoTeacherLogin();
      cy.createQuestion(
        'Question Title 23',
        'Question',
        'Option',
        'Option',
        'Option',
        'Correct'
      );
      cy.createQuestionWith2Correct(
        'Question Title 24',
        'Question',
        'Option',
        'Option',
        'Correct',
        'Correct'
      );
      cy.createQuizzWith2Questions(
        'Quiz Title',
        'Question Title 23',
        'Question Title 24'
      );
      cy.contains('Logout').click();
    });
  


it('student answer quizz and sees results', () =>{
    cy.demoStudentLogin();
    cy.solveQuizz('Quiz Title',2);
    cy.contains('Logout').click();
  });

//afterEach(() => {
 //cy.demoTeacherLogin();
 //cy.deleteQuiz('Quiz Title');
 //cy.contains('Logout').click();

//});
});