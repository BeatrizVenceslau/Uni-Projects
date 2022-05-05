package pt.ulisboa.tecnico.classes.professor;

import pt.ulisboa.tecnico.classes.DebugMode;

public class Professor {
  private static final String debugFlag = "-debug";
  public static void main(String[] args) {

    if (args.length == 1)
      if(debugFlag.equals(args[0]))
        DebugMode.isDebug();
    ProfessorImpl professor = new ProfessorImpl();
    professor.client();    
  }
}
