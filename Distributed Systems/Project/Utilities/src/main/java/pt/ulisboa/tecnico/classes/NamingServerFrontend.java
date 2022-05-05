package pt.ulisboa.tecnico.classes;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import pt.ulisboa.tecnico.classes.contract.naming.ClassServerNamingServer;
import pt.ulisboa.tecnico.classes.contract.naming.NamingServerServiceGrpc;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NamingServerFrontend {
    private static NamingServerServiceGrpc.NamingServerServiceBlockingStub stub;
    private static ManagedChannel channel;
    private static String host = "localhost";
    private static int port = 5000;
    private static final int duration = 5;
    private static NamingServerFrontend nameServerFrontend = null;

    private static void log(String info) {
        if (DebugMode.isDebug())
            FastLogger.log(true, NamingServerFrontend.class.getName(), info);
    }

    private static void logError(String info){
        if (DebugMode.isDebug())
            FastLogger.logError(true, NamingServerFrontend.class.getName(), info);
    }

    private NamingServerFrontend() {
        Runtime.getRuntime().addShutdownHook(new ShutdownHook());
    }

    public static NamingServerFrontend getInstance() {
        if (nameServerFrontend == null)
            nameServerFrontend = new NamingServerFrontend();

        return nameServerFrontend;
    }

    /**
     * finds all servers in naming server that match the serviceName and qualifiers list
     *
     * @param serviceName
     * @param qualifiers
     * @return              the list of Uris of the servers
     */
    public synchronized static List<String> lookup(String serviceName, List<String> qualifiers){
        log("Doing a lookup");
        try {
            createStub();
            ClassServerNamingServer.LookupRequest lookupRequest = ClassServerNamingServer.LookupRequest.newBuilder().setServiceName(serviceName).addAllQualifiers(qualifiers).build();
            ClassServerNamingServer.LookupResponse lookupResponse = stub.lookupService(lookupRequest);
            StubManager.closeChannels(channel);
            return lookupResponse.getServiceURISList();
        } catch (StatusRuntimeException e){
            logError("No naming server running");
            return Collections.emptyList();
        }
    }

    /**
     * registers the service in naming server
     *
     * @param myPort    the port of the server
     * @param host
     * @param serviceName
     * @param qualifiers
     */
    public synchronized static void register(int myPort, String host, String serviceName, List<String> qualifiers) {
        log("Registering "+host+":"+myPort);
        try {
            createStub();
            ClassServerNamingServer.RegisterServiceRequest request = ClassServerNamingServer.RegisterServiceRequest.newBuilder().setPort(myPort).setHost(host).setServiceName(serviceName).addAllQualifiers(qualifiers).build();
            ClassServerNamingServer.RegisterServiceResponse response = stub.registerServiceEntry(request);
            StubManager.closeChannels(channel);
        } catch (StatusRuntimeException e){
            logError("No naming server running");
        }
    }

    /**
     * deletes the service in the naming server
     *
     * @param myPort    the port of the server
     * @param host
     * @param serviceName
     */
    public synchronized static void delete(int myPort, String host, String serviceName) {
        log("Removing "+host+":"+myPort);
        try {
            createStub();
            ClassServerNamingServer.DeleteRequest request = ClassServerNamingServer.DeleteRequest.newBuilder().setPort(myPort).setHost(host).setServiceName(serviceName).build();
            ClassServerNamingServer.DeleteResponse response = stub.deleteService(request);
            StubManager.closeChannels(channel);
        } catch (StatusRuntimeException e){
            logError("No naming server running");
        }
    }

    //creates a blocking stub for the naming server
    private synchronized static void createStub() {
        channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        stub = NamingServerServiceGrpc.newBlockingStub(channel).withDeadlineAfter(duration, TimeUnit.SECONDS);
    }

    //creates a shutdown hook to close the naming server
    class ShutdownHook extends Thread{
        public ShutdownHook() {}

        public synchronized void run() {
            StubManager.closeChannels(channel);
            log("Closing naming server");
        }
    }

}
