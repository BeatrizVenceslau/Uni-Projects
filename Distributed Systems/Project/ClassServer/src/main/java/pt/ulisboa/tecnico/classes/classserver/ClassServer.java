package pt.ulisboa.tecnico.classes.classserver;

import io.grpc.Server;
import pt.ulisboa.tecnico.classes.DebugMode;
import pt.ulisboa.tecnico.classes.FastLogger;

public class ClassServer {
  private static String host;
  private static int port;
  private static char flag;
  private static final int ARGS_LENGTH = 3;
  private static final char P = 'P';
  private static final char S = 'S';
  private static final String debugFlag = "-debug";
  private static boolean debug = false;

  private static void log(String info) {
    if (debug)
      FastLogger.log(true, ClassServer.class.getName(), info);
  }

  private static void logError(String info){
    if (debug)
      FastLogger.logError(true, ClassServer.class.getName(), info);
  }

  public static void main(String[] args) {
    if (args.length < ARGS_LENGTH) {
      return;
    }

    host = args[0];

    //Verify port
    try {
      port = Integer.parseInt(args[1]);
    } catch (Exception e) {
      return;
    }

    //Verify flag
    flag = args[2].charAt(0);
    if (!(flag == P || flag == S)) { //string can only have one char
      return;
    }

    if (args.length == ARGS_LENGTH+1) {
      if (debugFlag.equals(args[3])) {
        DebugMode.setDebug(true);
      }
    }
    try {
      Thread.sleep(20);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    ClassServerRunner classServer = new ClassServerRunner(flag, port);
    //Create new server to listen on port
    Server server = classServer.createServer();
    //Start the server
    try {
      log("Starting Server");
      server.start();
    } catch (Exception e) {
      logError("Server start failed");
      return;
    }
    log("Server started");

    //Wait until server is terminated
    try {
      server.awaitTermination();
    } catch (Exception e) {
      logError("Server could not terminate");
    }
  }
}
