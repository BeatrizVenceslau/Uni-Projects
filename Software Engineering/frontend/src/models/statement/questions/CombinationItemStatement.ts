export default class CombinationItemStatementQuestionDetails {
    id: number | null = null;
    content: string = '';
  
    constructor(jsonObj?: CombinationItemStatementQuestionDetails) {
      if (jsonObj) {
        this.id = jsonObj.id || this.id;
        this.content = jsonObj.content || this.content;
      }
    }
  }
  