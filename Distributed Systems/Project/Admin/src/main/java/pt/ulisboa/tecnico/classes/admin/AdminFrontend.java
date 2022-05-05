package pt.ulisboa.tecnico.classes.admin;

import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import pt.ulisboa.tecnico.classes.DebugMode;
import pt.ulisboa.tecnico.classes.FastLogger;
import pt.ulisboa.tecnico.classes.Stringify;
import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions;
import pt.ulisboa.tecnico.classes.contract.admin.AdminClassServer;

import java.util.List;

import static pt.ulisboa.tecnico.classes.StubManager.*;

public class AdminFrontend {
    private String serviceName = "turmas";

    private void log(String info) {
        if (DebugMode.isDebug())
            FastLogger.log(true, AdminFrontend.class.getName(), info);
    }

    private void logError(String info){
        if (DebugMode.isDebug())
            FastLogger.logError(true, AdminFrontend.class.getName(), info);
    }

    public AdminFrontend() {
        super();
    }

    /**
     * creates a request to the first available server that correspond to the required qualifiers
     * prints the state of that server
     *
     * @param qualifiers    list of strings with the qualifiers
     */
    public synchronized void dump(List<String> qualifiers){
        try {
            log("Client requesting class state dump");
            AdminClassServer.DumpRequest request = AdminClassServer.DumpRequest.newBuilder().build();
            ClassesDefinitions.ResponseCode code = ClassesDefinitions.ResponseCode.INACTIVE_SERVER;
            ClassesDefinitions.ClassState classState = null;

            AdminClassServer.DumpResponse response;

            //Gets all the available channels for Admin that match the required qualifiers list
            List <ManagedChannel> availableChannels = getAdminChannels(serviceName, qualifiers);
            if (!availableChannels.isEmpty()) {
                response = newAdminClassStub(availableChannels.get(0)).dump(request);
                code = response.getCode();
                classState = response.getClassState();
            }

            closeChannels(availableChannels);

            if (code == ClassesDefinitions.ResponseCode.OK) {
                log("request handled successfully");
                System.out.println(Stringify.format(classState) +"\n");
                return;
            }
            logError(Stringify.format(code));
            System.out.println(Stringify.format(code));
        } catch (StatusRuntimeException exception) {
            logError(exception.toString());
        }
    }

    /**
     * creates a request to the first available server that correspond to the required qualifiers
     * activates that server
     *
     * @param qualifiers    list of strings with the qualifiers
     */
    public synchronized void activate(List<String> qualifiers){
        try {
            log("Client trying to activate server");
            AdminClassServer.ActivateRequest request = AdminClassServer.ActivateRequest.newBuilder().build();
            ClassesDefinitions.ResponseCode code = ClassesDefinitions.ResponseCode.INACTIVE_SERVER;
            AdminClassServer.ActivateResponse response;

            //Gets all the available channels for Admin that match the required qualifiers list
            List <ManagedChannel> availableChannels = getAdminChannels(serviceName, qualifiers);
            if (!availableChannels.isEmpty()) {
                response = newAdminClassStub(availableChannels.get(0)).activate(request);
                code = response.getCode();
            }
            closeChannels(availableChannels);

            System.out.println(Stringify.format(code) +"\n");
            if (code == ClassesDefinitions.ResponseCode.OK)
                log("Server activated");
            else
                logError(Stringify.format(code));
        } catch (StatusRuntimeException exception) {
            logError(exception.toString());
        }
    }

    /**
     * creates a request to the first available server that correspond to the required qualifiers
     * deactivates that server
     *
     * @param qualifiers    list of strings with the qualifiers
     */
    public synchronized void deactivate(List<String> qualifiers){
        try {
            log("Admin trying to deactivate server");
            AdminClassServer.DeactivateRequest request = AdminClassServer.DeactivateRequest.newBuilder().build();
            AdminClassServer.DeactivateResponse response;
            ClassesDefinitions.ResponseCode code = ClassesDefinitions.ResponseCode.INACTIVE_SERVER;

            //Gets all the available channels for Admin that match the required qualifiers list
            List <ManagedChannel> availableChannels = getAdminChannels(serviceName, qualifiers);
            if (!availableChannels.isEmpty()) {
                response = newAdminClassStub(availableChannels.get(0)).deactivate(request);
                code = response.getCode();
            }
            closeChannels(availableChannels);

            System.out.println(Stringify.format(code) +"\n");
            if (code == ClassesDefinitions.ResponseCode.OK)
                log("Server deactivated");
            else
                logError(Stringify.format(code));
        } catch (StatusRuntimeException exception) {
            logError(exception.toString());
        }
    }

