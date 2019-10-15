import java.sql.SQLOutput;
import java.util.*;
import java.util.stream.Collectors;

public class GeneticAlgorithm {

    private List<Recipe> recipes;
    private List<Chromosome> population;
    private List<Chromosome> offSpring = new ArrayList<>();

    Map<Chromosome,Integer> fitnessPopulation = new HashMap<>();
    Map<Chromosome,Integer> fitnessOffspring = new HashMap<>();

    private int iterationCounter = 1;

    private Chromosome bestOffSpring;
    private Chromosome bestParent;
    private Chromosome best;
    
    private int bestOffspringFitness = Integer.MAX_VALUE;
    private int bestParentFitness = Integer.MAX_VALUE;

    public Chromosome getBestOffSpring() {
        return bestOffSpring;
    }

    public Chromosome getBestParent() {
        return bestParent;
    }

    public int getBestOffspringFitness() {
        return bestOffspringFitness;
    }

    public int getBestParentFitness() {
        return bestParentFitness;
    }

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
         fitnessPopulation = new HashMap<>();
        
         for(Chromosome chromosome: population) {
             fitnessPopulation.put(chromosome,calculateFitness(chromosome.getGenes()));
             RecipeService.resetCompleteStatusOnAllActivities();
        }
    }

    public void printPopulation() {
        for(Chromosome chromosome:population) {
            System.out.println(chromosome.getGenes());
        }
    }

    public void printOffspring() {
        for(Chromosome chromosome:offSpring) {
            System.out.println(chromosome.getGenes());
        }
    }

    public void sortPopulation(){
        fitnessPopulation =  sortPopulation(fitnessPopulation);
        population = fitnessPopulation.keySet().stream().collect(Collectors.toList());
    }

    public void sortOffspring() {
       fitnessOffspring =  sortPopulation(fitnessOffspring);
        offSpring = fitnessOffspring.keySet().stream().collect(Collectors.toList());
    }

    public LinkedHashMap<Chromosome, Integer> sortPopulation(Map<Chromosome,Integer> fitnessMapPopulation) {
        return fitnessMapPopulation.entrySet()
                .stream()
                .sorted((e1, e2) -> e1.getValue().compareTo(e2.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public void selection() {
         if (!fitnessOffspring.isEmpty()) {
            int j=0;

            for (int i=Constants.NUMBER_OF_CHROMOSOMES/2; i < Constants.NUMBER_OF_CHROMOSOMES; i++) {
                population.set(i, offSpring.get(j));
                j++;
            }
         }
    }

    public int getBestFitnessValue() {
        for (Map.Entry<Chromosome, Integer> entry : fitnessPopulation.entrySet()) {
            if (entry.getValue()<bestParentFitness) {
                bestParentFitness = entry.getValue();
                bestParent = entry.getKey();
            }
        }

        for (Map.Entry<Chromosome, Integer> entry : fitnessOffspring.entrySet()) {
            if (entry.getValue()<bestOffspringFitness) {
                bestOffspringFitness = entry.getValue();
                bestOffSpring = entry.getKey();
            }
        }

        if (bestOffspringFitness<bestParentFitness) {
            best = bestOffSpring;
            return bestOffspringFitness;
        }
        best = bestParent;
        return bestParentFitness;
    }

    public Chromosome getBest() {
         return best;
    }

    public void evaluateOffSpring() {
        fitnessOffspring = new HashMap<>();
        for (Chromosome chromosome: offSpring) {
            fitnessOffspring.put(chromosome, calculateFitness(chromosome.getGenes()));
            RecipeService.resetCompleteStatusOnAllActivities();
        }
    }

    /*
    Here fitness is based on number of iterations. According to the recipes, an activity can be run simultaneously
    with other activities or it has to wait for the predecessor activities to complete. Also for the activity to be
    completed, there must be available resources. Here for each iteration, we will execute all the activities that
    can be completed concurrently. Ones which cannot be completed will be added to a queue which will be processed
    as part of the next iteration. For each iteration there will be a penalty. And during, one iteration the resources
    are shared. At the end of the iteration, the resources are released. At the end of all iterations, we get the final
    value of the fitness. The shortest value is the fittest chromosome.
     */
    private int calculateFitness(List<Chromosome.Gene> genes) {
        int recipeIndex;
        int activityIndex;
        int totalTimeUnits = 0;
        int time_waste = 0;
        List<Chromosome.Gene> nextIterationQueue = new ArrayList();

        for (Chromosome.Gene gene:genes) {
            recipeIndex = gene.getRecipeIndex();
            activityIndex = gene.getActivityIndex();
            Activity activity = RecipeService.getActivityForRecipe(recipeIndex, activityIndex);

            if (RecipeService.areAllPreviousActivitiesComplete(recipeIndex, activityIndex)) {
                //retroactively find the best resource configuration
                // when we are picking a human, we want to see the preconditions for the activity
                // find out the max time needed to finish preconditions
                // then start time of this activity is the max of either min queue of humans OR time needed to finish preconditions
                // then we pick human resource based on smallest gap between time they become available and the calculated start time
                // because there's no reason to pick a human with a shorter queue if the task won't be ready yet!
                
                // step 1: calculate max time to finish preconditions:
                int max_time = RecipeService.getTimeUnitsForActivitySet(RecipeService.getPreviousActivities(recipeIndex, activityIndex));
                
                // step 2: find the minimum human resource queue time
                int min_queue_time = 0;
                
                Resource humanResource = null;
                if (activity.isHumanNeeded()) {                 
                    humanResource = ResourceService.getResource(Constants.HUMAN);
                    min_queue_time = humanResource.getTimeAvailable();
                }
                
                // step 3: find max of these:
                int start_time = Math.max(max_time, min_queue_time);
                
                // step 4: find the human who has the human_time - start_time <= 0 but closest to 0
                // so i have to search through all the human resources
                int closest = Integer.MAX_VALUE;
                Resource chosen = humanResource;

                for (Resource resource : ResourceService.getResourceList()) {
                    if (resource.getResourceName().equals(Constants.HUMAN)) {
                        //we have a human - check their queue time
                        int q = resource.getTimeAvailable();
                        int diff = q - start_time;
                        if (Math.abs(0 - diff) < closest) {
                            closest = Math.abs(0 - diff);
                            chosen = resource;
                        }
                    }
                    ResourceService.useResource(chosen, 1, activity.getTimeUnitsNeeded());
                }
                time_waste += closest;                
            
                List<Resource> resources = activity.getResourcesNeeded();
                Resource resource;

                boolean isBreak = false;
                for (Resource resourceNeeded: resources) {
                    resource = ResourceService.getResource(resourceNeeded.getResourceName());
                    if (!ResourceService.checkAvailability(resource, resourceNeeded.getQuantity())) {
                        isBreak = true;
                        break;
                    }
                }

                if (isBreak) {
                    nextIterationQueue.add(gene);
                    continue;
                }

                for (Resource resourceNeeded: resources) {
                    resource = ResourceService.getResource(resourceNeeded.getResourceName());
                    ResourceService.useResource(resource, resourceNeeded.getQuantity(), activity.getTimeUnitsNeeded());
                }
                totalTimeUnits += activity.getTimeUnitsNeeded().getTimeUnits();
                activity.setActivityComplete(true);
            } else {
                nextIterationQueue.add(gene);
            }
        
            ResourceService.resetResourceQuantities();
            if (nextIterationQueue.isEmpty()) {
                return totalTimeUnits;
            }
        }
        return totalTimeUnits + time_waste + Constants.PENALTY_FOR_ITERATION + calculateFitness(nextIterationQueue);
    }

   // private double evaluate(Chromosome p) {
        // We will evaluate based on 2 criteria:
        // 1. Wait time - this is the amount of time when within 1 recipe, 
        //    step i is complete but we are unable to begin step i + 1 due to 
        //    resources being unavailable, eg.
        // 2. Total time - this is the total time spent in the kitchen from start of first step of first recipe,
        //    to completion of final step of last recipe
        // we may also need a criteria for time between courses, and to adjust the weights of these based on which we care about most.
     //   int wait_time = 0;
        // sum wait times
     //   int total_time = 0; //end time - start time
        
        // we return 1 divided by the weighted values to get a value between 0 and 1; closer to 1 is better
     //   return 1.0 / ((1.0 * wait_time) + (1.0 * total_time));
   // }

   /* Implement a 2 point crossover using PMX (Partially Mapped Crossover)
    * We will maintain 2 indices i and j. i will select the parent forward and 
    * j will select the parent backward.
    */    
    public void crossOver() {
        offSpring = new ArrayList<>();
         int i = 0;
         int j = Constants.NUMBER_OF_CHROMOSOMES - 1;

        while(i + j == Constants.NUMBER_OF_CHROMOSOMES - 1 && i < Constants.NUMBER_OF_CHROMOSOMES / 2) {
            Chromosome parent1 = population.get(i);
            //System.out.println("Parent 1");
            //parent1.representation();
            //System.out.println();
            Chromosome parent2 = population.get(j);
            //System.out.println("Parent 2");
            //parent2.representation();
            //System.out.println();
            performPMX(parent1, parent2);
            i++;
            j--;
        }
    }

    /*
    We will perform the mutation by processing the first half of the fittest parent and generating a random value.
    If the random value is > some value (0.7 in this case), we will interchange the positions of the genes based on
    a random integer.
     */
    public void mutation() {
        Random random = new Random();
        for(int i = 0; i < Constants.NUMBER_OF_CHROMOSOMES / 2; i++) {
            if(Math.random()>0.7) {
                List<Chromosome.Gene> genes = population.get(i).getGenes();

                for(int counter = 0;counter<3;counter++) {
                    int position1 = random.nextInt(genes.size());
                    int position2 = random.nextInt(genes.size());
                    Chromosome.Gene geneAtPosition1 = genes.get(position1);
                    Chromosome.Gene geneAtPosition2 = genes.get(position2);
                    genes.set(position2,geneAtPosition1);
                    genes.set(position1,geneAtPosition2);
                }
            }
        }
    }

    /*
         For the PMX Crossover we choose two random indexes so that it makes a segment in P1 and P2.
         These segments will be the elements in the corresponding positions in offspring 1 and 2.
         Then we fill rest of the elements from parent 1 to offspring 2 and from parent 2 to offspring 1.
         The segment in P1 and P2 is a map between the elements.
    */
    private void performPMX(Chromosome parent1, Chromosome parent2) {
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

    private void setGenes(Chromosome offSpring1, Chromosome offSpring2, Chromosome parent1, Chromosome parent2,
                          Map<Chromosome.Gene, Chromosome.Gene> crossoverMap, int pointer) {
        Chromosome.Gene uniqueParentGene;
        Chromosome.Gene geneP1 = parent1.getGenes().get(pointer);;
        Chromosome.Gene geneP2 = parent2.getGenes().get(pointer);;
        uniqueParentGene = getUniqueGene(offSpring1, crossoverMap, geneP2, true);
        offSpring1.getGenes().set(pointer, uniqueParentGene);
        uniqueParentGene = getUniqueGene(offSpring2, crossoverMap, geneP1, false);
        offSpring2.getGenes().set(pointer, uniqueParentGene);
    }

    private Chromosome.Gene getUniqueGene(Chromosome offspring, Map<Chromosome.Gene, Chromosome.Gene> crossoverMap, 
                                          Chromosome.Gene parentGene, boolean isKeyToValue) {
         if (offspring.getGenes().contains(parentGene)) {
             Chromosome.Gene geneMapping = new Chromosome().new Gene();
             if (isKeyToValue) {
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

    public int printStepsToFollow(List<Chromosome.Gene> genes) {
        System.out.println("Activities to perform during iteration..." + iterationCounter);
        int recipeIndex;
        int activityIndex;
        int totalTimeUnits = 0;
        List<Chromosome.Gene> nextIterationQueue = new ArrayList();

        for(Chromosome.Gene gene:genes) {
            recipeIndex = gene.getRecipeIndex();
            activityIndex = gene.getActivityIndex();
            Activity activity = RecipeService.getActivityForRecipe(recipeIndex,activityIndex);
            if(RecipeService.areAllPreviousActivitiesComplete(recipeIndex,activityIndex)) {
                List<Resource> resources = activity.getResourcesNeeded();
                Resource resource;
                Resource humanResource = null;
                if(activity.isHumanNeeded()) {
                   humanResource = ResourceService.getResource(Constants.HUMAN);
                   if(ResourceService.checkAvailability(humanResource,1)) {
                        ResourceService.useResource(humanResource, 1, activity.getTimeUnitsNeeded());
                    }  else {
                        nextIterationQueue.add(gene);
                        continue;
                    }
               }
               boolean isBreak = false;
               for(Resource resourceNeeded:resources) {
                    resource = ResourceService.getResource(resourceNeeded.getResourceName());
                   if(!ResourceService.checkAvailability(resource,resourceNeeded.getQuantity())) {
                        isBreak = true;
                        break;
                    }
                }

                    if(isBreak) {
                        nextIterationQueue.add(gene);
                        continue;
                    }

                    for(Resource resourceNeeded:resources) {

                        resource = ResourceService.getResource(resourceNeeded.getResourceName());
                        ResourceService.useResource(resource,resourceNeeded.getQuantity(), activity.getTimeUnitsNeeded());

                    }
                    totalTimeUnits+=activity.getTimeUnitsNeeded().getTimeUnits();
                    activity.setActivityComplete(true);
                    Recipe recipe = RecipeService.getRecipe(recipeIndex);
                    System.out.println(recipe.getName()+"|"+activity.getAction());
                } else {
                    nextIterationQueue.add(gene);
                }

            }

            ResourceService.resetResourceQuantities();
            iterationCounter++;
            if(nextIterationQueue.isEmpty()) {
                return totalTimeUnits;
            }
            return totalTimeUnits+Constants.PENALTY_FOR_ITERATION+printStepsToFollow(nextIterationQueue);
        }
    }


