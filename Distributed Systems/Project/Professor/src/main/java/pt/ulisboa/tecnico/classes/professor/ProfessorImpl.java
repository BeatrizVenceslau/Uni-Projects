package pt.ulisboa.tecnico.classes.professor;

import pt.ulisboa.tecnico.classes.DebugMode;
import pt.ulisboa.tecnico.classes.FastLogger;

import java.util.Scanner;

public class ProfessorImpl {
    private String EXIT_CMD = "exit";
    private String OPEN_ENROLL = "openEnrollments";
    private String CLOSE_ENROLL = "closeEnrollments";
    private String CANCEL_ENROLL = "cancelEnrollment";
    private String LIST = "list";
    private ProfessorFrontend frontend;

    public ProfessorImpl(){
        super();
        frontend = new ProfessorFrontend();
    }

    //client interface for professor
    public void client() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine();
            // exit
            if (EXIT_CMD.equals(line)) {
                scanner.close();
                System.exit(0);
                break;
            }
            else if (OPEN_ENROLL.equals(line.split(" ")[0])) {
                try {
                    String a = line.substring(OPEN_ENROLL.length()+1);
                    int capacity = Integer.parseInt(a);
                    frontend.open_enroll(capacity);
                } catch (Exception e) {
                    FastLogger.logError(DebugMode.isDebug(), ProfessorImpl.class.getName(), e.getMessage());
                }
            }
            else if (CLOSE_ENROLL.equals(line)) {
                frontend.close_enroll();
            }
            else if (CANCEL_ENROLL.equals(line.split(" ")[0])) {
                try {
                    String studentId = line.substring(CANCEL_ENROLL.length()+1);
                    frontend.discard(studentId);
                } catch (Exception e) {
                    FastLogger.logError(DebugMode.isDebug(), ProfessorImpl.class.getName(), e.getMessage());
                }
            }
            else if (LIST.equals(line)) {
                frontend.list();
            }
        }
        System.out.print("> ");
    }
}
