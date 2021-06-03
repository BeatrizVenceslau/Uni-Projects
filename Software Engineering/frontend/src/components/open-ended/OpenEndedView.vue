<template>
  <div class="open-ended-view">
    <span
      v-html="
        convertMarkDown(
          '<b>Default correct answer -> </b>' + defaultCorrectAnswer()
        )
      "
    />
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';
import OpenEndedQuestionDetails from '@/models/management/questions/OpenEndedQuestionDetails';
import OpenEndedAnswerDetails from '@/models/management/questions/OpenEndedAnswerDetails';

@Component
export default class OpenEndedView extends Vue {
  @Prop() readonly questionDetails!: OpenEndedQuestionDetails;
  @Prop() readonly answerDetails?: OpenEndedAnswerDetails;

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

  studentAnswerCorrect() {
    return this.answerDetails?.isCorrect(this.questionDetails!);
  }

  studentAnswer(): string {
    return this.answerDetails?.givenAnswer == null
      ? 'Student did not answer question'
      : this.answerDetails?.givenAnswer;
  }

  defaultCorrectAnswer(): string {
    return this.questionDetails!.defaultAnswer;
  }
}
</script>

<style scoped></style>
