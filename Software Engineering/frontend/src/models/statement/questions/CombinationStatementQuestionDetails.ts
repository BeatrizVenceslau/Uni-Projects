import StatementQuestionDetails from '@/models/statement/questions/StatementQuestionDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';
import CombinationItemStatement from '@/models/statement/questions/CombinationItemStatement';
import { _ } from 'vue-underscore';

export default class CombinationStatementQuestionDetails extends StatementQuestionDetails {
  language: string = 'Java';
  code: string = '';
  combinationItems1: CombinationItemStatement[] = [];
  combinationItems2: CombinationItemStatement[] = [];

  constructor(jsonObj?: CombinationStatementQuestionDetails) {
    super(QuestionTypes.ItemCombination);
    if (jsonObj) {
      this.language = jsonObj.language || this.language;
      this.code = jsonObj.code || this.code;
      this.combinationItems1 = jsonObj.combinationItems1
        ? jsonObj.combinationItems1.map(
            (item: CombinationItemStatement) => new CombinationItemStatement(item)
          )
        : this.combinationItems1;
    this.combinationItems2 = jsonObj.combinationItems2
        ? jsonObj.combinationItems2.map(
            (item: CombinationItemStatement) => new CombinationItemStatement(item)
          )
        : this.combinationItems2;
    }
  }
}