    /**
     * creates a request to the first available server that correspond to the required qualifiers
     * deactivates that server's gossip
     *
     * @param qualifiers    list of strings with the qualifiers
     */
    public synchronized void deactivateGossip(List<String> qualifiers){
        try {
            log("Admin trying to deactivate gossip");
            AdminClassServer.DeactivateGossipRequest request = AdminClassServer.DeactivateGossipRequest.newBuilder().build();
            AdminClassServer.DeactivateGossipResponse response;
            ClassesDefinitions.ResponseCode code = ClassesDefinitions.ResponseCode.INACTIVE_SERVER;

            //Gets all the available channels for Admin that match the required qualifiers list
            List <ManagedChannel> availableChannels = getAdminChannels(serviceName, qualifiers);
            if (!availableChannels.isEmpty()) {
                response = newAdminClassStub(availableChannels.get(0)).deactivateGossip(request);
                code = response.getCode();
            }
            closeChannels(availableChannels);

            System.out.println(Stringify.format(code) +"\n");
            if (code == ClassesDefinitions.ResponseCode.OK)
                log("Gossip Deactivated");
            else
                logError(Stringify.format(code));
        } catch (StatusRuntimeException exception) {
            logError(exception.toString());
        }
    }

    /**
     * creates a request to the first available server that correspond to the required qualifiers
     * activates that server's gossip
     *
     * @param qualifiers    list of strings with the qualifiers
     */
    public synchronized void activateGossip(List<String> qualifiers){
        try {
            log("Client trying to activate gossip");
            AdminClassServer.ActivateGossipRequest request = AdminClassServer.ActivateGossipRequest.newBuilder().build();
            AdminClassServer.ActivateGossipResponse response;
            ClassesDefinitions.ResponseCode code = ClassesDefinitions.ResponseCode.INACTIVE_SERVER;

            //Gets all the available channels for Admin that match the required qualifiers list
            List <ManagedChannel> availableChannels = getAdminChannels(serviceName, qualifiers);
            if (!availableChannels.isEmpty()) {
                response = newAdminClassStub(availableChannels.get(0)).activateGossip(request);
                code = response.getCode();
            }
            closeChannels(availableChannels);

            System.out.println(Stringify.format(code) +"\n");
            if (code == ClassesDefinitions.ResponseCode.OK)
                log("Gossip Active");
            else
                logError(Stringify.format(code));
        } catch (StatusRuntimeException exception) {
            logError(exception.toString());
        }
    }

    /**
     * creates a request to the first available server that correspond to the required qualifiers
     * and forces that server to gossip
     *
     * @param qualifiers    list of strings with the qualifiers
     */
    public synchronized void forceGossip(List<String> qualifiers) {
         try {
            log("Admin forcing" + qualifiers +  " to gossip");
            AdminClassServer.GossipRequest request = AdminClassServer.GossipRequest.newBuilder().build();
            AdminClassServer.GossipResponse response;
            ClassesDefinitions.ResponseCode code = ClassesDefinitions.ResponseCode.INACTIVE_SERVER;

            //Gets all the available channels for Admin that match the required qualifiers list
            List <ManagedChannel> availableChannels = getAdminChannels(serviceName, qualifiers);
            if (!availableChannels.isEmpty()) {
                response = newAdminClassStub(availableChannels.get(0)).gossip(request);
                code = response.getCode();
            }
            closeChannels(availableChannels);

            System.out.println(Stringify.format(code) +"\n");
            if (code == ClassesDefinitions.ResponseCode.OK)
                log("Gossip Forced");
            else
                logError(Stringify.format(code));
        } catch (StatusRuntimeException exception) {
            logError(exception.toString());
        }
    }

}
