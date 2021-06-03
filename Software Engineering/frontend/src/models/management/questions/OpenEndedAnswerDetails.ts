import AnswerDetails from '@/models/management/questions/AnswerDetails';
import OpenEndedQuestionDetails from '@/models/management/questions/OpenEndedQuestionDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';

export default class OpenEndedAnswerDetails extends AnswerDetails {
  givenAnswer: string = '';

  constructor(jsonObj?: OpenEndedAnswerDetails) {
    super(QuestionTypes.OpenEnded);
    if (jsonObj) {
      this.givenAnswer = jsonObj.givenAnswer.toString();
    }
  }

  answerRepresentation(question: OpenEndedQuestionDetails): string {
    return (
      'Correct answer: ' +
      question.defaultAnswer +
      '\n' +
      'Your answer: ' +
      this.givenAnswer +
      '\n'
    );
  }

  isCorrect(question: OpenEndedQuestionDetails): boolean {
    return (
      question.defaultAnswer.toLocaleLowerCase() ==
      this.givenAnswer.toLocaleLowerCase()
    );
  }
}
