package pt.ulisboa.tecnico.classes;

public class DebugMode {
    private static boolean debug = false;

    public static void setDebug(boolean debug) {
        DebugMode.debug = debug;
    }

    public static boolean isDebug() {
        return debug;
    }
}
