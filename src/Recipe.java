import java.util.ArrayList;
import java.util.List;

public class Recipe {

    private int priority;
    List<Activity> activityList = new ArrayList<>();
    private String name;
    private int timeStarted, timeEnded;
    private TimeUnits waitTime;

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    public List<Activity> getActivities() {
        return activityList;
    }
    
    // time spent between 2 activities in this recipe while we had to wait
    // for resources to become available
    public TimeUnits getWaitTime() {
        return waitTime;
    }
    
    // the time the first activity in this recipe began
    public int getTimeStarted() {
        return timeStarted;
    }
    
    // the time the last activity in this recipe was completed
    public int getTimeEnded() {
        return timeEnded;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "priority=" + priority +
                ", name='" + name + '\'' +
                '}';
    }
}
