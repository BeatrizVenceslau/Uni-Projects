export default class Item {
    id: number | null = null;
    content: string = '';
    correct: boolean = false;
  
    constructor(jsonObj?: Item) {
      if (jsonObj) {
        this.id = jsonObj.id;
        this.content = jsonObj.content;
        this.correct = jsonObj.correct;
      }
    }
  }
  