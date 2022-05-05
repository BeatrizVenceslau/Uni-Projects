package pt.ulisboa.tecnico.classes.namingserver;

import io.grpc.stub.StreamObserver;
import pt.ulisboa.tecnico.classes.DebugMode;
import pt.ulisboa.tecnico.classes.FastLogger;
import pt.ulisboa.tecnico.classes.contract.naming.ClassServerNamingServer;
import pt.ulisboa.tecnico.classes.contract.naming.NamingServerServiceGrpc;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class NamingServerImpl extends NamingServerServiceGrpc.NamingServerServiceImplBase {
    public HashMap<String, ServiceEntry> namingServices;
    public NamingServerImpl() {
        namingServices = new HashMap<>();
    }

    private void log(String info) {
        if (DebugMode.isDebug())
            FastLogger.log(true, NamingServerImpl.class.getName(), info);
    }

    private void logError(String info){
        if (DebugMode.isDebug())
            FastLogger.logError(true, NamingServerImpl.class.getName(), info);
    }

    /**
     * adds the service entry to the naming server
     *
     * @param serviceEntry    entry with serverUri and qualifiers list
     */
    private synchronized void addService(ServiceEntry serviceEntry) {
        namingServices.put(serviceEntry.getServiceName(), serviceEntry);
    }

    /**
     * adds server entry to naming server
     * associates service name with server entry
     *
     * @param host
     * @param port
     * @param serviceName
     * @param qualifiers
     */
    private synchronized void addServerEntry(String host, int port, String serviceName, List<String> qualifiers) {
        ServerEntry serverEntry = new ServerEntry(host, port, qualifiers);
        if (namingServices.get(serviceName) == null)
            addService(new ServiceEntry(serviceName));
        namingServices.get(serviceName).addServerEntry(serverEntry);

    }

    /**
     * gets the uris of the servers in the naming server that match the given serviceName and qualifiers list
     *
     * @param serviceName
     * @param qualifiers
     * @return             list of Uris of the servers
     */
    private synchronized List<String> getNamingServices(String serviceName, List<String> qualifiers) {
        ServiceEntry serviceEntry = namingServices.get(serviceName);
        if (serviceEntry == null)
            return Collections.emptyList();
        List<ServerEntry> serverEntries = serviceEntry.getServerEntries();
        List<String> URIS = serverEntries.stream()
                .filter(serverEntry -> serverEntry.checkQualifiers(qualifiers))
                .map(serverEntry -> serverEntry.getUri())
                .collect(Collectors.toList());
        return URIS;
    }

    /**
     * removes the server entry of the server with the given host, port and serviceName
     *
     * @param serviceName
     * @param host
     * @param port
     */
    private synchronized void removeServerEntry(String serviceName, String host, int port) {
        ServiceEntry serviceEntry = namingServices.get(serviceName);
        if (serviceEntry != null) {
            serviceEntry.removeServerEntry(ServerEntry.getUri(host, port));
            if (serviceEntry.getServerEntries().isEmpty())
                namingServices.remove(serviceName);
        }
    }

    /**
     * registers the serviceEntry into naming server
     *
     * @param request
     * @param responseObserver
     */
    @Override
    public void registerServiceEntry(ClassServerNamingServer.RegisterServiceRequest request, StreamObserver<ClassServerNamingServer.RegisterServiceResponse> responseObserver) {
        log("Received a register request from "+request.getHost()+":"+request.getPort());
        addServerEntry(request.getHost(), request.getPort(), request.getServiceName(), request.getQualifiersList());
        responseObserver.onNext(ClassServerNamingServer.RegisterServiceResponse.getDefaultInstance());
        responseObserver.onCompleted();
        log("Register done successfully");
    }

    /**
     * finds all servers with the requested service characteristics
     *
     * @param request
     * @param responseObserver
     */
    @Override
    public void lookupService(ClassServerNamingServer.LookupRequest request, StreamObserver<ClassServerNamingServer.LookupResponse> responseObserver) {
        log("Received a lookup request for "+request.getServiceName()+" service");
        List<String> Uris = getNamingServices(request.getServiceName(), request.getQualifiersList());
        responseObserver.onNext(ClassServerNamingServer.LookupResponse.newBuilder().addAllServiceURIS(Uris).build());
        responseObserver.onCompleted();
        log("Sent " + Uris);
    }

    /**
     * removes the serviceEntry from naming server
     *
     * @param request
     * @param responseObserver
     */
    @Override
    public void deleteService(ClassServerNamingServer.DeleteRequest request, StreamObserver<ClassServerNamingServer.DeleteResponse> responseObserver) {
        log("Removing "+request.getHost()+":"+ request.getPort());
        removeServerEntry(request.getServiceName(), request.getHost(), request.getPort());
        responseObserver.onNext(ClassServerNamingServer.DeleteResponse.getDefaultInstance());
        responseObserver.onCompleted();
        log("Successfully removed server");
    }
}
