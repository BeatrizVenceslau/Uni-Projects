<template>
  <div class="open-ended-create">
    <v-row>
      <v-textarea
        v-model="sQuestionDetails.defaultAnswer"
        :rules="[v => !!v || 'Default correct answer to question is required']"
        auto-grow
        data-cy="OpenEndedDefaultAnswer"
        label="Default Correct Answer"
        required
        rows="4"
      ></v-textarea>
    </v-row>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Watch, PropSync } from 'vue-property-decorator';
import OpenEndedQuestionDetails from '@/models/management/questions/OpenEndedQuestionDetails';

@Component
export default class OpenEndedCreate extends Vue {
  @PropSync('questionDetails', { type: OpenEndedQuestionDetails })
  sQuestionDetails!: OpenEndedQuestionDetails;
}
</script>

<style scoped></style>
