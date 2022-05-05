package pt.ulisboa.tecnico.classes.classserver;

import io.grpc.stub.StreamObserver;
import pt.ulisboa.tecnico.classes.DebugMode;
import pt.ulisboa.tecnico.classes.FastLogger;
import pt.ulisboa.tecnico.classes.Stringify;
import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions;
import pt.ulisboa.tecnico.classes.contract.professor.ProfessorClassServer;
import pt.ulisboa.tecnico.classes.contract.professor.ProfessorServiceGrpc;

public class ProfessorServerImpl extends ProfessorServiceGrpc.ProfessorServiceImplBase {
    Class aClass;

    private void log(String info) {
        if (DebugMode.isDebug())
            FastLogger.log(true, ProfessorServerImpl.class.getName(), info);
    }

    private void logError(String info){
        if (DebugMode.isDebug())
            FastLogger.logError(true, ProfessorServerImpl.class.getName(), info);
    }

    public ProfessorServerImpl(Class aClass) {
        this.aClass = aClass;
    }

    /**
     * opens enrollments with the expressed capacity from the request
     *
     * @param request  
     * @param responseObserver  
     */
    @Override
    public void openEnrollments(ProfessorClassServer.OpenEnrollmentsRequest request, StreamObserver<ProfessorClassServer.OpenEnrollmentsResponse> responseObserver) {
        ClassesDefinitions.ResponseCode code = ClassesDefinitions.ResponseCode.OK;
        log("Trying to open enrollments");
        try {
            aClass.openEnrollments(request.getCapacity());
            log(Stringify.format(code));
        } catch (ClassServerException e) {
            code = e.getResponseCode();
            logError(Stringify.format(code));
        }

        responseObserver.onNext(ProfessorClassServer.OpenEnrollmentsResponse.newBuilder().setCode(code).build());
        responseObserver.onCompleted();
        log("Success");
    }

    /**
     * closes enrollments
     *
     * @param request  
     * @param responseObserver  
     */
    @Override
    public void closeEnrollments(ProfessorClassServer.CloseEnrollmentsRequest request, StreamObserver<ProfessorClassServer.CloseEnrollmentsResponse> responseObserver) {
        ClassesDefinitions.ResponseCode code = ClassesDefinitions.ResponseCode.OK;
        log("Trying to close enrollments");
        try {
            aClass.closeEnrollments();
            log(Stringify.format(code));
        } catch (ClassServerException e) {
            code = e.getResponseCode();
            logError(Stringify.format(code));
        }
        responseObserver.onNext(ProfessorClassServer.CloseEnrollmentsResponse.newBuilder().setCode(code).build());
        responseObserver.onCompleted();
        log("Success");
    }

    /**
     * lists the state of the class
     *
     * @param request  
     * @param responseObserver  
     */
    @Override
    public void listClass(ProfessorClassServer.ListClassRequest request, StreamObserver<ProfessorClassServer.ListClassResponse> responseObserver) {
        ClassesDefinitions.ResponseCode code = ClassesDefinitions.ResponseCode.OK;
        log("Trying to list class");
        try {
            responseObserver.onNext(ProfessorClassServer.ListClassResponse.newBuilder().setClassState(aClass.list()).setCode(code).build());
            log(Stringify.format(code));
        } catch (ClassServerException e) {
            code = e.getResponseCode();
            responseObserver.onNext(ProfessorClassServer.ListClassResponse.newBuilder().setCode(code).build());
            logError(Stringify.format(code));
        }
        responseObserver.onCompleted();
        log("Success");
    }

    /**
     * cancels the enrollment of expressed student from the request
     *
     * @param request  
     * @param responseObserver  
     */
    @Override
    public void cancelEnrollment(ProfessorClassServer.CancelEnrollmentRequest request, StreamObserver<ProfessorClassServer.CancelEnrollmentResponse> responseObserver) {
        ClassesDefinitions.ResponseCode code = ClassesDefinitions.ResponseCode.OK;
        log("Trying to cancel enrollment");
        try {
            aClass.discard(request.getStudentId());
            log(Stringify.format(code));
        } catch (ClassServerException e) {
            code = e.getResponseCode();
            logError(Stringify.format(code));
        }
        log("Success");
        responseObserver.onNext(ProfessorClassServer.CancelEnrollmentResponse.newBuilder().setCode(code).build());
        responseObserver.onCompleted();
    }
}