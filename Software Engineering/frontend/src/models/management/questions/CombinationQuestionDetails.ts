import QuestionDetails from '@/models/management/questions/QuestionDetails';
import CombinationItem from '@/models/management/questions/CombinationItem';
import { QuestionTypes } from '@/services/QuestionHelpers';

export default class CombinationQuestionDetails extends QuestionDetails {
  language: string = 'Java';
  combinationItems1: CombinationItem[] = [];
  combinationItems2: CombinationItem[] = [];

  constructor(jsonObj?: CombinationQuestionDetails) {
    super(QuestionTypes.ItemCombination);
    if (jsonObj) {
        this.language = jsonObj.language || this.language;
        this.combinationItems1 = jsonObj.combinationItems1
            ? jsonObj.combinationItems1.map(
                (item: CombinationItem) => new CombinationItem(item)
            )
            : this.combinationItems1;
        this.combinationItems2 = jsonObj.combinationItems2
            ? jsonObj.combinationItems2.map(
                (item: CombinationItem) => new CombinationItem(item)
            )
            : this.combinationItems2;
    }
  }

  setAsNew(): void {
    this.combinationItems1.forEach(item => {
        item.setAsNew();
    });
    this.combinationItems2.forEach(item => {
        item.setAsNew();
    });
  }
}
