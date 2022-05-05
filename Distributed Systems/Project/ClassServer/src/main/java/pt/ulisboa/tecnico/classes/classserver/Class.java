package pt.ulisboa.tecnico.classes.classserver;

import pt.ulisboa.tecnico.classes.DebugMode;
import pt.ulisboa.tecnico.classes.FastLogger;
import pt.ulisboa.tecnico.classes.Stringify;
import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions;
import pt.ulisboa.tecnico.classes.contract.classserver.ClassServerClassServer;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Class {
    private static final char P = 'P';
    private ConcurrentHashMap<String, StudentClass> discarded = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, StudentClass> enrolled = new ConcurrentHashMap<>();
    private SortedMap<String, Instant> studentTimeline = new TreeMap<>();
    private SortedMap<Instant, TimeFrame> enrollablesTimes = new TreeMap<>(Collections.reverseOrder());
    private int capacity = 0;
    private boolean active = true;
    private boolean enrollmentsOpen = false;
    private char flag;
    private boolean gossip = true;

    public Class(char flag) {
        super();
        this.flag = flag;
    }

    public LinkedList<TimeFrame> getEnrollablesTimes() {
        return new LinkedList<>(enrollablesTimes.values().stream().toList());
    }

    public synchronized void toggleGossip() {
        gossip = !gossip;
    }

    public synchronized boolean getGossip(){
        return gossip;
    }

    private synchronized void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    private synchronized void setEnrollmentOpen(boolean enrolmentsOpen) {
        this.enrollmentsOpen = enrolmentsOpen;
    }

    private synchronized void setActive(boolean active) {
        this.active = active;
    }

    public synchronized boolean getActive() {
        return active;
    }

    public synchronized boolean getOpenEnrollments() {
        return enrollmentsOpen;
    }

    public synchronized int getCapacity() {
        return capacity;
    }

    private void log(String info) {
        if (DebugMode.isDebug())
            FastLogger.log(true, Class.class.getName(), info);
    }

    private void logError(String info){
        if (DebugMode.isDebug())
            FastLogger.logError(true, Class.class.getName(), info);
    }

    /**
     * checks for error scenarios and enrolls given student
     *
     * @param student
     */
    public synchronized void enroll(ClassesDefinitions.Student student) throws ClassServerException {
        log("Client requesting to enroll");
        if (!getActive()) {
            logError("Server inactive");
            throw new ClassServerException(ClassesDefinitions.ResponseCode.INACTIVE_SERVER);
        }

        if (!getOpenEnrollments()) {
            logError("Enrollments already closed");
            throw new ClassServerException(ClassesDefinitions.ResponseCode.ENROLLMENTS_ALREADY_CLOSED);
        }
        if (enrolled.get(student.getStudentId())  != null) {
            logError("Student already enrolled");
            throw new ClassServerException(ClassesDefinitions.ResponseCode.STUDENT_ALREADY_ENROLLED);
        }
        if (enrolled.mappingCount() >= getCapacity()) {
            logError("Class is already full");
            throw new ClassServerException(ClassesDefinitions.ResponseCode.FULL_CLASS);
        }

        StudentClass studentClass = new StudentClass(student.getStudentName(), student.getStudentId(), Instant.now());

        setTimestamp(studentClass);
        enrolled.put(studentClass.getStudentId(), studentClass);
        discarded.remove(studentClass.getStudentId());
        log("Student successfully enrolled");
    }

    /**
     * Sets timestamp for studentClass
     *
     * @param studentClass
     */
    public synchronized void setTimestamp(StudentClass studentClass){
        studentClass.setTimestamp(Instant.now());
        studentTimeline.put(studentClass.getStudentId(), studentClass.getTimestamp());
    }

    /**
     * checks for error scenarios and discards the enrollment of given student
     *
     * @param studentId
     */
    public synchronized void discard(String studentId) throws ClassServerException {
        log("Trying to cancel "+studentId+"'s enrollment");
        if (!getActive()) {
            logError("Server inactive");
            throw new ClassServerException(ClassesDefinitions.ResponseCode.INACTIVE_SERVER);
        }

        StudentClass student = enrolled.get(studentId);
        if (student == null && discarded.get(studentId) == null) {
            logError("Non-existing student");
            throw new ClassServerException(ClassesDefinitions.ResponseCode.NON_EXISTING_STUDENT);
        }
        if (student != null) {
            enrolled.remove(studentId);
            setTimestamp(student);
            discarded.put(studentId, student);
            log(studentId+"'s enrollment was cancelled");
            return;
        }
        log("Student had already been discarded no new operation was done");
    }

    /**
     * checks for error scenarios and closes enrollments
     */
    public synchronized void closeEnrollments() throws ClassServerException {
        log("Client trying to close enrollments");
        if (!getActive()) {
            logError("Server inactive");
            throw new ClassServerException(ClassesDefinitions.ResponseCode.INACTIVE_SERVER);
        }
        if (flag != P){
            logError("Can not write to non primary server");
            throw new ClassServerException(ClassesDefinitions.ResponseCode.WRITING_NOT_SUPPORTED);
        }
        if (!getOpenEnrollments()) {
            logError("Enrollments already closed");
            throw new ClassServerException(ClassesDefinitions.ResponseCode.ENROLLMENTS_ALREADY_CLOSED);
        }

        setEnrollmentOpen(false);
        enrollablesTimes.get(enrollablesTimes.firstKey()).closeTimeframe();
        log("Enrollments closed");
    }

    /**
     * checks for error scenarios and opens enrollments with said capacity
     *
     * @param newCapacity
     */
    public synchronized void openEnrollments(int newCapacity) throws ClassServerException {
        log("Client trying to open enrollments");
        if (!getActive()) {
            logError("Server inactive");
            throw new ClassServerException(ClassesDefinitions.ResponseCode.INACTIVE_SERVER);
        }
        if (flag != P){
            logError("Can not write to non primary server");
            throw new ClassServerException(ClassesDefinitions.ResponseCode.WRITING_NOT_SUPPORTED);
        }
        if (getOpenEnrollments()) {
            logError("Enrollments already open");
            throw new ClassServerException(ClassesDefinitions.ResponseCode.ENROLLMENTS_ALREADY_OPENED);
        }
        if (enrolled.mappingCount() >= newCapacity) {
            logError("Invalid capacity");
            throw new ClassServerException(ClassesDefinitions.ResponseCode.FULL_CLASS);
        }

        setEnrollmentOpen(true);
        setCapacity(newCapacity);
        TimeFrame timeFrame = new TimeFrame(Instant.now(), newCapacity);
        enrollablesTimes.put(timeFrame.getStart(), timeFrame);
        log("Enrollments open with "+newCapacity+" capacity");
    }

    /**
     * activates the server
     */
    public synchronized void activateServer() {
        log("Client trying to activate server");
        setActive(true);
        log("Server activated");
    }

    /**
     * deactivates the server
     */
    public synchronized void deactivateServer() {
         log("Client trying to deactivate the server");
         setActive(false);
         log("Server deactivated");
    }

    /**
     * checks for error scenario and returns the state of the server(for student and professor)
     * 
     * @return          class state
     */
    public ClassesDefinitions.ClassState list() throws ClassServerException {
        log("Client requesting class state");
        if (!getActive()) {
            logError("Server inactive");
            throw new ClassServerException(ClassesDefinitions.ResponseCode.INACTIVE_SERVER);
        }
        return classStateToProtoWrap();
    }

    /**
     * returns the state of the server(for admin)
     * 
     * @return          class state
     */
    public ClassesDefinitions.ClassState dump() throws ClassServerException {
        log("Client requesting class state dump");
        return classStateToProtoWrap();
    }

    /**
     * auxiliary function that returns the state of the server
     * 
     * @return          class state
     */
    public ClassesDefinitions.ClassState classStateToProtoWrap(){
        log("Preparing class state to be sent");
        ClassesDefinitions.ClassState classState = classStateToProto();
        log("Current "+ Stringify.format(classState));
        return classState;
    }

    /**
     * builds the class state
     * 
     * @return          class state
     */
    private synchronized ClassesDefinitions.ClassState classStateToProto() {
        return ClassesDefinitions.ClassState.newBuilder()
                .setOpenEnrollments(getOpenEnrollments())
                .addAllEnrolled(enrolled
                        .values()
                        .stream()
                        .map(studentClass -> studentClass.toStudent())
                        .collect(Collectors.toList())
                )
                .addAllDiscarded(discarded.values()
                        .stream()
                        .map(studentClass -> studentClass.toStudent())
                        .collect(Collectors.toList())
                )
                .setCapacity(getCapacity())
                .build();
    }

    public synchronized ClassServerClassServer.PropagateStateRequest createPropagateRequest() {
        return ClassServerClassServer.PropagateStateRequest.newBuilder()
                .setClassState(classStateToProtoWrap())
                .addAllEnrollableTimes(enrollablesTimes
                        .values()
                        .stream()
                        .map(timeFrame -> timeFrame.toEnrollTimes())
                        .collect(Collectors.toList())
                ).build();
    }

    public void setDiscarded(ConcurrentHashMap<String, StudentClass> discarded) {
        this.discarded = discarded;
    }

    public void setEnrolled(ConcurrentHashMap<String, StudentClass> enrolled) {
        this.enrolled = enrolled;
    }

    public void setStudentTimeline(SortedMap<String, Instant> studentTimeline) {
        this.studentTimeline = studentTimeline;
    }

    public void setEnrollablesTimes(SortedMap<Instant, TimeFrame> enrollablesTimes) {
        this.enrollablesTimes = enrollablesTimes;
    }

    /**
     * creates the class state for the class received in propagation
     * 
     * @param state
     * @param timeframes
     */
    public synchronized Class createReceivedClass(ClassesDefinitions.ClassState state, List<TimeFrame> timeframes) throws ClassServerException {
        if (!getActive()) {
            logError("Server inactive");
            throw new ClassServerException(ClassesDefinitions.ResponseCode.INACTIVE_SERVER);
        }

        //Discarded, Enrollables
        Class receivedClass = new Class('S'); // flag is not used in this case
        receivedClass.setEnrollmentOpen(state.getOpenEnrollments());
        receivedClass.setEnrolled(new ConcurrentHashMap<>(state.getEnrolledList().stream()
                .collect(Collectors.toMap(ClassesDefinitions.Student::getStudentId, item -> new StudentClass(item)))));

        receivedClass.setDiscarded(new ConcurrentHashMap<>(state.getDiscardedList().stream()
                .collect(Collectors.toMap(ClassesDefinitions.Student::getStudentId, item -> new StudentClass(item)))));
        receivedClass.setCapacity(state.getCapacity());

        //Enrollable Times
        SortedMap<Instant, TimeFrame> treeMap = new TreeMap<>(Collections.reverseOrder());
        treeMap.putAll(timeframes.stream()
                .collect(Collectors.toMap(TimeFrame::getStart, item -> item)));
        receivedClass.setEnrollablesTimes(treeMap);

        //Student Timeline
        SortedMap<String, Instant> sortedMap = new TreeMap<>();
        sortedMap.putAll(receivedClass.enrolled.values().stream()
                .collect(Collectors.toMap(StudentClass::getStudentId, StudentClass::getTimestamp)));
        sortedMap.putAll(receivedClass.discarded.values().stream()
                .collect(Collectors.toMap(StudentClass::getStudentId, StudentClass::getTimestamp)));
        receivedClass.setStudentTimeline(sortedMap);

        log("Create received class");
        return receivedClass;
    }

    /**
     * if gossip is active toggles it
     */
    public synchronized void deactivateGossip(){
        log("Tries to deactivated gossip");
        if (getGossip())
            toggleGossip();
        log("Deactivated gossip");
    }

    /**
     * if gossip is not active toggles it
     */
    public synchronized void activateGossip(){
        log("Tries to activated gossip");
        if (!getGossip())
            toggleGossip();
        log("Activated gossip");
    }


    /**
     * If student was enrolled within a valid timeframe
     *
     * @param student
     * @return Timeframe    the time frame in which the studt was enrolled
     */
    private TimeFrame inEnrollableTimeframe(StudentClass student) {
        for (TimeFrame timeframe : enrollablesTimes.values()) {
            if (student.in(timeframe)) {
                return timeframe;
            }
        }
        return null;
    }

    public class StudentTimestampComparator implements Comparator<StudentClass> {

        @Override
        public int compare(StudentClass firstStudent, StudentClass secondStudent) {
            return firstStudent.getTimestamp().compareTo(secondStudent.getTimestamp());
        }
    }

    /**
     * resolves a conflict between enrollments
     * when the action upon a student changes the timeline will be updated
     *
     * @param receivedStudent
     */
    public synchronized void conflictEnroll(StudentClass receivedStudent){
        log("Solving conflict enrollment");
        if (enrolled.get(receivedStudent.getStudentId()) != null)
            return;

        //checks to see if the student can be enrolled
        TimeFrame timeFrame = inEnrollableTimeframe(receivedStudent);
        if (timeFrame == null) {
            conflictDiscard(receivedStudent);
            return;
        }

        //sorts students by timestamp
        StudentTimestampComparator studentTimestampComparatorComparator = new StudentTimestampComparator();
        LinkedList<StudentClass> sortedStudents = new LinkedList<>(enrolled.values());
        Collections.sort(sortedStudents, studentTimestampComparatorComparator);

        //if there are too many students
        if (sortedStudents.size() >= timeFrame.getCapacity()) {
            StudentClass lastStudent = sortedStudents.getLast();
            //If last enrolled student time > student received time
            if (getTimestamp(lastStudent.getStudentId()).isAfter(receivedStudent.getTimestamp())) {
                conflictDiscard(lastStudent);
            } else {
                conflictDiscard(receivedStudent);
                return;
            }
        }

        enrolled.put(receivedStudent.getStudentId(), receivedStudent);
        studentTimeline.put(receivedStudent.getStudentId(), receivedStudent.getTimestamp());
        discarded.remove(receivedStudent.getStudentId());
        log("Solved conflict enrollment");
    }

    /**
     * resolves a conflict by discarding a student
     * when the action upon a student changes the timeline will be updated
     *
     * @param receivedStudent
     */
    public synchronized void conflictDiscard(StudentClass receivedStudent) {
        log("Tries to solve conflict discard");
        discarded.put(receivedStudent.getStudentId(), receivedStudent);
        studentTimeline.put(receivedStudent.getStudentId(), receivedStudent.getTimestamp());
        enrolled.remove(receivedStudent.getStudentId());
        log("Solves conflict discard");
    }

    public Collection<String> getStudentIds(){
        return studentTimeline.keySet();
    }


    //finds the timestamp for the last action upon the student
    public synchronized Instant getTimestamp(String studentId){
        Instant timestamp = studentTimeline.get(studentId);
        if (timestamp == null)
            return Instant.MIN;
        return timestamp;
    }

    /**
     * resolves conflicts that come from merging the local students with received ones
     *
     * @param receivedState
     */
    public synchronized void mergeStudents(Class receivedState){
        log("Will merge students");
        for (String studentId : receivedState.getStudentIds()) {
            //If timestamp received > my timestamp or if my timestamp is null
            if (receivedState.getTimestamp(studentId).isAfter(getTimestamp(studentId))) {
                if (receivedState.enrolled.get(studentId) != null) {
                   conflictEnroll(receivedState.enrolled.get(studentId));
                } else {
                    conflictDiscard(receivedState.discarded.get(studentId));
                }
            }
        }
        log("Merged students");
    }

    /**
     * resolves conflicts that come from updating the timeline in the local server
     *
     * @param timeFramesOrderedFromMostRecent
     */
    public synchronized void correctTimeline (LinkedList<TimeFrame> timeFramesOrderedFromMostRecent) {
        log("Tries to update the timeline");
        //receives an un-updated timeline - ignores it
        if (timeFramesOrderedFromMostRecent.size() == 0)
            return;
        //has an un-updated timeline - takes the given one
        if (enrollablesTimes.size() == 0)
            enrollablesTimes.putAll(timeFramesOrderedFromMostRecent.stream().collect(Collectors.toMap(TimeFrame::getStart, item -> item)));

        TimeFrame currentLocalFrame = enrollablesTimes.get(enrollablesTimes.firstKey());

        //if they have the same timeframes
        if (timeFramesOrderedFromMostRecent.size() == enrollablesTimes.size()) {
            //sees if the received one has a more updated end timestamp
            if(timeFramesOrderedFromMostRecent.getFirst().getEnd() != null && !timeFramesOrderedFromMostRecent.getFirst().getEnd().equals(currentLocalFrame.getEnd())) {
                enrollablesTimes.put(enrollablesTimes.firstKey(), timeFramesOrderedFromMostRecent.getFirst());
            }
        }
        //if the received timeline is more recent
        else if(timeFramesOrderedFromMostRecent.size() > enrollablesTimes.size()) {
            for (TimeFrame timeFrame : timeFramesOrderedFromMostRecent) {
                enrollablesTimes.put(timeFrame.getStart(), timeFrame);

                //stops when it gets to the last one it had locally
                if(timeFrame.getStart().equals(currentLocalFrame.getStart()))
                    break;
            }
        } else { //is more recent than received
            return;
        }
        log("Updated the timeline");

        log("Checks validity of local enrollments");
        //checks validity of local students with new timeline
        //if end == null enrollments still open else enrollments closed
        setEnrollmentOpen(timeFramesOrderedFromMostRecent.getFirst().getEnd() == null);
        setCapacity(timeFramesOrderedFromMostRecent.getFirst().getCapacity());
        for (String studentId : studentTimeline.keySet()) {
            StudentClass student = enrolled.get(studentId);
            if(student != null) {
                if (!isEnrollable(student)) {
                        conflictDiscard(student);
                }
            }
        }
        log("Validated local enrollements");
    }

    /**
     * checks if student can be enrolled
     * is in valid timeframe and class is not full
     *
     * @param student
     */
    private boolean isEnrollable(StudentClass student) {
        for (TimeFrame timeframe : enrollablesTimes.values()) {
            if (student.in(timeframe) && enrolled.size() <= timeframe.getCapacity()) {
                return true;
            }
        }
        return false;
    }
}
