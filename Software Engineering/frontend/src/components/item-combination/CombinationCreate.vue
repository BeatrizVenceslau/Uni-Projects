<template>
  <div>
    <v-select
      :items="languages"
      v-model="sQuestionDetails.language"
      label="Language"
      :disabled="readonlyEdit"
    />
    <div class="combination-create">
      <v-card-actions>
        <v-spacer />
        <v-tooltip top>
          <template v-slot:activator="{ on }">
            <v-btn color="primary" small @click="Dropdownify" v-on="on">
              Answer Item Group 1
            </v-btn>
          </template>
          <span>
            Click here to create items from group 2 to match the item.
          </span>
        </v-tooltip>
      </v-card-actions>

      <BaseCodeEditor
        ref="codeEditor"
        :code.sync="sQuestionDetails.code"
        :language.sync="sQuestionDetails.language"
      />

      <CombinationItems
        v-for="(item, index) in sQuestionDetails.combinationItems"
        :key="index"
        :item="item"
        v-model="sQuestionDetails.combinationItems[index]"
      />
      <!-- v-on:change="handleInput" -->
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch, Prop, PropSync } from 'vue-property-decorator';
import CombinationQuestionDetails from '@/models/management/questions/CombinationQuestionDetails';
import Question from '@/models/management/Question';
import CombinationItems from '@/components/item-combination/CombinationItems.vue';
import Item from '@/models/management/Item';
import CombinationItem from '@/models/management/questions/CombinationItem';
import BaseCodeEditor from '@/components/BaseCodeEditor.vue';

@Component({
  components: {
    BaseCodeEditor,
    CombinationItems
  }
})
export default class CombinationQuestionEdit extends Vue {
  @PropSync('questionDetails', { type: CombinationQuestionDetails })
  sQuestionDetails!: CombinationQuestionDetails;
  @Prop({ default: true }) readonly readonlyEdit!: boolean;
  counter: number = 1;

  get languages(): String[] {
    return BaseCodeEditor.availableLanguages;
  }

  get baseCodeEditorRef(): BaseCodeEditor {
    return this.$refs.codeEditor as BaseCodeEditor;
  }
  Dropdownify() {
    const content = this.baseCodeEditorRef.codemirror.getSelection();
    if (content) {
      const item = new Item();
      item.correct = true;
      item.content = content;
      const combinationItem = new CombinationItem();
      combinationItem.relations = [item];
      this.sQuestionDetails.combinationItems.push(combinationItem);
      this.baseCodeEditorRef.codemirror.replaceSelection(
        '{{item-' + this.counter + '}}'
      );
      this.counter++;
    }
  }
}
</script>

<style>
.cm-custom-drop-down {
  background: #ffa014;
  color: white;
  font-size: x-small;
  padding: 4px 2px 4px 2px;
  border-radius: 5px;
  font-weight: bolder;
  height: 16px;
}
.code-create {
  text-align: left;
}
.CodeMirror-linenumber.CodeMirror-gutter-elt {
  left: 0;
}
</style>
