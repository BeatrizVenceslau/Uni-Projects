package pt.ulisboa.tecnico.classes;

import java.util.logging.Logger;

public class FastLogger {
    public static void log(boolean debug, String LogKey, String info) {
        if (debug)
            Logger.getLogger(LogKey).info("Class: "+ LogKey+ " MSG " + info+"\n");
    }

    public static void logError(boolean debug, String LogKey, String info){
        if (debug)
            Logger.getLogger(LogKey).warning("Class: "+ LogKey+ " ERROR " + info +"\n");
    }
}
