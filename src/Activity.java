import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Activity {

    private double priority;
    private boolean isHumanNeeded;
    private String action;
    List<Resource> resourcesNeeded = new ArrayList<>();
    private TimeUnits timeUnitsNeeded;

    public boolean isActivityComplete() {
        return isActivityComplete;
    }

    public void setActivityComplete(boolean activityComplete) {
        isActivityComplete = activityComplete;
    }

    private boolean isActivityComplete;

    public double getPriority() {
        return priority;
    }

    public void setPriority(double priority) {
        this.priority = priority;
    }

    public boolean isHumanNeeded() {
        return isHumanNeeded;
    }

    public void setHumanNeeded(boolean humanNeeded) {
        isHumanNeeded = humanNeeded;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<Resource> getResourcesNeeded() {
        return resourcesNeeded;
    }

    public void setResourcesNeeded(List<Resource> resourcesNeeded) {
        this.resourcesNeeded = resourcesNeeded;
    }

    public TimeUnits getTimeUnitsNeeded() {
        return timeUnitsNeeded;
    }

    public void setTimeUnitsNeeded(TimeUnits timeUnitsNeeded) {
        this.timeUnitsNeeded = timeUnitsNeeded;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "priority=" + priority +
                ", isHumanNeeded=" + isHumanNeeded +
                ", action='" + action + '\'' +
                ", resourcesNeeded=" + resourcesNeeded +
                ", timeUnitsNeeded=" + timeUnitsNeeded +
                '}';
    }
}
