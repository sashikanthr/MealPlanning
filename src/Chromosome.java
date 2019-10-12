import java.util.ArrayList;
import java.util.List;

public class Chromosome {

    /*
    Each chromosome is made of 2n+m genes. where n is the number of activities and m is the number of recipes.
    The first n genes are used to determine the delay time used at each of the n iterations of the scheduling procedure.
    The last m genes are used to determine the time each recipe can wait
    */

    List<String> genes;

    Chromosome(List<Recipe> recipes) {
        int n=0;
        for(Recipe recipe:recipes) {
            n+=recipe.getActivities().size();
        }
        genes = new ArrayList<>(2*n+recipes.size());
        prepareGenes(recipes);
    }

    private void prepareGenes(List<Recipe> recipes) {



    }

}
