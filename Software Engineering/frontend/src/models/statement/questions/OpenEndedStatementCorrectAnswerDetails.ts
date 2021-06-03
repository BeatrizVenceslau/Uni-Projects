import StatementCorrectAnswerDetails from '@/models/statement/questions/StatementCorrectAnswerDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';

export default class OpenEndedStatementCorrectAnswerDetails extends StatementCorrectAnswerDetails {
  public defaultCorrectAnswer: string = '';

  constructor(jsonObj?: OpenEndedStatementCorrectAnswerDetails) {
    super(QuestionTypes.OpenEnded);
    if (jsonObj) {
      this.defaultCorrectAnswer = jsonObj.defaultCorrectAnswer;
    }

    if (
      this.defaultCorrectAnswer === null ||
      this.defaultCorrectAnswer === undefined
    ) {
      this.defaultCorrectAnswer = '';
    }
  }
}
