import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Resource {

    private String resourceName;
    private int quantity;
    private boolean available;

    public List<ResourceQueue> getResourceQueue() {
        return resourceQueue;
    }

    List<ResourceQueue> resourceQueue = new ArrayList<>();

    public void setAvailable(boolean available) {
        this.available = available;
    }
    public int getOriginalQuantity() {
        return originalQuantity;
    }

    public void setOriginalQuantity(int originalQuantity) {
        this.originalQuantity = originalQuantity;
    }

    private int originalQuantity;


    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public boolean isAvailable() {
        return available;
    }

    /*
    Adds resource with the specified quantity to the resource queue.
    It also records the current running time unit and the number of
    time units it has to have the lock.
     */
    
    public boolean use(Activity activity, int quantity,int timeUnitCounterItIsLockedAt, int timeUnitsNeeded) {

        if (this.quantity >= quantity) {

            ResourceQueue resource = new ResourceQueue();
            resource.setQuantityAcquired(quantity);
            resource.setTimeUnitCounterItIsLockedAt(timeUnitCounterItIsLockedAt);
            resource.setTimeUnitsNeeded(timeUnitsNeeded);
            resource.setActivity(activity);
            resourceQueue.add(resource);
            this.quantity -= quantity;
            return true;
        } else {
            throw new RuntimeException("Resource Not Available");
        }
    }

    /*
    Resource queue holds the information of the time unit step it was acquired
    and the number of time units it has to wait to release the resource.
    This method takes the current time unit step as the input and resources are released based
    on the time difference.
     */

    public void release(int timeUnitsTaken) {

        if(!resourceQueue.isEmpty()) {
            List<ResourceQueue> resourcesToBeRemovedFromQueue = new ArrayList<>();
            for(ResourceQueue resource:resourceQueue) {

                if(timeUnitsTaken-resource.timeUnitCounterItIsLockedAt>=resource.timeUnitsNeeded) {
                    this.quantity+=resource.getQuantityAcquired();
                    resourcesToBeRemovedFromQueue.add(resource);
                }
            }
            if(!resourcesToBeRemovedFromQueue.isEmpty()) {
                for(ResourceQueue resource:resourcesToBeRemovedFromQueue) {
                    resourceQueue.remove(resource);
                }
            }
        }
    }

    /*
    Checks if the required quantity of the resource is available or not.
    It will also verify the resource queue if any resource could be released
    in the current time step.
     */
    
    public boolean isFree(int quantityNeeded, int timeUnitsTaken) {

          if(this.quantity>=quantityNeeded) {
                return true;
            }

          if(!resourceQueue.isEmpty()) {
              List<ResourceQueue> resourcesToBeRemovedFromQueue = new ArrayList<>();
              for(ResourceQueue resource:resourceQueue) {

                  if(timeUnitsTaken-resource.timeUnitCounterItIsLockedAt>=resource.timeUnitsNeeded) {
                      this.quantity+=resource.getQuantityAcquired();
                      resourcesToBeRemovedFromQueue.add(resource);
                  }
              }
              if(!resourcesToBeRemovedFromQueue.isEmpty()) {
                  for(ResourceQueue resource:resourcesToBeRemovedFromQueue) {
                      resourceQueue.remove(resource);
                  }
              }
          }

        if(this.quantity>=quantityNeeded) {
            return true;
        }


        return false;
    }

    @Override
    public String toString() {
        return "Resource{" +
                "resourceName='" + resourceName + '\'' +
                ", quantity=" + quantity +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resource resource = (Resource) o;
        return Objects.equals(resourceName, resource.resourceName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resourceName);
    }

    /*
    The ResourceQueue instance is to hold the information of an activity that acquired the resource,
    the time unit step where this resource was acquired, the quantity of resource acquired and the
    amount of time units the resource has to be locked.
     */

    class ResourceQueue {

        public int getQuantityAcquired() {
            return quantityAcquired;
        }

        public void setQuantityAcquired(int quantityAcquired) {
            this.quantityAcquired = quantityAcquired;
        }

        public int getTimeUnitCounterItIsLockedAt() {
            return timeUnitCounterItIsLockedAt;
        }

        public void setTimeUnitCounterItIsLockedAt(int timeUnitCounterItIsLockedAt) {
            this.timeUnitCounterItIsLockedAt = timeUnitCounterItIsLockedAt;
        }

        public int getTimeUnitsNeeded() {
            return timeUnitsNeeded;
        }

        public void setTimeUnitsNeeded(int timeUnitsNeeded) {
            this.timeUnitsNeeded = timeUnitsNeeded;
        }

        private int quantityAcquired;

        private int timeUnitCounterItIsLockedAt;

        private int timeUnitsNeeded;

        public Activity getActivity() {
            return activity;
        }

        public void setActivity(Activity activity) {
            this.activity = activity;
        }

        private Activity activity;
    }
}
