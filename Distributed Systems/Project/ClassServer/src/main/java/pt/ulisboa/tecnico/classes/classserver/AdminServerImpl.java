package pt.ulisboa.tecnico.classes.classserver;

import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import pt.ulisboa.tecnico.classes.DebugMode;
import pt.ulisboa.tecnico.classes.FastLogger;
import pt.ulisboa.tecnico.classes.Stringify;
import pt.ulisboa.tecnico.classes.StubManager;
import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions;
import pt.ulisboa.tecnico.classes.contract.admin.AdminClassServer;
import pt.ulisboa.tecnico.classes.contract.admin.AdminServiceGrpc;
import pt.ulisboa.tecnico.classes.contract.classserver.ClassServerClassServer;

public class AdminServerImpl extends AdminServiceGrpc.AdminServiceImplBase {
    private final String uri;
    private final String serviceName;
    Class aClass;

    private void log(String info) {
        if (DebugMode.isDebug())
            FastLogger.log(true, AdminServerImpl.class.getName(), info);
    }

    private void logError(String info){
        if (DebugMode.isDebug())
            FastLogger.logError(true, AdminServerImpl.class.getName(), info);
    }

    public AdminServerImpl(Class aClass, String serviceName, String uri) {
        this.aClass = aClass;
        this.serviceName = serviceName;
        this.uri = uri;
    }

    /**
     * activates the server
     *
     * @param request   request to activate server
     * @param responseObserver  response from server
     */
    @Override
    public void activate(AdminClassServer.ActivateRequest request, StreamObserver<AdminClassServer.ActivateResponse> responseObserver) {
        ClassesDefinitions.ResponseCode code = ClassesDefinitions.ResponseCode.OK;
        log("Trying to activate server");
        aClass.activateServer();
        responseObserver.onNext(AdminClassServer.ActivateResponse.newBuilder().setCode(code).build());
        responseObserver.onCompleted();
        log("Success");
    }

    /**
     * deactivates the server
     *
     * @param request   request to deactivate server
     * @param responseObserver  response from server
     */
    @Override
    public void deactivate(AdminClassServer.DeactivateRequest request, StreamObserver<AdminClassServer.DeactivateResponse> responseObserver) {
        ClassesDefinitions.ResponseCode code = ClassesDefinitions.ResponseCode.OK;
        log("Trying to deactivate server");
        aClass.deactivateServer();
        responseObserver.onNext(AdminClassServer.DeactivateResponse.newBuilder().setCode(code).build());
        responseObserver.onCompleted();
        log("Success");
    }

    /**
     * gives the state of the server
     *
     * @param request  request to dump server state
     * @param responseObserver  response from server
     */
    @Override
    public void dump(AdminClassServer.DumpRequest request, StreamObserver<AdminClassServer.DumpResponse> responseObserver) {
        ClassesDefinitions.ResponseCode code = ClassesDefinitions.ResponseCode.OK;
        log("Trying to dump state");
        try {
            responseObserver.onNext(AdminClassServer.DumpResponse.newBuilder().setClassState(aClass.dump()).setCode(code).build());
            log("Success");
        } catch (ClassServerException e) {
            code = e.getResponseCode();
            logError(Stringify.format(code));
            responseObserver.onNext(AdminClassServer.DumpResponse.newBuilder().setCode(code).build());
        }
        responseObserver.onCompleted();
    }

    /**
     * forces server to gossip
     *
     * @param request  request to gossip server state
     * @param responseObserver  response from server
     */
    @Override
    public synchronized void gossip(AdminClassServer.GossipRequest request, StreamObserver<AdminClassServer.GossipResponse> responseObserver) {
        ClassesDefinitions.ResponseCode code = ClassesDefinitions.ResponseCode.OK;
        log("Trying to force gossip");

        for(ManagedChannel broadcastChannel: StubManager.getBroadcastChannels(serviceName, uri)) {
            ClassServerClassServer.PropagateStateRequest propagateStateRequest = aClass.createPropagateRequest();
            ClassServerClassServer.PropagateStateResponse propagateStateResponse = StubManager.newClassServerStub(broadcastChannel).propagateState(propagateStateRequest);
            StubManager.closeChannels(broadcastChannel);
        }

        responseObserver.onNext(AdminClassServer.GossipResponse.newBuilder().setCode(code).build());
        responseObserver.onCompleted();
        log("Success");
    }

    /**
     * deactivates gossip
     *
     * @param request  request to deactivate gossip
     * @param responseObserver  response from server
     */
    @Override
    public void deactivateGossip(AdminClassServer.DeactivateGossipRequest request, StreamObserver<AdminClassServer.DeactivateGossipResponse> responseObserver) {
        ClassesDefinitions.ResponseCode code = ClassesDefinitions.ResponseCode.OK;
        log("Trying to deactivate gossip");
        aClass.deactivateGossip();
        responseObserver.onNext(AdminClassServer.DeactivateGossipResponse.newBuilder().setCode(code).build());
        responseObserver.onCompleted();
        log("Success");
    }

    /**
     * activates gossip
     *
     * @param request  request to activate gossip
     * @param responseObserver  response from server
     */
    @Override
    public void activateGossip(AdminClassServer.ActivateGossipRequest request, StreamObserver<AdminClassServer.ActivateGossipResponse> responseObserver) {
        ClassesDefinitions.ResponseCode code = ClassesDefinitions.ResponseCode.OK;
        log("Trying to activate gossip");
        aClass.activateGossip();
        responseObserver.onNext(AdminClassServer.ActivateGossipResponse.newBuilder().setCode(code).build());
        responseObserver.onCompleted();
        log("Success");
    }
}
