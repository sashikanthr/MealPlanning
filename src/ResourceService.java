import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ResourceService {

    private static List<Resource> resourceList;

    public static List<Resource> loadResources(String resourceLocation) {
        resourceList = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(resourceLocation))) {
            Consumer<String> prepareResource = line -> {
                String[] resourceParameters = line.split(Constants.DELIMITER);
                Resource resource = new Resource();
                resource.setResourceName(resourceParameters[0]);
                resource.setQuantity(Integer.parseInt(resourceParameters[1]));
                resource.setOriginalQuantity(Integer.parseInt(resourceParameters[1]));
                resource.setAvailable(true);
                resourceList.add(resource);

            };
            stream.forEach(prepareResource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resourceList;
    }

    public static Resource getResource(String resourceName) {
        Predicate<Resource> findResource = resource -> resourceName.equalsIgnoreCase(resource.getResourceName());
        Optional<Resource> resourceOptional = resourceList.stream().filter(findResource).findFirst();
        if(resourceOptional.isPresent()) {
            return resourceOptional.get();
        } else {
            throw new RuntimeException("Resource not found with name.."+resourceName);
        }
    }

    //Returns true if resource is available for the required quantity.
    public static boolean checkAvailability(String resourceName,int quantityNeeded,int timeUnitsTaken) {

        Resource resource = ResourceService.getResource(resourceName);
        return resource.isFree(quantityNeeded,timeUnitsTaken);
    }

    //Returns true if resource is allocated for the required quantity else returns false.
    public static boolean useResource(Activity activity,String resourceName,int quantity,int timeUnitsTaken, TimeUnits timeUnitsNeeded) {
        Resource resource = ResourceService.getResource(resourceName);
        return resource.use(activity,quantity,timeUnitsTaken,timeUnitsNeeded.getTimeUnits());
    }

    //Adds quantity to the resource with the released amount.
    public static void releaseResource(Resource resource, int quantity) {
        resource.release(quantity);
    }

    public static void printResources() {

        for(Resource resource:resourceList) {
            System.out.println(resource);
        }
    }

    public static void releaseResourceQuantities(int timeUnitsTaken) {

        Consumer<? super Resource> resetQuantity = resource -> {
            resource.release(timeUnitsTaken);
        };
        resourceList.forEach(resetQuantity);
    }
    
    public static List<Resource> getResourceList() {
        return resourceList;
    }

    public static boolean areThereAnyResourceQueuesHavingThisActivity(Activity activity) {

        for(Resource resource:resourceList) {

            for(Resource.ResourceQueue resourceQueue:resource.getResourceQueue()) {

                if(activity.equals(resourceQueue.getActivity())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isResourceAlreadyAcquired(Activity activity,Resource resourceNeeded) {

        Resource resource = ResourceService.getResource(resourceNeeded.getResourceName());
            Predicate<Resource.ResourceQueue> findActivity = resourceQueue ->activity.equals(resourceQueue.getActivity());
            if(resource.getResourceQueue().stream().filter(findActivity).findFirst().isPresent()) {
                return true;
            } else {
                return false;
            }

    }

    public static boolean verifyIfAllResourcesAreAllocated(Activity activity) {

        for(Resource resourceNeeded:activity.getResourcesNeeded()) {
            Resource resource = ResourceService.getResource(resourceNeeded.getResourceName());
            for(Resource.ResourceQueue resourceQueue: resource.getResourceQueue()) {

                if (!resourceQueue.getActivity().equals(activity)) {

                    return false;
                }
            }
        }

        return true;
    }

    public static int calculateResourceIdleTime() {

        int resourceIdleTime = 0;

        for(Resource resource:resourceList) {

            resourceIdleTime+=resource.getQuantity();

        }

        return resourceIdleTime;

    }
}
