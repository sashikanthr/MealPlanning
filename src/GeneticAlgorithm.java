import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import java.sql.SQLOutput;
import java.util.*;

public class GeneticAlgorithm {

    private List<Recipe> recipes;

    private List<Chromosome> population;

    private List<Chromosome> offSpring = new ArrayList<>();

     public GeneticAlgorithm(List<Recipe> recipes) {

         this.recipes = recipes;

     }

     public List<Chromosome> generateInitialPopulation() {

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

         /*Implement a 2 point crossover using PMX (Partially Mapped Crossover)
         We will maintain 2 indices i and j. i will select the parent forward and j will select the parent backward.
          */

         int i=0;
         int j=Constants.NUMBER_OF_CHROMOSOMES-1;

        while(i+j==Constants.NUMBER_OF_CHROMOSOMES-1 && i<Constants.NUMBER_OF_CHROMOSOMES/2) {

            Chromosome parent1 = population.get(i);
            //System.out.println("Parent 1");
            //parent1.representation();
            //System.out.println();
            Chromosome parent2 = population.get(j);
            //System.out.println("Parent 2");
            //parent2.representation();
            //System.out.println();
            performPMX(parent1,parent2);
            i++;
            j--;

        }
        System.out.println("Population.."+population.size());
        System.out.println("Offspring..."+offSpring.size());

    }

    private void performPMX(Chromosome parent1, Chromosome parent2) {

         /*
         For the PMX Crossover we choose two random indexes so that it makes a segment in P1 and P2.
         These segments will be the elements in the corresponding positions in offspring 1 and 2.
         Then we fill rest of the elements from parent 1 to offspring 2 and from parent 2 to offspring 1.
         The segment in P1 and P2 is a map between the elements.
          */

         Random random = new Random();
         int i = random.nextInt(parent1.getGenes().size());
         int j = random.nextInt(parent2.getGenes().size());
        // Here the map is that key->value and value->key
        //System.out.println("Index i.."+i);
        //System.out.println("Index j.."+j);
        if(i>j) {
            //Swap using Bitwise XOR
            i = i^j;
            j = i^j;
            i = i^j;
        }

      //  System.out.println("New Index i.."+i);
       // System.out.println("New Index j.."+j);

         Map<Chromosome.Gene,Chromosome.Gene> crossoverMap = new HashMap<>();
         Chromosome offSpring1 = new Chromosome(parent2.getGenes().size());
         Chromosome offSpring2 = new Chromosome(parent1.getGenes().size());

         //Set the offsprings' initial elements from P1 and P2 and generate the crossover map
         for(int pointer=i;pointer<=j;pointer++) {
             Chromosome.Gene geneP1 = parent1.getGenes().get(pointer);
             Chromosome.Gene geneP2 = parent2.getGenes().get(pointer);
             crossoverMap.put(geneP1,geneP2);
             offSpring1.getGenes().set(pointer,geneP1);
             offSpring2.getGenes().set(pointer,geneP2);
         }

         //Set the rest of the genes.
        //System.out.println("Crossover Map.."+crossoverMap);
        for(int pointer = 0;pointer<i;pointer++) {
            setGenes(offSpring1,offSpring2,parent1,parent2,crossoverMap,pointer);
        }

        for(int pointer=j+1;pointer<parent1.getGenes().size();pointer++) {
            setGenes(offSpring1,offSpring2,parent1,parent2,crossoverMap,pointer);
        }

        offSpring.add(offSpring1);
        offSpring.add(offSpring2);
    //    System.out.println("OffSpring 1");
     //   offSpring1.representation();
      //  System.out.println();
      //  System.out.println("OffSpring 2");
       // offSpring2.representation();


    }

    public void mutate() {


    }

    private void setGenes(Chromosome offSpring1, Chromosome offSpring2, Chromosome parent1, Chromosome parent2,Map<Chromosome.Gene,Chromosome.Gene> crossoverMap,int pointer) {

        Chromosome.Gene uniqueParentGene;
        Chromosome.Gene geneP1 = parent1.getGenes().get(pointer);;
        Chromosome.Gene geneP2 = parent2.getGenes().get(pointer);;
        uniqueParentGene = getUniqueGene(offSpring1,crossoverMap,geneP2,true);
        offSpring1.getGenes().set(pointer,uniqueParentGene);
        uniqueParentGene = getUniqueGene(offSpring2,crossoverMap,geneP1,false);
        offSpring2.getGenes().set(pointer,uniqueParentGene);

    }

    private Chromosome.Gene getUniqueGene(Chromosome offspring, Map<Chromosome.Gene,Chromosome.Gene> crossoverMap, Chromosome.Gene parentGene,boolean isKeyToValue) {

         if(offspring.getGenes().contains(parentGene)) {
             Chromosome.Gene geneMapping = new Chromosome().new Gene();
             if(isKeyToValue) {
                 geneMapping = crossoverMap.get(parentGene);
             } else {
                 for (Map.Entry<Chromosome.Gene, Chromosome.Gene> entry : crossoverMap.entrySet()) {
                     if(entry.getValue().equals(parentGene)) {
                         geneMapping = entry.getKey();
                     }
                 }
             }
             return getUniqueGene(offspring,crossoverMap,geneMapping,isKeyToValue);

         }
             return parentGene;

    }

}
