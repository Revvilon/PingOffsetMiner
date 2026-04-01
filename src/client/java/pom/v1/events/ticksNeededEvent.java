package pom.v1.events;

public class ticksNeededEvent {
    public double ticksNeeded;
    public double ticksElapsed;

    public ticksNeededEvent(double ticksNeeded, double ticksElapsed) {
        this.ticksElapsed = ticksElapsed;
        this.ticksNeeded = ticksNeeded;
    }
}
