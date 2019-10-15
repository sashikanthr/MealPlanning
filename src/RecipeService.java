import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class RecipeService {

    private static List<Recipe> recipes;

    public static List<Recipe> loadRecipes(String recipeLocation) {

        recipes = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(recipeLocation))) {
            Consumer<String> prepareRecipe = line -> {

                if("*".equalsIgnoreCase(line)) {
                    recipes.add(new Recipe());
                } else {

                    Recipe recipe = recipes.get(recipes.size()-1);
                    if(line.split(Constants.DELIMITER).length>2) {
                        recipe.addActivity(prepareActivity(line));
                    } else {
                        String[] parameters = line.split(Constants.DELIMITER);
                        recipe.setPriority(Integer.parseInt(parameters[0]));
                        recipe.setName(parameters[1]);
                    }
                }
            };
            stream.forEach(prepareRecipe);
            recipes.removeIf(recipe->recipe.getActivities().isEmpty());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return recipes;
    }

    private static void printRecipes(List<Recipe> allRecipes) {

        Consumer<? super Recipe> printRecipe = recipe -> {

            System.out.println(recipe);
            recipe.getActivities().forEach(System.out::println);
        };
        allRecipes.forEach(printRecipe);
    }

    private static Activity prepareActivity(String line) {

        Activity activity = new Activity();

        String[] parameters = line.split(Constants.DELIMITER);
        activity.setPriority(Double.parseDouble(parameters[0]));
        if("1".equalsIgnoreCase(parameters[1])) {
            activity.setHumanNeeded(true);
        } else {
            activity.setHumanNeeded(false);
        }
        activity.setAction(parameters[2]);
        activity.setResourcesNeeded(prepareResourceList(parameters[3]));
        activity.setTimeUnitsNeeded(prepareTimeUnits(parameters[4]));

        return activity;
    }

    private static TimeUnits prepareTimeUnits(String parameter) {

        TimeUnits timeUnit = new TimeUnits();
        timeUnit.setTimeUnits(Integer.parseInt(parameter));
        return timeUnit;
    }

    private static List<Resource> prepareResourceList(String parameter) {

        List<Resource> resources = new ArrayList<>();
        String[] resourceParameters = parameter.split(Constants.RESOURCE_DELIMITER);
        for(String resource:resourceParameters) {
            Resource resourcesNeeded = prepareResource(resource);
            if(resourcesNeeded.getResourceName()!=null) {
                resources.add(resourcesNeeded);
            }
        }
        return resources;
    }

    private static Resource prepareResource(String resourceParameter) {

        Resource resource = new Resource();
        if(resourceParameter!=null && !resourceParameter.isEmpty()) {
            String[] resourceParameterValues = resourceParameter.trim().split(Constants.RESOURCE_QUANTITY_DELIMITER);
            resource.setResourceName(resourceParameterValues[0].trim());
            resource.setQuantity(Integer.parseInt(resourceParameterValues[1].trim()));
        }
        return resource;
    }

    public static Activity getActivityForRecipe(int recipeIndex,int activityIndex) {

        return recipes.get(recipeIndex).getActivities().get(activityIndex);
    }

    /*
    Get the predecessor activity along with the priority. If the difference in priority is more than 1 steps,
    the predecessor should have been completed first. Otherwise it can be run concurrently.
     */

    public static boolean areAllPreviousActivitiesComplete(int recipeIndex,int activityIndex) {

        Recipe recipe = recipes.get(recipeIndex);
        Activity activity = getActivityForRecipe(recipeIndex,activityIndex);
        double priority= activity.getPriority();
        for(int i=0;i<activityIndex;i++) {

            Activity predecessorActivity = recipe.getActivities().get(i);
            if(priority - predecessorActivity.getPriority()>=1) {
                if(!predecessorActivity.isActivityComplete()) {
                    return false;
                }
            };

        }

        return true;
    }
    
    public static List<Activity> getPreviousActivies (int recipeIndex, int activityIndex) {
        Recipe recipe = recipes.get(recipeIndex);
        Activity activity = getActivityForRecipe(recipeIndex,activityIndex);
        double priority= activity.getPriority();
        
        List<Activity> prev = new ArrayList<>();
        for(int i = 0; i < activityIndex; i++) {
            prev.add(recipe.getActivities().get(i));
        }
        
        return prev;
    }

    public static void resetCompleteStatusOnAllActivities() {

        for(Recipe recipe:recipes) {

            for(Activity activity:recipe.getActivities()) {
                activity.setActivityComplete(false);
            }
        }
    }

    public static int getTotalTimeUnitsNeededForAllActivities() {

        int timeUnitsNeeded = 0;

        for(Recipe recipe:recipes) {

            for(Activity activity:recipe.getActivities()) {
                timeUnitsNeeded+=activity.getTimeUnitsNeeded().getTimeUnits();
            }
        }

        return timeUnitsNeeded;
    }
    
    public static int getTimeUnitsForActivitySet(List<Activity> activities) {
        int timeUnitsNeeded = 0;
        
        for (Activity activity : activities) {
            timeUnitsNeeded += activity.getTimeUnitsNeeded().getTimeUnits();
        }
    }

    public static Recipe getRecipe(int recipeIndex) {
        return recipes.get(recipeIndex);
    }
}
