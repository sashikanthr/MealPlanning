public class TimeUnits {

    private int timeUnits;
    //Need to represent timeUnits with actual time. 5 TUs can be 5 mins in the meal planning case.

    public int getTimeUnits() {
        return timeUnits;
    }

    public void setTimeUnits(int timeUnits) {
        this.timeUnits = timeUnits;
    }

    @Override
    public String toString() {
        return "TimeUnits{" +
                "timeUnits=" + timeUnits +
                '}';
    }
}
