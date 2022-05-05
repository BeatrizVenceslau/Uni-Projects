package pt.ulisboa.tecnico.classes.student;

import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import pt.ulisboa.tecnico.classes.DebugMode;
import pt.ulisboa.tecnico.classes.FastLogger;
import pt.ulisboa.tecnico.classes.Stringify;
import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions;
import pt.ulisboa.tecnico.classes.contract.student.StudentClassServer;

import java.util.List;

import static pt.ulisboa.tecnico.classes.StubManager.*;

public class StudentFrontend {
    private ClassesDefinitions.Student student;
    String serviceName = "turmas";

    private void log(String info) {
        if (DebugMode.isDebug())
            FastLogger.log(true, StudentFrontend.class.getName(), info);
    }

    private void logError(String info){
        if (DebugMode.isDebug())
            FastLogger.logError(true, StudentFrontend.class.getName(), info);
    }

    public StudentFrontend(String studentId, String studentName) {
        super();
        log("Creating student class");
        student = ClassesDefinitions.Student.newBuilder().setStudentId(studentId).
                setStudentName(studentName).build();
    }
    /**
     * try to enroll
     */
    public synchronized void enroll(){
        log("Trying to enroll");
        StudentClassServer.EnrollRequest request = StudentClassServer.EnrollRequest.newBuilder().setStudent(student).build();
        ClassesDefinitions.ResponseCode code = ClassesDefinitions.ResponseCode.INACTIVE_SERVER;
        List <ManagedChannel> availableChannels = getAllChannels(serviceName);
        for (ManagedChannel availableChannel : availableChannels) {
            try {
                StudentClassServer.EnrollResponse response = newStudentClassStub(availableChannel).enroll(request);
                if(response.getCode() != ClassesDefinitions.ResponseCode.INACTIVE_SERVER) {
                    code = response.getCode();
                    break;
                }
            } catch (StatusRuntimeException exception) {
                logError(exception.toString());
            }
        }
        closeChannels(availableChannels);

        String message = Stringify.format(code);
        System.out.println(message +"\n");
        log(message);
    }

    /**
     * lists the state of the class
     */
    public synchronized void list() {
        log("Trying to list class state");
        StudentClassServer.ListClassRequest request = StudentClassServer.ListClassRequest.newBuilder().build();
        ClassesDefinitions.ResponseCode code = ClassesDefinitions.ResponseCode.INACTIVE_SERVER;
        ClassesDefinitions.ClassState state = null;

        List<ManagedChannel> availableChannels = getAllChannels(serviceName);
        for (ManagedChannel availableChannel : availableChannels) {
            try {
                StudentClassServer.ListClassResponse response = newStudentClassStub(availableChannel).listClass(request);
                if(response.getCode() != ClassesDefinitions.ResponseCode.INACTIVE_SERVER) {
                    code = response.getCode();
                    state = response.getClassState();
                    break;
                }
            } catch (StatusRuntimeException exception) {
                logError(exception.toString());
            }
        }
        closeChannels(availableChannels);

        String message = "";
        if(code == ClassesDefinitions.ResponseCode.OK) {
            message = Stringify.format(state);
        } else {
            message = Stringify.format(code);
        }
        log(message);
        System.out.println(message +"\n");
    }
}
