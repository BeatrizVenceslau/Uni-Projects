
package pt.ulisboa.tecnico.classes.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AdminImpl {
    private String EXIT_CMD = "exit";
    private String DUMP = "dump";
    private String DEACTIVATE = "deactivate";
    private String ACTIVATE = "activate";
    private String DEACTIVATEGOSSIP = "deactivateGossip";
    private String ACTIVATEGOSSIP = "activateGossip";
    private String GOSSIP = "gossip";
    private AdminFrontend frontend;

    public AdminImpl(){
        super();
        frontend = new AdminFrontend();
    }

    /**
     * Returns flag S if it receives S otherwise returns P
     *
     * @param flag  char 'S' or 'P' that is received in the terminal
     * @return      String flag
     */
    public String checkQualifiers(String flag) {
        if (flag.equals("S")) {
            return "S";
        }
        else{
            return "P";
        }
    }

    /**
     * Returns a list composed of the qualifiers received in the terminal
     *
     * @param expected  the expected command
     * @param received  the received line from terminal
     * @return          List of Strings with the corresponding qualifiers
     */
    public List<String> getQualifiers(String expected, String received){
        List<String> qualifiers = new ArrayList<>();
        try {
            String flag = received.substring(expected.length()+1);
            qualifiers.add(checkQualifiers(flag));
        } catch (Exception e) {
            qualifiers.add("P");
        }
        return qualifiers;
    }

    //Client interface for Admin
    public void client() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine();

            if (EXIT_CMD.equals(line)) {
                scanner.close();
                System.exit(0);
                break;
            }
            else if (DUMP.equals(line.split(" ")[0])) {
                frontend.dump(getQualifiers(DUMP, line));
            }
            else if (ACTIVATE.equals(line.split(" ")[0])) {
                frontend.activate(getQualifiers(ACTIVATE, line));
            }
            else if (DEACTIVATE.equals(line.split(" ")[0])) {
                frontend.deactivate(getQualifiers(DEACTIVATE, line));
            }
            else if (ACTIVATEGOSSIP.equals(line.split(" ")[0])) {
                frontend.activateGossip(getQualifiers(ACTIVATEGOSSIP, line));
            }
            else if (DEACTIVATEGOSSIP.equals(line.split(" ")[0])) {
                frontend.deactivateGossip(getQualifiers(DEACTIVATEGOSSIP, line));
            }
            else if (GOSSIP.equals(line.split(" ")[0])) {
                frontend.forceGossip(getQualifiers(GOSSIP, line));
            }
        }
        System.out.print("> ");
    }
}
