import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Chromosome {

    /*
    Each chromosome is made of n genes. where n is the total number of activities. Each gene is represented as a string of recipe number and the activity number.
    Once all genes are added to a list. The list is shuffled.
    */

    private List<Gene> genes;

    Chromosome(List<Recipe> recipes) {

        genes = new ArrayList<>();
        for (Recipe recipe : recipes) {
            List<Activity> activities = recipe.getActivities();
            for (Activity activity : activities) {
                genes.add(new Gene(recipes.indexOf(recipe),activities.indexOf(activity)));
            }
        }

        Collections.shuffle(genes);
    }

    public List<Gene> getGenes() {
        return genes;
    }

    public void representation() {

        for(Gene gene:genes) {
            System.out.print(gene+" ");
        }

    }

     class Gene {

        private int recipeIndex;
        private int activityIndex;

         public Gene(int recipeIndex, int activityIndex) {
             this.recipeIndex = recipeIndex;
             this.activityIndex = activityIndex;
         }

         public int getRecipeIndex() {
             return recipeIndex;
         }

         public void setRecipeIndex(int recipeIndex) {
             this.recipeIndex = recipeIndex;
         }

         public int getActivityIndex() {
             return activityIndex;
         }

         public void setActivityIndex(int activityIndex) {
             this.activityIndex = activityIndex;
         }

         @Override
         public boolean equals(Object o) {
             if (this == o) return true;
             if (o == null || getClass() != o.getClass()) return false;
             Gene gene = (Gene) o;
             return recipeIndex == gene.recipeIndex &&
                     activityIndex == gene.activityIndex;
         }

         @Override
         public int hashCode() {
             return Objects.hash(recipeIndex, activityIndex);
         }

         @Override
         public String toString() {
             return ""+recipeIndex+activityIndex;
         }
     }
}
