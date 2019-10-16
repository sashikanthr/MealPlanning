//import com.sun.xml.internal.ws.util.StringUtils;

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

        String recipeLocation = System.getProperty("user.dir")+"\\Recipes.txt";
        String resourceLocation = System.getProperty("user.dir")+"\\AvailableResources.txt";
        if(args.length>0) {
            recipeLocation = args[0];
        } else if(args.length>1) {
            resourceLocation = args[1];
        }
        List<Recipe> recipes = RecipeService.loadRecipes(recipeLocation);
        System.out.println("Recipes Loaded..."+recipes.size());
        List<Resource> availableResources = ResourceService.loadResources(resourceLocation);
        System.out.println("Resources Loaded..."+availableResources.size());
       // ResourceService.printResources();
        if (checkMaxResourcesExist(recipes, availableResources)) {
            getBestOrder(recipes);
            System.out.println(RecipeService.getTotalTimeUnitsNeededForAllActivities());
        } else {
            System.out.println("Insufficient resources available.");
        }
        System.out.println(RecipeService.getTotalTimeUnitsNeededForAllActivities());
    }

    private static void getBestOrder(List<Recipe> allRecipes) {
        System.out.println("Starting the Genetic Algorithm to get the best sequence");
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(allRecipes);
        geneticAlgorithm.generateInitialPopulation();
        int bestFitnessValue = 715;
        Chromosome bestChromosome;
        int tempBestFitness;
        int maxItrs =0;
        do {
            geneticAlgorithm.selection();
            geneticAlgorithm.evaluatePopulation();
            geneticAlgorithm.sortPopulation();
            geneticAlgorithm.crossOver();
            geneticAlgorithm.mutation();
            geneticAlgorithm.evaluateOffSpring();
            geneticAlgorithm.sortOffspring();
            tempBestFitness = geneticAlgorithm.getBestFitnessValue();
            System.out.println("Best Fitness Value.."+tempBestFitness);
            System.out.println("Resource Idle Time for Best Chromosome..."+geneticAlgorithm.getBest().getResourceIdleTime());
            maxItrs++;

        }while(maxItrs<Constants.MAX_ITRS);
        System.out.println("Found the best sequence after iterations:"+maxItrs);
        System.out.println("Offspring Fitness.."+geneticAlgorithm.getBestOffspringFitness());
        System.out.println("Parent Fitness.."+geneticAlgorithm.getBestParentFitness());

        System.out.println("Best Genes.."+geneticAlgorithm.getBest().getGenes());


    }

    private static boolean checkMaxResourcesExist(List<Recipe> recipes, List<Resource> available) {
        if (ResourceService.getResource(Constants.HUMAN).getOriginalQuantity() == 0) {
            return false;
        }
        for (Recipe recipe : recipes) {
            List<Activity> activities = recipe.getActivities();
            for (Activity activity : activities) {
                List<Resource> needed = activity.getResourcesNeeded();
                for (Resource r : needed) {
                    Resource resource = ResourceService.getResource(r.getResourceName());
                    if(r.getQuantity()>resource.getQuantity())
                        return false;
                    }
                }
            }

        return true;
    }


}
