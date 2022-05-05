package pt.ulisboa.tecnico.classes.student;

import java.util.Scanner;

public class StudentImpl {
    private String EXIT_CMD = "exit";
    private String LIST_CLASS = "list";
    private String ENROLL = "enroll";
    private StudentFrontend frontend;

    public StudentImpl(String studentId, String studentName){
        super();
        frontend = new StudentFrontend(studentId, studentName);
    }
    
    //client interface for student
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
            else if (ENROLL.equals(line)) {
                frontend.enroll();
            }
            else if (LIST_CLASS.equals(line)) {
                frontend.list();
            }
        }
        System.out.print("> ");
    }
}
