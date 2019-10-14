import java.util.Objects;

public class Resource {

    private String resourceName;
    private int quantity;
    private boolean available;
    private int timeAvailable;

    public void setAvailable(boolean available) {
        this.available = available;
    }

    private boolean available;

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
    
    public boolean use(int quantity, int duration) {

        if (available) {

            if (this.quantity >= quantity) {
                this.quantity -= quantity;
                if (this.quantity == 0) {
                    available = false;
                }
                this.timeAvailable += duration;
                return true;
            }

        }
        return false;
    }

    public void release(int quantity) {

        this.quantity+=quantity;
        available = true;
    }
    
    public boolean isFree(int quantity) {

        if(available) {

            if (this.quantity >= quantity) {
                return true;
            }
        }
        return false;
    }
    
    public int getTimeAvailable() {
        return timeAvailable;
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
}
