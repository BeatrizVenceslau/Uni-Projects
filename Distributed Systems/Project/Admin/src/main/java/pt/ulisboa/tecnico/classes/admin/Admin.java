package pt.ulisboa.tecnico.classes.admin;

import pt.ulisboa.tecnico.classes.DebugMode;

public class Admin {
  private static final String debugFlag = "-debug";

  public static void main(String[] args) {
    if (args.length == 1 && debugFlag.equals(args[0])) {
      DebugMode.setDebug(true);
    }
    AdminImpl admin = new AdminImpl();
    admin.client();
  }
}
