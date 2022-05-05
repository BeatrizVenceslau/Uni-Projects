package pt.ulisboa.tecnico.classes.namingserver;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import pt.ulisboa.tecnico.classes.DebugMode;

import java.io.IOException;

public class NamingServer {

  private static int port = 5000;
  private String host = "127.0.0.1";
  private final static String debugFlag = "-debug";
  public static void main(String[] args) {

    if (args.length == 1)
      if(debugFlag.equals(args[0]))
        DebugMode.setDebug(true);


    Server server = ServerBuilder.forPort(5000).addService(new NamingServerImpl()).build();
    try {
      server.start();
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      server.awaitTermination();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
