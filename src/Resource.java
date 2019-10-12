public class Resource {

    private String resourceName;
    private int quantity;
    private Boolean available;

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
    
    public Boolean isAvailable() {
        return available;
    }
    
    public void use() {
        if (available) {
            available = false;
        } else {
            //do something - tell to wait
        }
    }
    
    public void free() {
        // we should check that the one who is holding it is the one who frees 
        // it - use a semaphore/lock?
        available = true;
    }

    @Override
    public String toString() {
        return "Resource{" +
                "resourceName='" + resourceName + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
