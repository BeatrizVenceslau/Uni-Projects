<template>
  <v-card border-variant="light" outlined>
    <v-card-title>
      <span>Answer Item - Group #1</span>
      <v-badge
        :content="item.relations.length"
        :value="item.relations.length"
        color="grey"
        inline
      />
    </v-card-title>
    <v-list>
      <v-list-item>
        <v-list-item-content>
          <v-list-item-title>
            <v-text-field
              v-model="currentText"
              label="Add item relations"
              v-on:keyup.enter="addNewElement"
            />
          </v-list-item-title>
        </v-list-item-content>
        <v-list-item-action>
          <v-btn @click="addNewElement" class="ma-2" icon>
            <v-icon color="grey lighten-1">mdi-plus</v-icon>
          </v-btn>
        </v-list-item-action>
      </v-list-item>
      <v-list-item v-for="(item, index) in item.relations" :key="item.content">
        <v-list-item-content>
          <v-list-item-title>{{ item.content }} </v-list-item-title>
        </v-list-item-content>
        <v-list-item-action>
          <v-btn @click="item.correct = !item.correct" icon>
            <v-icon v-if="!item.correct" color="grey lighten-1"
              >mdi-checkbox-blank-outline
            </v-icon>
            <v-icon v-if="item.correct" color="green lighten-1"
              >mdi-checkbox-marked-outline</v-icon
            >
          </v-btn>
          <v-btn @click="item.relations.splice(index, 1)" icon>
            <v-icon color="red lighten-1">mdi-delete-forever </v-icon>
          </v-btn>
        </v-list-item-action>
      </v-list-item>
    </v-list>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Watch, Prop, PropSync } from 'vue-property-decorator';
import CombinationItem from '@/models/management/questions/CombinationItem';
import Item from '@/models/management/Item';

@Component
export default class CombinationItems extends Vue {
  @PropSync('value', { type: CombinationItem }) item!: CombinationItem;
  currentText: string = ''
  addNewElement() {
    if (this.currentText) {
      const item = new Item();
      item.content = this.currentText;
      this.item.relations.push(item);
      this.currentText = '';
    }
  }
}
</script>
