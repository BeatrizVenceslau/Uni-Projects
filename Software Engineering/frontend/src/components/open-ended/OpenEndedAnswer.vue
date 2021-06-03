<template>
  <div class="open-ended-answer">
    <v-textarea
      class="content"
      placeholder="Type something in here..."
      v-model="answerDetails.givenAnswer"
      auto-grow
      data-cy="OpenEndedDefaultAnswer"
      label="Your answer"
      required
      @blur="updateAnswer()"
    ></v-textarea>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model, Emit } from 'vue-property-decorator';

import OpenEndedStatementQuestionDetails from '@/models/statement/questions/OpenEndedStatementQuestionDetails';
import OpenEndedStatementAnswerDetails from '@/models/statement/questions/OpenEndedStatementAnswerDetails';
// import OpenEndedStatementCorrectAnswerDetails from '@/models/statement/questions/OpenEndedStatementCorrectAnswerDetails';

@Component
export default class OpenEndedAnswer extends Vue {
  @Prop(OpenEndedStatementQuestionDetails)
  readonly questionDetails!: OpenEndedStatementQuestionDetails;
  @Prop(OpenEndedStatementAnswerDetails)
  answerDetails!: OpenEndedStatementAnswerDetails;

  @Emit('question-answer-update')
  updateAnswer() {
    // just emit the damned event
  }
}
</script>
