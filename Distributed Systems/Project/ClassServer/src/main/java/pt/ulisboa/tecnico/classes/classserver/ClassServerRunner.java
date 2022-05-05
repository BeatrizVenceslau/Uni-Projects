package pt.ulisboa.tecnico.classes.classserver;

import io.grpc.*;
import pt.ulisboa.tecnico.classes.DebugMode;
import pt.ulisboa.tecnico.classes.FastLogger;
import pt.ulisboa.tecnico.classes.NamingServerFrontend;

import java.util.Arrays;

public class ClassServerRunner {
    Class aClass;
    final BindableService professorServerImpl;
    final BindableService adminServerImpl;
    final BindableService studentServerImpl;
    private String host = "localhost";
    private int myPort = 0;
    private String serviceName = "turmas";
    private String flag;
    final ClassServerImpl propagateStateImpl;

    private void log(String info) {
        if (DebugMode.isDebug())
            FastLogger.log(true, ClassServerRunner.class.getName(), info);
    }

    private void logError(String info){
        if (DebugMode.isDebug())
            FastLogger.logError(true, ClassServerRunner.class.getName(), info);
    }

    public ClassServerRunner(char flag, int port){
        this.flag = Character.toString(flag);

        myPort = port;
        aClass = new Class(flag);

        log("Creating all service implementations");
        professorServerImpl = new ProfessorServerImpl(aClass);
        adminServerImpl =  new AdminServerImpl(aClass, serviceName, getURI());
        studentServerImpl = new StudentServerImpl(aClass);
        propagateStateImpl = new ClassServerImpl(aClass, serviceName, getURI());
        log("Implementations created");

        log("Adding shutdown hook");
        Runtime.getRuntime().addShutdownHook(new ShutdownHook());
    }

    public String getURI() {
        return host+":"+myPort;
    }

    /**
     * Creates a server and registers it in the naming server
     *
     * @return          Server
     */
    public Server createServer() {
        log("Creating server");
        NamingServerFrontend.getInstance().register(myPort, host, serviceName, Arrays.asList(flag));
        propagateStateImpl.setTimer();
        return ServerBuilder.forPort(myPort)
                .addService(professorServerImpl)
                .addService(adminServerImpl)
                .addService(studentServerImpl)
                .addService(propagateStateImpl)
                .build();
    }

    //Creates a shutdown hook to close the server
    class ShutdownHook extends Thread{
        public ShutdownHook() {}

        public synchronized void run() {
            NamingServerFrontend.delete(myPort, host, serviceName);
            log("Closing server");
        }
    }
}
