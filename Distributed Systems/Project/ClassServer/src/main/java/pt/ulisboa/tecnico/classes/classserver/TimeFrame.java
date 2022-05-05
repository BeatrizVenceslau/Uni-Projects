package pt.ulisboa.tecnico.classes.classserver;

import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions;

import java.time.Instant;

public class TimeFrame {
    Instant start;
    Instant end;
    int capacity;

    public TimeFrame(Instant start, int capacity) {
        this.start = start;
        this.end = null;
        this.capacity = capacity;
    }

    /**
     * Transforms the grpc timestamp in a Instant. If the timestamp is the default, return null.
     *
     * @param timestamp
     * @return
     */
    private Instant toTimestamp(ClassesDefinitions.Timestamp timestamp) {
        if (Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos()) == Instant.ofEpochSecond(ClassesDefinitions.Timestamp.getDefaultInstance().getSeconds(), ClassesDefinitions.Timestamp.getDefaultInstance().getNanos()))
            return null;
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }
    
    public TimeFrame(ClassesDefinitions.EnrollableTimes timeframe) {
        this.start = toTimestamp(timeframe.getStart());
        this.end = toTimestamp(timeframe.getEnd());
        this.capacity = timeframe.getCapacity();
    }



    public void closeTimeframe(){
        end = Instant.now();
    }

    public Instant getEnd() {
        return end;
    }

    public Instant getStart() {
        return start;
    }

    public int getCapacity() {
        return capacity;
    }

    /**
     * Returns this class as proto object. If the end of the timeframe is null, then the timestamp is default.
     *
     * @return
     */
    public ClassesDefinitions.EnrollableTimes toEnrollTimes(){
        ClassesDefinitions.Timestamp end = ClassesDefinitions.Timestamp.getDefaultInstance();
        if (getEnd() != null)
                end = ClassesDefinitions.Timestamp.newBuilder()
                        .setSeconds(getEnd()
                                .getEpochSecond())
                        .setNanos(getEnd()
                                .getNano()
                        ).build();
        return
                ClassesDefinitions.EnrollableTimes
                .newBuilder()
                .setCapacity(getCapacity())
                .setStart(ClassesDefinitions.Timestamp
                        .newBuilder()
                        .setSeconds(getStart()
                                .getEpochSecond())
                        .setNanos(getStart()
                                .getNano()
                        ).build()
                ).setEnd(
                        end
                ).build();
    }
}
