import java.util.ArrayList;
import java.util.List;

public class GeneticAlgorithm {

    private List<Recipe> recipes;

    private List<Chromosome> population;

     public GeneticAlgorithm(List<Recipe> recipes) {

         this.recipes = recipes;

     }

     public List<Chromosome> generatePopulation() {

         population = new ArrayList<>(Constants.NUMBER_OF_CHROMOSOMES);
         int counter=0;
         while(counter<Constants.NUMBER_OF_CHROMOSOMES) {
             population.add(new Chromosome(recipes));
             counter++;
         }
         return population;
    }

    public void evaluatePopulation() {
        /*double[] popScore = new double[population.length];
        for (int i = 0; i < population.length; i++) {
            popScore[i] = evaluate(population[i]);
        }*/

    }
    
    private double evaluate(Chromosome p) {
        // We will evaluate based on 2 criteria:
        // 1. Wait time - this is the amount of time when within 1 recipe, 
        //    step i is complete but we are unable to begin step i + 1 due to 
        //    resources being unavailable, eg.
        // 2. Total time - this is the total time spent in the kitchen from start of first step of first recipe,
        //    to completion of final step of last recipe
        // we may also need a criteria for time between courses, and to adjust the weights of these based on which we care about most.
        int wait_time = 0;
        // sum wait times
        int total_time = 0; //end time - start time
        
        // we return 1 divided by the weighted values to get a value between 0 and 1; closer to 1 is better
        return 1.0 / ((1.0 * wait_time) + (1.0 * total_time));
    }

    public void repeat() {

        while(true) { //stopping criteria not satisfied

            crossOver();
            mutate();
            evaluatePopulation();;
        }

    }

    public void crossOver() {


    }

    public void mutate() {


    }

}
