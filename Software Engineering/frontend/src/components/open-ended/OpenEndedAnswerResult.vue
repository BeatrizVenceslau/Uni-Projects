<template>
  <div class="open-ended-answer-result">
    <div class="open-ended-answer-student">
      <v-textarea
        :class="{
          correct: answerDetails.isAnswerCorrect(correctAnswerDetails)
        }"
        class="content"
        v-model="answer"
        auto-grow
        data-cy="OpenEndedStudentAnswer"
        label="Your answer"
        disabled="false"
      ></v-textarea>
    </div>
    <div
      v-if="!answerDetails.isAnswerCorrect(correctAnswerDetails)"
      class="open-ended-answer-correct"
    >
      <v-textarea
        class="content"
        v-model="correctAnswerDetails.defaultCorrectAnswer"
        auto-grow
        data-cy="OpenEndedCorrectAnswer"
        label="Correct answer"
        disabled="true"
      ></v-textarea>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model, Emit } from 'vue-property-decorator';

import OpenEndedStatementQuestionDetails from '@/models/statement/questions/OpenEndedStatementQuestionDetails';
import OpenEndedStatementAnswerDetails from '@/models/statement/questions/OpenEndedStatementAnswerDetails';
import OpenEndedStatementCorrectAnswerDetails from '@/models/statement/questions/OpenEndedStatementCorrectAnswerDetails';

@Component
export default class OpenEndedAnswerResult extends Vue {
  @Prop(OpenEndedStatementQuestionDetails)
  readonly questionDetails!: OpenEndedStatementQuestionDetails;
  @Prop(OpenEndedStatementAnswerDetails)
  answerDetails!: OpenEndedStatementAnswerDetails;
  @Prop(OpenEndedStatementCorrectAnswerDetails)
  correctAnswerDetails!: OpenEndedStatementCorrectAnswerDetails;
  answer: string;

  getAnswer(): string {
    let x = this.answerDetails.givenAnswer
      ? this.answerDetails.givenAnswer
      : '';
    if (x === '') {
      return '""';
    } else {
      return x;
    }
  }

  constructor() {
    super();
    this.answer = this.getAnswer();
  }
}
</script>

<style lang="scss">
.open-ended-answer-result {
  & > .open-ended-answer-student > div {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    & .content {
      flex-grow: 1;
      margin: 0 5px;
      max-width: 90%;
    }
    &.correct {
      color: #299455;
    }
    &:not(.correct) {
      color: #cf2323;
    }
  }
}
</style>
