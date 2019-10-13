import com.sun.xml.internal.ws.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class MealPlanning {

    public static void main(String[] args) {

        //Read Recipes. File location can be mentioned in the args. If not mentioned, it will take the default location

        String fileLocation = System.getProperty("user.dir")+"\\Recipes.txt";
        if(args.length>0) {
            fileLocation = args[0];
        }
        List<Recipe> allRecipes = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(fileLocation))) {
            Consumer<String> prepareRecipe = line -> {

                if("*".equalsIgnoreCase(line)) {
                    allRecipes.add(new Recipe());
                } else {

                    Recipe recipe = allRecipes.get(allRecipes.size()-1);
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
            allRecipes.removeIf(recipe->recipe.getActivities().isEmpty());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //printRecipes(allRecipes);
        getBestOrder(allRecipes);
    }

    private static void getBestOrder(List<Recipe> allRecipes) {

        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(allRecipes);
        List<Chromosome> population = geneticAlgorithm.generateInitialPopulation();
        /*for(Chromosome chromosome:population) {
            System.out.println();
            chromosome.representation();

        }*/
        geneticAlgorithm.crossOver();

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
            resources.add(prepareResource(resource));
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
}
