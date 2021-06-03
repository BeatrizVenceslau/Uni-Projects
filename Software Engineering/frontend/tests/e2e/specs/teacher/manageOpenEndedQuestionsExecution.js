// Copy-pasted from https://www.lipsum.com/ :D

const longTextBlock = `Lorem Ipsum is simply dummy text of the printing and typesetting industry. \
Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, \
when an unknown printer took a galley of type and scrambled it to make a type specimen book. \
It has survived not only five centuries, but also the leap into electronic typesetting, \
remaining essentially unchanged. \
It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, \
and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum. \
Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin \
literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney \
College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, \
and going through the cites of the word in classical literature, discovered the undoubtable source. \
Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of "de Finibus Bonorum et Malorum" \
(The Extremes of Good and Evil) by Cicero, written in 45 BC.`

const longTextBlock2 = `${longTextBlock.substring(0, 366)}\nAs such, we can agree this is why lorem ipsum is absolutely great`

describe('Manage Open-Ended Questions Walk-through', () => {
  function validateQuestion(
    title,
    content,
    defaultCorrectAnswer
  ) {
    cy.get('[data-cy="showQuestionDialog"]')
      .should('be.visible')
      .within($ls => {
        cy.get('.headline').should('contain', title);
        cy.get('span > p').should('contain', content);
        cy.get('.open-ended-view > span > p').should('contain', defaultCorrectAnswer)
      })
  }

  function validateQuestionFull(
    title,
    content,
    defaultCorrectAnswer
  ) {
    cy.get('[data-cy="questionTitleGrid"]')
      .first()
      .click();

    validateQuestion(title, content, defaultCorrectAnswer);

    cy.get('button')
      .contains('close')
      .click();
  }

  before(() => {
    cy.cleanMultipleChoiceQuestionsByName('Cypress Question Example');
    cy.cleanCodeFillInQuestionsByName('Cypress Question Example');
    cy.cleanOpenEndedQuestionsByName('Cypress Question Example');
  });
  after(() => {
    cy.cleanOpenEndedQuestionsByName('Cypress Question Example');
  });

  beforeEach(() => {
    cy.demoTeacherLogin();
    cy.server();
    cy.route('GET', '/courses/*/questions').as('getQuestions');
    cy.route('GET', '/courses/*/topics').as('getTopics');
    cy.get('[data-cy="managementMenuButton"]').click();
    cy.get('[data-cy="questionsTeacherMenuButton"]').click();

    cy.wait('@getQuestions')
      .its('status')
      .should('eq', 200);

    cy.wait('@getTopics')
      .its('status')
      .should('eq', 200);
  });

  afterEach(() => {
    cy.logout();
  });

  it('Creates a new open-ended question', () => {
    cy.get('button')
      .contains('New Question')
      .click();

    cy.get('[data-cy="createOrEditQuestionDialog"]')
      .parent()
      .should('be.visible');

    cy.get('span.headline').should('contain', 'New Question');

    cy.get('[data-cy="questionTitleTextArea"]')
      .type('Cypress Question Example - 01', { force: true });
    cy.get('[data-cy="questionQuestionTextArea"]')
      .type('Cypress Question Example - Content - 01', { force: true });

    cy.get('[data-cy="questionTypeInput"]')
      .type('open_ended', { force: true })
      .click({ force: true });

    cy.wait(1000);

    cy.get('[data-cy="OpenEndedDefaultAnswer"]')
      .type(longTextBlock, { force: true });

    cy.route('POST', '/courses/*/questions/').as('postQuestion');

    cy.get('button')
    .contains('Save')
    .click();

    cy.wait('@postQuestion')
    .its('status')
    .should('eq', 200);

    cy.get('[data-cy="questionTitleGrid"]')
    .first()
    .should('contain', 'Cypress Question Example - 01');

    validateQuestionFull(
      'Cypress Question Example - 01',
      'Cypress Question Example - Content - 01',
      longTextBlock
    );
  })

  it('Can view question (with button)', function() {
    cy.get('tbody tr')
      .first()
      .within($list => {
        cy.get('button')
          .contains('visibility')
          .click();
      });

    cy.wait(1000);

    validateQuestion(
      'Cypress Question Example - 01',
      'Cypress Question Example - Content - 01',
      longTextBlock
    );

    cy.get('button')
      .contains('close')
      .click();
  });

  it('Can view question (with click)', function() {
    cy.get('[data-cy="questionTitleGrid"]')
      .first()
      .click();

    cy.wait(1000); //making sure codemirror loaded

    validateQuestion(
      'Cypress Question Example - 01',
      'Cypress Question Example - Content - 01',
      longTextBlock
    );

    cy.get('button')
      .contains('close')
      .click();
  });

  it('Can update title (with right-click)', function() {
    cy.route('PUT', '/questions/*').as('updateQuestion');

    cy.get('[data-cy="questionTitleGrid"]')
      .first()
      .rightclick();

    cy.wait(1000); //making sure codemirror loaded

    cy.get('[data-cy="createOrEditQuestionDialog"]')
      .parent()
      .should('be.visible')
      .within($list => {
        cy.get('span.headline').should('contain', 'Edit Question');

        cy.get('[data-cy="questionTitleTextArea"]')
          .clear({ force: true })
          .type('Cypress Question Example - 01 - Edited', { force: true });

        cy.get('button')
          .contains('Save')
          .click();
      });

    cy.wait('@updateQuestion')
      .its('status')
      .should('eq', 200);

    cy.get('[data-cy="questionTitleGrid"]')
      .first()
      .should('contain', 'Cypress Question Example - 01 - Edited');

    validateQuestionFull(
      'Cypress Question Example - 01 - Edited',
      'Cypress Question Example - Content - 01',
      longTextBlock
    );
  });

  it('Can update content (with button)', function() {
    cy.route('PUT', '/questions/*').as('updateQuestion');

    cy.get('tbody tr')
      .first()
      .within($list => {
        cy.get('button')
          .contains('edit')
          .click();
      });

    cy.wait(1000); //making sure codemirror loaded

    cy.get('[data-cy="createOrEditQuestionDialog"]')
      .parent()
      .should('be.visible')
      .within($list => {
        cy.get('span.headline').should('contain', 'Edit Question');

        cy.get('[data-cy="questionQuestionTextArea"]')
          .clear({ force: true })
          .type('Cypress New Content For Question!', { force: true });

        cy.get('button')
          .contains('Save')
          .click();
      });

    cy.wait('@updateQuestion')
      .its('status')
      .should('eq', 200);

    validateQuestionFull(
      'Cypress Question Example - 01 - Edited',
      'Cypress New Content For Question!',
      longTextBlock
    );
  });

  it('Can update the default correct answer', function() {
    const newTxtBlock = `${longTextBlock.substring(0, 366)}\nAs such, we can agree this is why lorem ipsum is absolutely great`
    cy.route('PUT', '/questions/*').as('updateQuestion');

    cy.get('tbody tr')
      .first()
      .within($list => {
        cy.get('button')
          .contains('edit')
          .click();
      });

    cy.wait(1000); //making sure codemirror loaded

    cy.get('[data-cy="createOrEditQuestionDialog"]')
      .parent()
      .should('be.visible')
      .within($list => {
        cy.get('span.headline').should('contain', 'Edit Question');

        cy.get('[data-cy="OpenEndedDefaultAnswer"]')
            .clear({ force: true })
            .type(longTextBlock2, { force: true });

        cy.get('button')
          .contains('Save')
          .click();
      });

    cy.wait('@updateQuestion')
      .its('status')
      .should('eq', 200);

    validateQuestionFull(
      'Cypress Question Example - 01 - Edited',
      'Cypress New Content For Question!',
      longTextBlock2
    );
  });

  it('Can duplicate the question', function() {
    cy.get('tbody tr')
      .first()
      .within($list => {
        cy.get('button')
          .contains('cached')
          .click();
      });

    cy.wait(1000); //making sure codemirror loaded

    cy.get('[data-cy="createOrEditQuestionDialog"]')
      .parent()
      .should('be.visible');

    cy.get('span.headline').should('contain', 'New Question');

    cy.get('[data-cy="questionTitleTextArea"]')
      .should('have.value', 'Cypress Question Example - 01 - Edited')
      .type('{end} - DUP', { force: true });
    cy.get('[data-cy="questionQuestionTextArea"]').should(
      'have.value',
      'Cypress New Content For Question!'
    );

    cy.route('POST', '/courses/*/questions/').as('postQuestion');

    cy.wait(1000);

    cy.get('button')
      .contains('Save')
      .click();

    cy.wait('@postQuestion')
      .its('status')
      .should('eq', 200);

    cy.get('[data-cy="questionTitleGrid"]')
      .first()
      .should('contain', 'Cypress Question Example - 01 - Edited - DUP');

    validateQuestionFull(
      'Cypress Question Example - 01 - Edited - DUP',
      'Cypress New Content For Question!',
      longTextBlock2
    );
  });

  it('Can delete the created question', function() {
    cy.route('DELETE', '/questions/*').as('deleteQuestion');
    cy.get('tbody tr')
      .first()
      .within($list => {
        cy.get('button')
          .contains('delete')
          .click();
      });

    cy.wait('@deleteQuestion')
      .its('status')
      .should('eq', 200);
  });

  it('Cannot create a new open-ended question with a whitespace-only default answer', () => {
    cy.get('button')
      .contains('New Question')
      .click();

    cy.get('[data-cy="createOrEditQuestionDialog"]')
      .parent()
      .should('be.visible');

    cy.get('span.headline').should('contain', 'New Question');

    cy.get('[data-cy="questionTitleTextArea"]')
      .type('Cypress Question Example - 01', { force: true });
    cy.get('[data-cy="questionQuestionTextArea"]')
      .type('Cypress Question Example - Content - 01', { force: true });

    cy.get('[data-cy="questionTypeInput"]')
      .type('open_ended', { force: true })
      .click({ force: true });

    cy.wait(1000);

    cy.get('[data-cy="OpenEndedDefaultAnswer"]')
      .type('                           \n\n\                \n            \n  \n                             \n ', { force: true });

    cy.route('POST', '/courses/*/questions/').as('postQuestion');

    cy.get('button')
      .contains('Save')
      .click();

    cy.wait('@postQuestion')
      .its('status')
      .should('eq', 400);

    cy.closeErrorMessage()
  })
});
