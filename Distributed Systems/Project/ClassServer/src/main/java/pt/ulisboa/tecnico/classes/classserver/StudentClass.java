package pt.ulisboa.tecnico.classes.classserver;

import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions;

import java.time.Instant;

public class StudentClass {

    Instant timestamp;
    String id;
    String name;

    public StudentClass(String name, String id, Instant timestamp) {
        setId(id);
        setTimestamp(timestamp);
        setName(name);
    }

    /**
     * returns the timestamp of the student's last action
     *
     * @param student
     * @return         timestamp of the student's last action
     */
    private Instant toTimestamp(ClassesDefinitions.Student student) {
        return Instant.ofEpochSecond(student.getLastAction().getSeconds(), student.getLastAction().getNanos());
    }

    public StudentClass(ClassesDefinitions.Student student) {
        setId(student.getStudentId());
        setTimestamp(toTimestamp(student));
        setName(student.getStudentName());
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getStudentId() {
        return id;
    }

    public String getName() {
        return name;
    }

    /**
     * Checks if the timestamp is includend in the timeframe
     *
     * @param timeFrame
     * @return            true if the timestamp is after the start of the timeframe and before its end
     */
    public boolean in(TimeFrame timeFrame) {
        if (getTimestamp().isAfter(timeFrame.getStart()))
            if (timeFrame.getEnd() == null || getTimestamp().isBefore(timeFrame.getEnd()))
                return true;
        return false;
    }
    
    /**
     * Returns student class as proto object
     *
     * @return
     */
    public ClassesDefinitions.Student toStudent() {
        Instant now = getTimestamp();
        return ClassesDefinitions.Student.newBuilder()
                .setStudentId(getStudentId())
                .setLastAction(ClassesDefinitions.Timestamp.newBuilder().setSeconds(now.getEpochSecond()).setNanos(now.getNano()).build())
                .setStudentName(getName())
                .build();
    }
}
