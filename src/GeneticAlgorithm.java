import java.util.*;
import java.util.function.Predicate;
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
             fitnessPopulation.put(chromosome,calculateFitness(chromosome));
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
            fitnessOffspring.put(chromosome, calculateFitness(chromosome));
            RecipeService.resetCompleteStatusOnAllActivities();
        }
    }

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
                List<Chromosome.Gene> genes = offSpring.get(i).getGenes();

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

        public int calculateFitness(Chromosome chromosome)  {

        int timeUnitsTaken = 0;
        boolean isLoopBroken = false;
        int resourceIdleTime = 0;

        List<Chromosome.Gene> copyOfGenes = new ArrayList<>(chromosome.getGenes());

        while(!isLoopBroken) {

            for(Chromosome.Gene gene:copyOfGenes) {

                Activity activity = RecipeService.getActivityForRecipe(gene.getRecipeIndex(),gene.getActivityIndex());
                if(!activity.isProgress() && RecipeService.areAllPreviousActivitiesComplete(gene.getRecipeIndex(),gene.getActivityIndex())) {

                    if(activity.isHumanNeeded()) {
                        if(ResourceService.checkAvailability(Constants.HUMAN,1,timeUnitsTaken)) {
                            ResourceService.useResource(activity,Constants.HUMAN, 1, timeUnitsTaken, activity.getTimeUnitsNeeded());
                        } else {
                            timeUnitsTaken++;
                            verifyIfAnyResourcesCanBeReleased(timeUnitsTaken);
                            isLoopBroken = areAllActivitiesComplete();
                            if(isLoopBroken) break; else
                            continue;
                        }
                    }
                    boolean areAllResourcesAllocated = true;
                    for(Resource resourceNeeded:activity.getResourcesNeeded()) {

                        if(!ResourceService.checkAvailability(resourceNeeded.getResourceName(),resourceNeeded.getQuantity(),timeUnitsTaken)) {
                            areAllResourcesAllocated = false;
                            continue;
                        }

                    }

                    if(!areAllResourcesAllocated) {
                        timeUnitsTaken++;
                        verifyIfAnyResourcesCanBeReleased(timeUnitsTaken);
                        markActivitiesThatAreComplete();
                        continue;
                    } else {
                        for(Resource resourceNeeded:activity.getResourcesNeeded()) {
                            ResourceService.useResource(activity,resourceNeeded.getResourceName(), resourceNeeded.getQuantity(), timeUnitsTaken, activity.getTimeUnitsNeeded());
                        }
                        activity.setProgress(true);
                    }

                }
                timeUnitsTaken++;
                verifyIfAnyResourcesCanBeReleased(timeUnitsTaken);
                markActivitiesThatAreComplete();
                resourceIdleTime+=ResourceService.calculateResourceIdleTime();
            }
            markActivitiesThatAreComplete();
            verifyIfAnyResourcesCanBeReleased(timeUnitsTaken);
            isLoopBroken = areAllActivitiesComplete();
            if(isLoopBroken) {
                break;
            }
            cleanUpActivitiesThatAreComplete(copyOfGenes);

        }
        chromosome.setResourceIdleTime(resourceIdleTime);
        return timeUnitsTaken;
    }

    private void markActivitiesThatAreComplete() {

        RecipeService.markRecipesThatAreComplete();

    }

    private void cleanUpActivitiesThatAreComplete(List<Chromosome.Gene> copyOfGenes) {
        Predicate<Chromosome.Gene> filterCompletedOnes = gene -> {
            return RecipeService.getActivityForRecipe(gene.getRecipeIndex(),gene.getActivityIndex()).isActivityComplete();
        };
        copyOfGenes.removeIf(filterCompletedOnes);

    }

    private boolean areAllActivitiesComplete() {

        return RecipeService.areAllActivitiesComplete();

    }


    private void verifyIfAnyResourcesCanBeReleased(int timeUnitsTaken) {
        ResourceService.releaseResourceQuantities(timeUnitsTaken);
    }
}


