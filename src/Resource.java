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
