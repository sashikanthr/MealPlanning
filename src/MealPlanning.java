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

        String recipeLocation = System.getProperty("user.dir")+"\\Recipes.txt";
        String resourceLocation = System.getProperty("user.dir")+"\\AvailableResources.txt";
        if(args.length>0) {
            recipeLocation = args[0];
        } else if(args.length>1) {
            resourceLocation = args[1];
        }
        List<Recipe> recipes = RecipeService.loadRecipes(recipeLocation);
        List<Resource> availableResources = ResourceService.loadResources(resourceLocation);
       // ResourceService.printResources();
        getBestOrder(recipes);
        System.out.println(RecipeService.getTotalTimeUnitsNeededForAllActivities());
    }

    private static void getBestOrder(List<Recipe> allRecipes) {

        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(allRecipes);
        List<Chromosome> population = geneticAlgorithm.generateInitialPopulation();
        geneticAlgorithm.evaluatePopulation();
        geneticAlgorithm.crossOver();
        geneticAlgorithm.evaluateOffSpring();
        geneticAlgorithm.getBestFitnessValue();

    }


}
