package pt.ulisboa.tecnico.classes.classserver;

import io.grpc.stub.StreamObserver;
import pt.ulisboa.tecnico.classes.DebugMode;
import pt.ulisboa.tecnico.classes.FastLogger;
import pt.ulisboa.tecnico.classes.Stringify;
import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions;
import pt.ulisboa.tecnico.classes.contract.classserver.ClassServerClassServer;
import pt.ulisboa.tecnico.classes.contract.classserver.ClassServerServiceGrpc;

import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class ClassServerImpl extends ClassServerServiceGrpc.ClassServerServiceImplBase{
    Class aClass;
    private String serviceName;
    static Timer timer;
    private String uri;

    private void log(String info) {
        if (DebugMode.isDebug())
            FastLogger.log(true, ClassServerImpl.class.getName(), info);
    }

    private void logError(String info){
        if (DebugMode.isDebug())
            FastLogger.logError(true, ClassServerImpl.class.getName(), info);
    }

    public String getUri() {
        return uri;
    }

    public ClassServerImpl(Class aClass, String serviceName, String URI){
        this.aClass = aClass;
        this.serviceName = serviceName;
        this.uri = URI;
    }

    //In the primary server, propagates the state of the server each second
    public void setTimer(){
        log("Propagation enabled");
        timer = new Timer(true);
        int one_second = 1000;
        int duration = 5 * one_second;
        timer.scheduleAtFixedRate(new PropagateStateTask(), 0, duration);
    }

    /**
     * receives the propagated state from another server and updates accordingly
     *
     * @param request  
     * @param responseObserver  
     */
    @Override
    public synchronized void propagateState(ClassServerClassServer.PropagateStateRequest request, StreamObserver<ClassServerClassServer.PropagateStateResponse> responseObserver) {
        ClassesDefinitions.ResponseCode code = ClassesDefinitions.ResponseCode.OK;
        if (!aClass.getActive())
            code = ClassesDefinitions.ResponseCode.INACTIVE_SERVER;
        if(aClass.getActive() && aClass.getGossip()) {
            log("Receiving state from another server");
            try {
                Class receivedClass = aClass.createReceivedClass(request.getClassState(), request.getEnrollableTimesList()
                        .stream()
                        .map(item->new TimeFrame(item))
                        .collect(Collectors.toList()));
                aClass.correctTimeline(receivedClass.getEnrollablesTimes());
                aClass.mergeStudents(receivedClass);
            } catch (ClassServerException e) {
                code = e.getResponseCode();
            }
        }
        responseObserver.onNext(ClassServerClassServer.PropagateStateResponse.newBuilder().setCode(code).build());
        responseObserver.onCompleted();
        if (aClass.getGossip())
            log(Stringify.format(code));
    }

    class PropagateStateTask extends TimerTask {
        public void run() {
            PropagateFrontend.propagateState(aClass, serviceName, getUri());
        }
    }
}
