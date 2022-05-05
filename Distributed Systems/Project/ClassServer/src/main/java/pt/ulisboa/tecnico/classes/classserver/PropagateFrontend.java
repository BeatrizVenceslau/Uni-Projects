package pt.ulisboa.tecnico.classes.classserver;

import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import pt.ulisboa.tecnico.classes.DebugMode;
import pt.ulisboa.tecnico.classes.FastLogger;
import pt.ulisboa.tecnico.classes.Stringify;
import pt.ulisboa.tecnico.classes.StubManager;

public class PropagateFrontend {

    private static void log(String info) {
        if (DebugMode.isDebug())
            FastLogger.log(true, ClassServerImpl.class.getName(), info);
    }

    private static void logError(String info){
        if (DebugMode.isDebug())
            FastLogger.logError(true, ClassServerImpl.class.getName(), info);
    }

    /**
     * propagates satte to another server
     *
     * @param aClass
     * @param serviceName
     * @param uri
     */
    public static synchronized void propagateState(Class aClass, String serviceName, String uri){
        if (!aClass.getActive())
            return;
        if (aClass.getGossip()) {
            for (ManagedChannel broadcastChannel : StubManager.getBroadcastChannels(serviceName, uri)) {
                try {
                    log("Propagating state to another server");
                    pt.ulisboa.tecnico.classes.contract.classserver.ClassServerClassServer.PropagateStateRequest request = aClass.createPropagateRequest();
                    pt.ulisboa.tecnico.classes.contract.classserver.ClassServerClassServer.PropagateStateResponse response = StubManager.newClassServerStub(broadcastChannel).propagateState(request);
                    log(Stringify.format(response.getCode()));
                    StubManager.closeChannels(broadcastChannel);
                    log("State propagated");
                } catch (StatusRuntimeException exception) {
                    logError(exception.toString());
                }
            }
        }
    }

}
