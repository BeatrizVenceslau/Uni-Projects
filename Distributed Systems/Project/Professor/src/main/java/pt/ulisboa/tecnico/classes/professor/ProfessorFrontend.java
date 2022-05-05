package pt.ulisboa.tecnico.classes.professor;

import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import pt.ulisboa.tecnico.classes.DebugMode;
import pt.ulisboa.tecnico.classes.Stringify;
import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions;
import pt.ulisboa.tecnico.classes.contract.professor.ProfessorClassServer;
import pt.ulisboa.tecnico.classes.FastLogger;
import java.util.List;
import static pt.ulisboa.tecnico.classes.StubManager.*;

public class ProfessorFrontend {
    String serviceName = "turmas";

    public ProfessorFrontend() {
        super();
    }

    private void log(String info) {
        if (DebugMode.isDebug())
            FastLogger.log(true, ProfessorFrontend.class.getName(), info);
    }

    private void logError(String info){
        if (DebugMode.isDebug())
            FastLogger.logError(true, ProfessorFrontend.class.getName(), info);
    }

    /**
     * Opens enrollments
     *
     * @param capacity  the capacity with which the class is open
     */
    public synchronized void open_enroll(int capacity){
        ProfessorClassServer.OpenEnrollmentsRequest request = ProfessorClassServer.OpenEnrollmentsRequest.newBuilder().setCapacity(capacity).build();
        ClassesDefinitions.ResponseCode code = ClassesDefinitions.ResponseCode.INACTIVE_SERVER;

        List <ManagedChannel> availableChannels = getPrimaryChannel(serviceName);
        for (ManagedChannel availableChannel : availableChannels) {
            try {
                ProfessorClassServer.OpenEnrollmentsResponse response = newProfessorClassStub(availableChannel).openEnrollments(request);
                if(response.getCode() != ClassesDefinitions.ResponseCode.INACTIVE_SERVER) {
                    code = response.getCode();
                    break;
                }
            } catch (StatusRuntimeException exception) {
                logError(exception.toString());
            }
        }
        closeChannels(availableChannels);

        System.out.println(Stringify.format(code)+"\n");
        log(Stringify.format(code)+"\n");
    }

    /**
     * closes enrollments
     */
    public synchronized void close_enroll(){
        ProfessorClassServer.CloseEnrollmentsRequest request = ProfessorClassServer.CloseEnrollmentsRequest.newBuilder().build();
        ClassesDefinitions.ResponseCode code = ClassesDefinitions.ResponseCode.INACTIVE_SERVER;

        List <ManagedChannel> availableChannels = getPrimaryChannel(serviceName);
        for (ManagedChannel availableChannel : availableChannels) {
            try {
                ProfessorClassServer.CloseEnrollmentsResponse response = newProfessorClassStub(availableChannel).closeEnrollments(request);
                if(response.getCode() != ClassesDefinitions.ResponseCode.INACTIVE_SERVER) {
                    code = response.getCode();
                    break;
                }
            } catch (StatusRuntimeException exception) {
                logError(exception.toString());
            }
        }
        closeChannels(availableChannels);


        System.out.println(Stringify.format(code)+"\n");
        log(Stringify.format(code)+"\n");
    }

    /**
     * Discards a student
     *
     * @param studentId  the ID of the student to be discarded
     */
    public synchronized void discard(String studentId){
        ProfessorClassServer.CancelEnrollmentRequest request = ProfessorClassServer.CancelEnrollmentRequest.newBuilder().setStudentId(studentId).build();
        ClassesDefinitions.ResponseCode code = ClassesDefinitions.ResponseCode.INACTIVE_SERVER;

        List <ManagedChannel> availableChannels = getAllChannels(serviceName);
        for (ManagedChannel availableChannel : availableChannels) {
            try {
                ProfessorClassServer.CancelEnrollmentResponse response = newProfessorClassStub(availableChannel).cancelEnrollment(request);
                if(response.getCode() != ClassesDefinitions.ResponseCode.INACTIVE_SERVER) {
                    code = response.getCode();
                    break;
                }
            } catch (StatusRuntimeException exception) {
                logError(exception.toString());
            }
        }
        closeChannels(availableChannels);

        System.out.println(Stringify.format(code)+"\n");
        log(Stringify.format(code)+"\n");
    }

    /**
     * Lists the state of the class 
     */
    public synchronized void list() {
        ProfessorClassServer.ListClassRequest request = ProfessorClassServer.ListClassRequest.newBuilder().build();
        ClassesDefinitions.ResponseCode code = ClassesDefinitions.ResponseCode.INACTIVE_SERVER;
        ClassesDefinitions.ClassState classState = null;

        List <ManagedChannel> availableChannels = getAllChannels(serviceName);
        for (ManagedChannel availableChannel : availableChannels) {
            try {
                ProfessorClassServer.ListClassResponse response = newProfessorClassStub(availableChannel).listClass(request);
                if(response.getCode() != ClassesDefinitions.ResponseCode.INACTIVE_SERVER) {
                    code = response.getCode();
                    classState = response.getClassState();
                    break;
                }
            } catch (StatusRuntimeException exception) {
                logError(exception.toString());
            }
        }
        closeChannels(availableChannels);

        if(code == ClassesDefinitions.ResponseCode.OK){
            System.out.println(Stringify.format(classState)+"\n");
            log(Stringify.format(code)+"\n");
            return;
        }

        System.out.println(Stringify.format(code)+"\n");
        logError(Stringify.format(code)+"\n");
    }
}

