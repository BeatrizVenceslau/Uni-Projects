import QuestionDetails from '@/models/management/questions/QuestionDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';

export default class OpenEndedQuestionDetails extends QuestionDetails {
  defaultAnswer: string = '';

  constructor(jsonObj?: OpenEndedQuestionDetails) {
    super(QuestionTypes.OpenEnded);
    if (jsonObj) {
      this.defaultAnswer = jsonObj.defaultAnswer || this.defaultAnswer;
    }
  }

  setAsNew(): void {}
}
