package pt.ulisboa.tecnico.classes.classserver;

import io.grpc.stub.StreamObserver;
import pt.ulisboa.tecnico.classes.DebugMode;
import pt.ulisboa.tecnico.classes.Stringify;
import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions;
import pt.ulisboa.tecnico.classes.contract.student.StudentClassServer;
import pt.ulisboa.tecnico.classes.contract.student.StudentServiceGrpc;
import pt.ulisboa.tecnico.classes.FastLogger;


public class StudentServerImpl extends StudentServiceGrpc.StudentServiceImplBase {
    Class aClass;

    private void log(String info){
        if (DebugMode.isDebug())
            FastLogger.log(true, StudentServerImpl.class.getName(), info);
    }

    private void logError(String info){
        if (DebugMode.isDebug())
            FastLogger.logError(true, StudentServerImpl.class.getName(), info);
    }

    public StudentServerImpl(Class aClass) {
        this.aClass = aClass;
    }

    /**
     * tries to enroll
     *
     * @param request  
     * @param responseObserver  
     */
    @Override
    public void enroll(StudentClassServer.EnrollRequest request, StreamObserver<StudentClassServer.EnrollResponse> responseObserver) {
        ClassesDefinitions.ResponseCode code = ClassesDefinitions.ResponseCode.OK;
        log("Trying to enroll student" + request.getStudent());
        try {
            aClass.enroll(request.getStudent());
            log(Stringify.format(code));
        } catch (ClassServerException e) {
            code = e.getResponseCode();
            logError(Stringify.format(code));
        }
        responseObserver.onNext(StudentClassServer.EnrollResponse.newBuilder().setCode(code).build());
        responseObserver.onCompleted();
    }

    /**
     * lists the state of the class
     *
     * @param request  
     * @param responseObserver  
     */
    @Override
    public void listClass(StudentClassServer.ListClassRequest request, StreamObserver<StudentClassServer.ListClassResponse> responseObserver) {
        ClassesDefinitions.ResponseCode code = ClassesDefinitions.ResponseCode.OK;
        log("Trying to list class");
        try {
            responseObserver.onNext(StudentClassServer.ListClassResponse.newBuilder().setClassState(aClass.list()).setCode(code).build());
            log(Stringify.format(code));
        } catch (ClassServerException e) {
            code = e.getResponseCode();
            responseObserver.onNext(StudentClassServer.ListClassResponse.newBuilder().setCode(code).build());
            logError(Stringify.format(code));
        }
        responseObserver.onCompleted();
    }
}


