package pt.ulisboa.tecnico.classes;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.ulisboa.tecnico.classes.contract.admin.AdminServiceGrpc;
import pt.ulisboa.tecnico.classes.contract.classserver.ClassServerServiceGrpc;

import pt.ulisboa.tecnico.classes.contract.professor.ProfessorServiceGrpc;
import pt.ulisboa.tecnico.classes.contract.student.StudentServiceGrpc;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class StubManager {
    private static final int duration = 5;

    private static void log(String info) {
        if (DebugMode.isDebug())
            FastLogger.log(true, StubManager.class.getName(), info);
    }

    private static void logError(String info){
        if (DebugMode.isDebug())
            FastLogger.logError(true, StubManager.class.getName(), info);
    }

    /**
     * gets the broadcast channels
     * the channels for the given serviceName excluding the origin server identified by the selfURI
     *
     * @param serviceName
     * @param selfURI
     * @return              list of broadcast channels
     */
    public synchronized static List<ManagedChannel> getBroadcastChannels(String serviceName, String selfURI) {
        return getChannels(serviceName, selfURI, Collections.emptyList());
    }

    /**
     * gets the channels that match the given serviceName and qualifiers
     * excluding the origin server identified by the selfURI
     *
     * @param serviceName
     * @param selfURI
     * @param qualifiers
     * @return              list of channels
     */
    private synchronized static  List<ManagedChannel> getChannels(String serviceName, String selfURI, List<String> qualifiers) {
        List<String> URIS = NamingServerFrontend.getInstance().lookup(serviceName, qualifiers);
        log("Channels with: service name - "+serviceName +"\n qualifiers - "+qualifiers+"\n"+URIS);
        List<ManagedChannel> list = new ArrayList<>();
        for (String URI : URIS) {
            if (!URI.equals(selfURI)) {
                ManagedChannel channel = ManagedChannelBuilder.forTarget(URI).usePlaintext().build();
                list.add(channel);
            }
        }
        return list;
    }

    /**
     * Returns all available channels in a random order
     *
     * @param serviceName
     * @return              list of reading channels
     */
    public synchronized static List<ManagedChannel> getAllChannels(String serviceName) {
        String S = "S";
        String P = "P";
        List<ManagedChannel> primaries = getChannels(serviceName, "", Arrays.asList(P));
        List<ManagedChannel> secondaries =  getChannels(serviceName,"", Arrays.asList(S));
        List<ManagedChannel> managedChannels = new ArrayList<>();
        managedChannels.addAll(primaries);
        managedChannels.addAll(secondaries);

        Collections.shuffle(managedChannels);

        return managedChannels;
    }

    /**
     * gets the channels for writes
     *
     * @param serviceName
     * @return              list of writing channels
     */
    public synchronized static List<ManagedChannel> getPrimaryChannel(String serviceName) {
        String P = "P";
        return getChannels(serviceName, "", Arrays.asList(P));
    }

    /**
     * gets the channels for the admin that match the service name and qualifiers list given
     *
     * @param serviceName
     * @param qualifiers
     * @return              list of admin channels
     */
    public synchronized static List<ManagedChannel> getAdminChannels(String serviceName, List<String> qualifiers) {
        return getChannels(serviceName, "", qualifiers);
    }

    /**
     * returns a classServer stub to the given channel
     *
     * @param channel
     * @return              classServer stub
     */
    public synchronized static ClassServerServiceGrpc.ClassServerServiceBlockingStub newClassServerStub(ManagedChannel channel) {
        return ClassServerServiceGrpc.newBlockingStub(channel).withDeadlineAfter(duration, TimeUnit.SECONDS);
    }

    /**
     * returns a admin stub to the given channel
     *
     * @param channel
     * @return              admin stub
     */
    public synchronized static AdminServiceGrpc.AdminServiceBlockingStub newAdminClassStub(ManagedChannel channel){
        return AdminServiceGrpc.newBlockingStub(channel).withDeadlineAfter(duration, TimeUnit.SECONDS);
    }

    /**
     * returns a professor stub to the given channel
     *
     * @param channel
     * @return              professor stub
     */
    public synchronized static ProfessorServiceGrpc.ProfessorServiceBlockingStub newProfessorClassStub(ManagedChannel channel){
        return ProfessorServiceGrpc.newBlockingStub(channel).withDeadlineAfter(duration, TimeUnit.SECONDS);
    }

    /**
     * returns a student stub to the given channel
     *
     * @param channel
     * @return              student stub
     */
    public synchronized static StudentServiceGrpc.StudentServiceBlockingStub newStudentClassStub(ManagedChannel channel){
        return StudentServiceGrpc.newBlockingStub(channel).withDeadlineAfter(duration, TimeUnit.SECONDS);
    }

    /**
     * closes a given channel
     *
     * @param channel
     */
    public synchronized static void closeChannels(ManagedChannel channel) {
        if (!channel.isShutdown()) {
            channel.shutdown();
            try {
                while(!channel.awaitTermination(1, TimeUnit.NANOSECONDS)) {}
            } catch (InterruptedException e) {
                logError(e.getStackTrace().toString());
            }
        }
    }

    /**
     * closes all channels in the given list
     *
     * @param channels
     */
    public synchronized static void closeChannels(List<ManagedChannel> channels) {
        for (ManagedChannel channel : channels) {
            if (!channel.isShutdown()) {
                    channel.shutdown();
                try {
                    while(!channel.awaitTermination(1, TimeUnit.NANOSECONDS)) {}
                } catch (InterruptedException e) {
                    logError(e.getStackTrace().toString());
                }
            }
        }

    }
}
