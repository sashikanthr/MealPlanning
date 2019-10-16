# MealPlanning

ACTIVITY - Represents a single activity in a recipe. Stores information about priority,
           resources/humans needed, and duration.
           
CHROMOSOME - Represents a chromosome with a set of Genes (activities).

CONSTANTS - Set of parameters such as delimiters, number of chromosomes, max iterations.

GENETIC_ALGORITHM - Performs the schedule creation; evaluates fitness of chromosomes, does
                    mutations and crossovers, and returns the best solution found.
                    
MEAL_PLANNING - The main class; responsible for reading input, 
                making sure we have sufficient resources for all tasks to be completable, and
                calling the genetic algorithm.
                
RECIPE - Represents a single recipe.

RECIPE_SERVICE - Utility functions for working with recipes, such as finding previous activities
                 needed for a certain step, checking completeness of preconditions.
                 
RESOURCE - Represents a single resource. 

RESOURCE_SERVICE - Handles determining if a requested quantity of a resource is available,
                   allocating, and freeing resources.
                   
TIME_UNITS - Handles functionality related to time units which represent duration of each activity.



USAGE:
Provide a file "Recipes.txt" and a file "AvailableResources.txt" in the directory above the code 
  (directory src is in). Call MealPlanning.java to run.
