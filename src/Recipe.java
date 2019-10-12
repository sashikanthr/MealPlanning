import java.util.ArrayList;
import java.util.List;

public class Recipe {

    private int priority;
    List<Activity> activityList = new ArrayList<>();
    private String name;

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

    @Override
    public String toString() {
        return "Recipe{" +
                "priority=" + priority +
                ", name='" + name + '\'' +
                '}';
    }
}
