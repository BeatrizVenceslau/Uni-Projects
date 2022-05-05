package pt.ulisboa.tecnico.classes.student;

import pt.ulisboa.tecnico.classes.DebugMode;

public class Student {
  private static final String ALUNO = "aluno";
  private static final int USER_ID_LENGTH = 9;
  private static final int MIN_NUMBER_ARGS = 2;
  private static final int USERNAME_MAX_LENGTH = 30;
  private static final int USERNAME_MIN_LENGTH = 3;
  private static final String debugFlag = "-debug";

  public static void main(String[] args) {
    String username = "";
    String userid = "";

    if (args.length < MIN_NUMBER_ARGS) {
      return;
    }

    String tmpId = args[0];
    StringBuilder tmp_user = new StringBuilder();
    for (int i = 1; i < args.length; i++) {
      if (i > 1 && args[i].equals(debugFlag)) {
        DebugMode.setDebug(true);
        break;
      }
      tmp_user.append(args[i]);

      if (args[args.length-1].equals(debugFlag) && args.length-2 != i)
        tmp_user.append(" ");
      else if (!args[args.length-1].equals(debugFlag) && i != args.length-1)
        tmp_user.append(" ");

    }

    //Verify userName
    if (!(tmp_user.toString().length() <= USERNAME_MAX_LENGTH && tmp_user.toString().length() >= USERNAME_MIN_LENGTH)) {
       return;
    }
    username = tmp_user.toString();

    //Verify userId
    if (tmpId.length() != USER_ID_LENGTH) {
      return;
    }

    String firstFiveChars = tmpId.substring(0, 5);
    String lastFourChars = tmpId.substring(5);

    //Verify that last four chars are integer
    try {
      Integer.valueOf(lastFourChars);
    } catch (Exception e) {
      return;
    }

    //Verify that first five chars are the word aluno
    if (!firstFiveChars.equals(ALUNO)) {
      return;
    }
    userid = tmpId;
    StudentImpl student = new StudentImpl(userid, username);
    student.client();
  }
}

