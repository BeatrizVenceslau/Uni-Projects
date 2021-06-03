import StatementQuestionDetails from '@/models/statement/questions/StatementQuestionDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';

export default class OpenEndedStatementQuestionDetails extends StatementQuestionDetails {
  content: string = '';

  constructor(jsonObj?: OpenEndedStatementQuestionDetails) {
    super(QuestionTypes.OpenEnded);
    if (jsonObj) {
      this.content = jsonObj.content
        ? jsonObj.content.toString()
        : this.content;
    }
  }
}
