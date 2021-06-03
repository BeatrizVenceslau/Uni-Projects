import Item from '@/models/management/Item';

export default class CombinationItem {
    id: number | null = null;
    group: number | null = null;
    content: string = '';
    relations: Item[] = [];
  
    constructor(jsonObj?: CombinationItem) {
      if (jsonObj) {
        this.id = jsonObj.id;
        this.group = jsonObj.group;
        this.content = jsonObj.content;
        this.relations = jsonObj.relations
        ? jsonObj.relations.map((item: Item) => new Item(item))
        : this.relations;
      }
    }
  
    setAsNew(): void {
      this.id = null;
      this.group = null;
      this.relations.forEach(item => {
        item.id = null;
      });
    }
  }
  