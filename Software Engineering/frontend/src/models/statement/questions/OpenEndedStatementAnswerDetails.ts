import StatementAnswerDetails from '@/models/statement/questions/StatementAnswerDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';
import OpenEndedStatementCorrectAnswerDetails from '@/models/statement/questions/OpenEndedStatementCorrectAnswerDetails';

export default class OpenEndedStatementAnswerDetails extends StatementAnswerDetails {
  public givenAnswer: string = '';

  constructor(jsonObj?: OpenEndedStatementAnswerDetails) {
    super(QuestionTypes.OpenEnded);
    if (jsonObj) {
      this.givenAnswer = jsonObj.givenAnswer;
    }
  }

  isAnswerCorrect(
    correctAnswerDetails: OpenEndedStatementCorrectAnswerDetails
  ): boolean {
    return (
      this.isQuestionAnswered() &&
      correctAnswerDetails.defaultCorrectAnswer.toLocaleLowerCase() ==
        this.givenAnswer.toLocaleLowerCase()
    );
  }

  isQuestionAnswered(): boolean {
    return (
      this.givenAnswer !== null &&
      this.givenAnswer !== undefined &&
      this.givenAnswer != ''
    );
  }
}
