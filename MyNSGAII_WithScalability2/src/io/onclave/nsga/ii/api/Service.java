/*
 * This repository / codebase is Open Source and free for use and rewrite.
 */
package io.onclave.nsga.ii.api;

import com.file.read.CalculateTheCostsForChromosomes;
import com.file.read.ObjectConstrains;
import com.file.read.WriteToExternalFile;
import io.onclave.nsga.ii.Interface.IObjectiveFunction;
import io.onclave.nsga.ii.configuration.Configuration;
import io.onclave.nsga.ii.datastructure.Allele;
import io.onclave.nsga.ii.datastructure.Chromosome;
import io.onclave.nsga.ii.datastructure.ParetoObject;
import io.onclave.nsga.ii.datastructure.Population;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This is the service class that does most of the under-the-hood work that is abstracted/encapsulated
 * by other classes at the business/controller layer.
 * 
 * @author  Debabrata Acharya <debabrata.acharya@icloud.com>
 * @version 1.1
 * @since   0.1
 */
public class Service {
    
    
    public static List<List<Double>> allObjectiveValues = new ArrayList<List<Double>>();
    public static  ArrayList objectiveOneValues = new ArrayList();
    public static  ArrayList objectiveTwoValues = new ArrayList();
    public static  ArrayList objectiveThreeValues = new ArrayList();
    public static  ArrayList objectiveFourValues = new ArrayList();
    
    /**
     * this is an implementation of the fast non-dominated sorting algorithm as defined in the
     * NSGA-II paper [DOI: 10.1109/4235.996017] Section III Part A.
     * 
     * @param   population  the population object that needs to undergo fast non-dominated sorting algorithm
     * @return  a HashMap with an integer key that labels the ranks and a list of chromosomes as values that clot chromosomes of same rank
     */
    public static HashMap<Integer, List<Chromosome>> fastNonDominatedSort(Population population) {
        
        
        allObjectiveValues = CalculateTheCostsForChromosomes.calculateTheCost(population);
        objectiveOneValues = (ArrayList) allObjectiveValues.get(0);
        objectiveTwoValues = (ArrayList) allObjectiveValues.get(1);
        objectiveThreeValues = (ArrayList) allObjectiveValues.get(2);
        objectiveFourValues = (ArrayList) allObjectiveValues.get(3);
        HashMap<Integer, List<Chromosome>> paretoFront = new HashMap<>();
        List<Chromosome> singularFront = new ArrayList<>();
        List<Chromosome> populace = population.getPopulace();
        
        
        int size = populace.size();
        int a =0;
        /**
         * iterating over each chromosome of the population
         */
        for(int i = 0; i < populace.size(); i++){
            Chromosome chromosome = populace.get(i);
            
            /**
             * an initial domination rank of 0 is set for each chromosome and a blank list is set for the number of
             * chromosomes that the present chromosome dominates.
             */
            chromosome.setDominationRank(0);
            chromosome.setDominatedChromosomes(new ArrayList<>());
            
            
             /**
             * for each chromosome, the program iterates over all the other remaining chromosomes to find which other
             * chromosomes are dominated by this chromosome and vice versa.
             */
             for(int j = 0; j < populace.size(); j++ ){
                 Chromosome competitor = populace.get(j);
                    if(!competitor.equals(chromosome)){
                    /**
                    * if the present chromosome dominates the competitor, then:
                    *      i:   check if the competitor already exists in the list of dominated chromosomes of the present chromosome.
                    *     ii:   if the competitor does not exist within the list, then add it to the list of dominated chromosomes
                    *           of the present chromosome.
                    * else, if the competitor dominates the present chromosome, then increment the domination rank of the present
                    * chromosome by one.
                    */
                   if(dominates(i, j)) {
                       if(!chromosome.getDominatedChromosomes().contains(competitor)) chromosome.getDominatedChromosomes().add(competitor);
                   } else if(dominates(j, i)) chromosome.setDominationRank(chromosome.getDominationRank() + 1);
                    }
             }
             
            /**
             * if the domination rank of the present chromosome is 0, it means that this chromosome is a non-dominated chromosome
             * and hence it is added to the clot of chromosomes that are also non-dominated.
             */
            if(chromosome.getDominationRank() == 0) singularFront.add(chromosome);
            a++;
        }
       
        /**
         * the first clot of non-dominated chromosomes is added to the HashMap with rank label 1.
         */
        paretoFront.put(1, singularFront);
        
        int i = 1;
        List<Chromosome> previousFront = paretoFront.get(i);
        List<Chromosome> nextFront = new ArrayList<>();
        
        /**
         * the current/previous ranked clot of chromosomes with rank i is iterated over to find the next clot of chromosomes
         * with rank (i+1)
         */
        while(previousFront != null && !previousFront.isEmpty()) {
            
            /**
             * iterating over each chromosome from the previous clot of chromosomes ranked i.
             */
            for(Chromosome chromosome : previousFront) {
                
                /**
                 * iterating over each of the dominated chromosomes from the present chromosome of rank i.
                 */
                for(Chromosome recessive : chromosome.getDominatedChromosomes()) {
                    
                    /**
                     * if the domination rank of the current recessive chromosome in consideration is not 0, then
                     * decrement it's rank by 1.
                     * if the domination rank of the current recessive chromosome in consideration is 0, then add
                     * it to the next front [clot of chromosomes that belong to rank (i+1)].
                     */
                    if(recessive.getDominationRank() != 0) recessive.setDominationRank(recessive.getDominationRank() - 1);
                    if(recessive.getDominationRank() == 0) if(!nextFront.contains(recessive)) nextFront.add(recessive);
                }
            }
            
            /**
             * this code snippet ensures "rank jumps" to create all the possible rank lists from the parent
             * population.
             * new ranks are created only when there are recessive chromosomes with domination rank = 1 which are
             * decremented to domination rank 0 and then added to the next front.
             * but, due to the randomness of the algorithm, situation may occur such that even after decrementing all recessive
             * chromosome domination ranks by 1, none have domination rank 0 and hence the next front remains empty.
             * to ensure that all recessive chromosomes are added to some rank list, the program jumps domination ranks
             * of each recessive chromosome by decrementing domination rank by 1 until at least one of them reaches a
             * domination rank count of 0 and then that recessive chromosome is added to the next front.
             * 
             * if the next front is empty and the previous front has at least one dominated chromosome:
             *      i:  find the minimum rank among all the recessive chromosomes available:
             *              1:  iterate over all the chromosomes of the previous front
             *              2:  while the chromosomes have no dominated chromosomes with rank 0:
             *                      a:  iterate over all the recessive chromosomes of the current chromosome
             *                      b:  if the minimum rank is greater than the dominated rank of the present recessive,
             *                          mark this as the minimum rank recorded among all recessive chromosomes available.
             *              3:  end while
             *     ii:  iterate over all the chromosomes of the previous front
             *              1: while the chromosomes have no dominated chromosomes with rank 0:
             *                      a:  iterate over all the dominated chromosomes of the current chromosome
             *                      b:  if the domination rank of the recessive chromosome is not 0, then decrement the
             *                          domination count by value of minimum rank.
             *                      c:  if the domination rank is 0, then add it to the next front.
             *              2:  end while
             */
            if(nextFront.isEmpty() && !isDominatedChromosomesEmpty(previousFront)) {
                
                int minimumRank = -1;
                
                for(Chromosome chromosome : previousFront)
                    while(hasRecessiveRankGreaterThanZero(chromosome))
                        for(Chromosome recessive : chromosome.getDominatedChromosomes())
                            if((minimumRank == -1) || minimumRank > recessive.getDominationRank()) minimumRank = recessive.getDominationRank();
                
                if(minimumRank != -1) for(Chromosome chromosome : previousFront)
                    while(hasRecessiveRankGreaterThanZero(chromosome)) for(Chromosome recessive : chromosome.getDominatedChromosomes()) {
                            if(recessive.getDominationRank() != 0) recessive.setDominationRank(recessive.getDominationRank() - minimumRank);
                            if(recessive.getDominationRank() == 0) if(!nextFront.contains(recessive)) nextFront.add(recessive);
                    }
            }
            
            /**
             * if the next front calculated is not empty, then it is added to the ranked HashMap data-structure
             * with the rank (i+1), else all chromosomes are sorted into some rank or the other and the program
             * breaks out of the loop.
             */
            if(!nextFront.isEmpty()) paretoFront.put(++i, nextFront); else break;
            
            /**
             * the next front (i) calculated is marked as the previous front for the next iteration (i+1) and
             * an empty next front is created.
             */
            previousFront = nextFront;
            nextFront = new ArrayList<>();
        }

        return paretoFront;
    }
    
    /**
     * this is the implementation of the crowding distance assignment algorithm as defined in the
     * NSGA-II paper [DOI: 10.1109/4235.996017] Section III Part B.
     * this ensures diversity preservation.
     * 
     * @param   singularFront   a list of chromosomes whose crowding distances are to be calculated
     * @return                  a list of ParetoObjects with assigned crowding distances. [Refer ParetoObject.java for more information]
     */
    public static List<ParetoObject> crowdingDistanceAssignment(List<Chromosome> singularFront) {
        
        int i = 0;
        int end = singularFront.size() - 1;
        Double maxObjectiveValueOne = null;
        Double minObjectiveValueOne = null;
        Double maxObjectiveValueTwo = null;
        Double minObjectiveValueTwo = null;
        
        Double maxObjectiveValueThree = null;
        Double minObjectiveValueThree = null;
        Double maxObjectiveValueFour = null;
        Double minObjectiveValueFour = null;
        List<IObjectiveFunction> objectives = Configuration.getObjectives();
        List<ParetoObject> singlePareto = new ArrayList<>();
        
        /**
         * for each chromosome in the input list, a new ParetoObject with an initial crowding distance of 0
         * is created and added to the list of ParetoObjects that are to be returned.
         */
        for(Chromosome chromosome : singularFront) singlePareto.add(i++, new ParetoObject(chromosome, 0f));
        
        /**
         * iterating over each of the objective functions set [refer Configuration.java for more information],
         * the ParetoObject list is sorted according to the objective functions and the first and last ParetoObjects
         * are set a crowding distance of infinity.
         */
        List<List<Double>> costOfObjectives = CalculateTheCostsForChromosomes.calculateTheCostChromos(singularFront);
        ArrayList objectiveOneCost = (ArrayList)costOfObjectives.get(0);
        ArrayList objectiveTwoCost = (ArrayList)costOfObjectives.get(1);
        ArrayList objectiveThreeCost = (ArrayList)costOfObjectives.get(2);
        ArrayList objectiveFourCost = (ArrayList)costOfObjectives.get(3);
        
        
        int sizeofObjectives = objectives.size();
       
        
        
        for(int j =0; j < 4; j++){
            
            IObjectiveFunction objective = objectives.get(j);
            singlePareto = sort(singlePareto, objectives.get(j));
            
            singlePareto.get(0).setCrowdingDistance(Double.MAX_VALUE);
            singlePareto.get(end).setCrowdingDistance(Double.MAX_VALUE);
            
            for(int k = 0; k < singlePareto.size() ; k++){
                if(j == 0){
                    if((maxObjectiveValueOne == null) || (maxObjectiveValueOne < (Double)objectiveOneCost.get(k))) maxObjectiveValueOne = (Double)objectiveOneCost.get(k);
                    if((minObjectiveValueOne == null) || (minObjectiveValueOne > (Double)objectiveOneCost.get(k))) minObjectiveValueOne = (Double)objectiveOneCost.get(k);
                    //System.out.println("First One: "+k);
                }
                
                if(j == 1){
                    if((maxObjectiveValueTwo == null) || (maxObjectiveValueTwo < (Double)objectiveTwoCost.get(k))) maxObjectiveValueTwo = (Double)objectiveTwoCost.get(k);
                    if((minObjectiveValueTwo == null) || (minObjectiveValueTwo > (Double)objectiveTwoCost.get(k))) minObjectiveValueTwo = (Double)objectiveTwoCost.get(k);
                    //System.out.println("Second One: "+k);
                }
                
                if(j == 2){
                    if((maxObjectiveValueThree == null) || (maxObjectiveValueThree < (Double)objectiveThreeCost.get(k))) maxObjectiveValueThree = (Double)objectiveThreeCost.get(k);
                    if((minObjectiveValueThree == null) || (minObjectiveValueThree > (Double)objectiveThreeCost.get(k))) minObjectiveValueThree = (Double)objectiveThreeCost.get(k);
                }
                
                if(j == 3){
                    if((maxObjectiveValueFour == null) || (maxObjectiveValueFour < (Double)objectiveFourCost.get(k))) maxObjectiveValueFour = (Double)objectiveFourCost.get(k);
                    if((minObjectiveValueFour == null) || (minObjectiveValueFour > (Double)objectiveFourCost.get(k))) minObjectiveValueFour = (Double)objectiveFourCost.get(k);
                }
                
            }
            
           
            
        }
        
         for(i = 2; i < end; i++) singlePareto.get(i).setCrowdingDistance(calculateCrowdingDistance(singlePareto,
                                                                                                        i,
                                                                                                        objectives.get(0),
                                                                                                        maxObjectiveValueOne,
                                                                                                        minObjectiveValueOne,objectiveOneCost));
         
         for(i = 2; i < end; i++) {
             singlePareto.get(i).setCrowdingDistance(calculateCrowdingDistance(singlePareto,
                                                                                                        i,
                                                                                                        objectives.get(1),
                                                                                                        maxObjectiveValueTwo,
                                                                                                        minObjectiveValueTwo,objectiveTwoCost));
         }
         
         for(i = 2; i < end; i++) singlePareto.get(i).setCrowdingDistance(calculateCrowdingDistance(singlePareto,
                                                                                                        i,
                                                                                                        objectives.get(2),
                                                                                                        maxObjectiveValueThree,
                                                                                                        minObjectiveValueThree,objectiveThreeCost));
         
         for(i = 2; i < end; i++) singlePareto.get(i).setCrowdingDistance(calculateCrowdingDistance(singlePareto,
                                                                                                        i,
                                                                                                        objectives.get(3),
                                                                                                        maxObjectiveValueFour,
                                                                                                        minObjectiveValueFour,objectiveFourCost));
        
        
//        for(IObjectiveFunction objective : objectives) {
//            
//            maxObjectiveValue = null;
//            minObjectiveValue = null;
//            singlePareto = sort(singlePareto, objective);
//            
//            singlePareto.get(0).setCrowdingDistance(Double.MAX_VALUE);
//            singlePareto.get(end).setCrowdingDistance(Double.MAX_VALUE);
//            
//            /**
//             * the max and min objective values are calculated according to the present objective function
//             */
//            for(ParetoObject paretoObject : singlePareto) {
//                
//                if((maxObjectiveValue == null) || (maxObjectiveValue < objective.objectiveFunction(paretoObject))) maxObjectiveValue = objective.objectiveFunction(paretoObject);
//                if((minObjectiveValue == null) || (minObjectiveValue > objective.objectiveFunction(paretoObject))) minObjectiveValue = objective.objectiveFunction(paretoObject);
//            }
//            
//            /**
//             * the crowding distance of all ParetoObjects are calculated and assigned except the first and last ParetoObjects
//             * that have infinite crowding distance
//             */
//            for(i = 2; i < end; i++) singlePareto.get(i).setCrowdingDistance(calculateCrowdingDistance(singlePareto,
//                                                                                                        i,
//                                                                                                        objective,
//                                                                                                        maxObjectiveValue,
//                                                                                                        minObjectiveValue));
//        }
        
        return singlePareto; 
   }
    
    /**
     * this method sorts a list of ParetoObjects based on the Crowd-Comparison Operator using the domination
     * rank and crowding distance as discussed in the NSGA-II paper [DOI: 10.1109/4235.996017] Section III Part B.
     * 
     * @param   singleFront     a list of ParetoObjects that are to be sorted according to their crowding distance
     * @return                  a list of sorted ParetoObjects
     */
    public static List<ParetoObject> crowdComparisonSort(List<ParetoObject> singleFront) {
        
        int index = -1;
        List<ParetoObject> sortedFront = new ArrayList<>();
        ParetoObject presentParetoObject;
        ParetoObject competitor;
        
        /**
         * all the ParetoObjects are, at first, marked as false for crowding distance sorted.
         */
        singleFront.stream().forEach((paretoObject) -> { paretoObject.setCrowdingDistanceSorted(false); });
        
        /**
         * iterating over each ParetoObject in the singular front input:
         *  i:  the i-th ParetoObject is marked as presentParetoObject
         * ii:  if the presentParetoObject is not already sorted by crowding distance:
         *          1:  iterate over the rest of the ParetoObjects in the input list as competitors that are
         *              not already sorted using crowding distance
         *          2:  compare the i-th and the j-th chromosome using the crowd comparison operator:
         *                  a: for different ranks, choose the one with the lower (better) rank.
         *                  b: for same rank, choose the one which has lower crowding distance.
         *          3:  if competitor dominates the i-th chromosome, then mark competitor as presentParetoObject
         *          4:  continue until i-th chromosome is compared to all competitors.
         *          5:  mark the presentParetoObject as already sorted by crowding distance
         *          6:  add presentParetoObject into list of sorted front with an incremented index
         */
        for(int i = 0; i < singleFront.size(); i++) {
            
            presentParetoObject = singleFront.get(i);
            
            if(!presentParetoObject.isCrowdingDistanceSorted()) {
                
                for(int j = 0; j < singleFront.size(); j++) {

                    competitor = singleFront.get(j);
                    
                    if(!competitor.isCrowdingDistanceSorted()) {
                        
                        double dominationRank = presentParetoObject.getChromosome().getDominationRank();
                        double competingDominationRank = competitor.getChromosome().getDominationRank();
                        double crowdingDistance = presentParetoObject.getCrowdingDistance();
                        double competingCrowdingDistance = competitor.getCrowdingDistance();

                        if(i != j) if((dominationRank > competingDominationRank) || ((dominationRank == competingDominationRank) && (crowdingDistance < competingCrowdingDistance))) presentParetoObject = competitor;
                    }
                }
                
                presentParetoObject.setCrowdingDistanceSorted(true);
                sortedFront.add(++index, presentParetoObject);
            }
        }
        
        return sortedFront;
    }
    
    /**
     * this method is not implemented, as it is not absolutely necessary for this algorithm to work.
     * is kept if implementation is needed in future.
     * returns the same unsorted parent population as of now.
     * 
     * @param   population  the population that is to be sorted
     * @return              a sorted population
     */
    public static Population nonDominatedPopulationSort(Population population) {
        
        //--TO-DO--
        
        return population;
    }
    
    /**
     * this method checks whether competitor1 dominates competitor2.
     * requires that none of the values of the objective functions using competitor1 is smaller
     * than the values of the objective functions using competitor2.
     * at least one of the values of the objective functions using competitor1 is greater than
     * the corresponding value of the objective functions using competitor2.
     * 
     * @param   competitor1     the chromosome that may dominate
     * @param   competitor2     the chromosome that may be dominated
     * @return                  boolean logic whether competitor1 dominates competitor2.
     */
    public static boolean dominates(final int competitor1, final int competitor2) {
        
        /**
         * getting the list of configured objectives from Configuration.java
         */
        List<IObjectiveFunction> objectives = Configuration.getObjectives();
        
        /**
         * checks the negation of the predicate [none of the values of objective functions using competitor1
         * is less than values of objective functions using competitor2] meaning that at least one of the values
         * of the objective functions using competitor1 is less than the values of the objective functions using
         * competitor2, hence returning false as competitor1 does not dominate competitor2
         */
        
        if(((double)objectiveOneValues.get(competitor1) < (double)objectiveOneValues.get(competitor2)) || ((double)objectiveTwoValues.get(competitor1) < (double)objectiveTwoValues.get(competitor2)) || ((double)objectiveThreeValues.get(competitor1) < (double)objectiveThreeValues.get(competitor2)) || ((double)objectiveFourValues.get(competitor1) < (double)objectiveFourValues.get(competitor2))){
            return false;
        }
        
         // if (!objectives.stream().noneMatch((objective) -> (objective.objectiveFunction(competitor1) > objective.objectiveFunction(competitor2))))return false;

            
          /**
         * returns the value of the predicate [at least one of the values of the objective functions using
         * competitor1 is greater than the corresponding value of the objective function using competitor2]
         */
//            System.out.println("Objective1 Comp1: "+objectives.get(0).objectiveFunction(competitor1));
//            System.out.println("Objective2 Comp1: "+objectives.get(1).objectiveFunction(competitor1));
//            
//            System.out.println("Objective1 Comp2: "+objectives.get(0).objectiveFunction(competitor2));
//            System.out.println("Objective2 Comp2: "+objectives.get(1).objectiveFunction(competitor2));
    if(((double)objectiveOneValues.get(competitor1) > (double)objectiveOneValues.get(competitor2)) || ((double)objectiveTwoValues.get(competitor1) > (double)objectiveTwoValues.get(competitor2)) || ((double)objectiveThreeValues.get(competitor1) > (double)objectiveThreeValues.get(competitor2)) || ((double)objectiveFourValues.get(competitor1) > (double)objectiveFourValues.get(competitor2))){
            return true;
        }
        
       // return objectives.stream().anyMatch((objective) -> (objective.objectiveFunction(competitor1) < objective.objectiveFunction(competitor2)));
        return false;
    }
    
    /**
     * the list is first converted to an array data-structure and then a randomized quick sort
     * algorithm is followed.
     * the resulting sorted array is again converted to a List data-structure before returning.
     * 
     * @param   singlePareto    the list of ParetoObjects that are to be sorted.
     * @param   objective       the objective function using which the ParetoObjects are sorted.
     * @return                  sorted list of ParetoObjects.
     */
    private static List<ParetoObject> sort(List<ParetoObject> singlePareto, IObjectiveFunction objective) {
        
        ParetoObject[] paretoArray = new ParetoObject[singlePareto.size()];
        singlePareto.toArray(paretoArray);
        
        randomizedQuickSort(paretoArray, 0, paretoArray.length - 1, objective);
        
        return (new ArrayList<>(Arrays.asList(paretoArray)));
    }
    
    /**
     * refer [https://jordanspencerwu.github.io/randomized-quick-sort/] for more details on randomized
     * quick sort algorithm.
     * 
     * @param   paretoArray     the array to be sorted
     * @param   head            the pointer/position of the head element
     * @param   tail            the pointer/position of the tail element
     * @param   objective       the objective function depending on which the sort is to take place
     * @return                  the pivot index.
     */
    private static int partition(ParetoObject[] paretoArray, int head, int tail, IObjectiveFunction objective) {
        
        ParetoObject pivot = paretoArray[tail];
        int i = head - 1;
        
        for(int j = head; j <= (tail - 1); j++) {
            
            if(objective.objectiveFunction(paretoArray[j]) <= objective.objectiveFunction(pivot)) {
                
                i++;
                ParetoObject temporary = paretoArray[i];
                paretoArray[i] = paretoArray[j];
                paretoArray[j] = temporary;
            }
        }
        
        ParetoObject temporary = paretoArray[i + 1];
        paretoArray[i + 1] = paretoArray[tail];
        paretoArray[tail] = temporary;
        
        return (i + 1);
    }
    
    /**
     * refer [https://jordanspencerwu.github.io/randomized-quick-sort/] for more details on randomized
     * quick sort algorithm.
     * 
     * @param   paretoArray     the array to be sorted
     * @param   head            the pointer/position of the head element
     * @param   tail            the pointer/position of the tail element
     * @param   objective       the objective function depending on which the sort is to take place
     * @return                  the random partition position index.
     */
    private static int randomizedPartition(ParetoObject[] paretoArray, int head, int tail, IObjectiveFunction objective) {
        
        int random = ThreadLocalRandom.current().nextInt(head, tail + 1);
        
        ParetoObject temporary = paretoArray[head];
        paretoArray[head] = paretoArray[random];
        paretoArray[random] = temporary;
        
        return partition(paretoArray, head, tail, objective);
    }
    
    /**
     * refer [https://jordanspencerwu.github.io/randomized-quick-sort/] for more details on randomized
     * quick sort algorithm.
     * 
     * @param   paretoArray     the array to be sorted
     * @param   head            the pointer/position of the head element
     * @param   tail            the pointer/position of the tail element
     * @param   objective       the objective function depending on which the sort is to take place
     */
    private static void randomizedQuickSort(ParetoObject[] paretoArray, int head, int tail, IObjectiveFunction objective) {
        
        if(head < tail) {
            
            int pivot = randomizedPartition(paretoArray, head, tail, objective);
            
            randomizedQuickSort(paretoArray, head, pivot - 1, objective);
            randomizedQuickSort(paretoArray, pivot + 1, tail, objective);
        }
    }
    
    /**
     * implementation of crowding distance calculation as defined in NSGA-II paper
     * [DOI: 10.1109/4235.996017] Section III Part B.
     * 
     * I[i]distance = I[i]distance + (I[i+1].m - I[i-1].m)/(f-max - f-min)
     * 
     * I[i]distance = crowding distance of the i-th individual
     * I[i+1].m = m-th objective function value of the (i+1)-th individual
     * I[i-1].m = m-th objective function value of the (i-1)-th individual
     * f-max, f-min = maximum and minimum values of the m-th objective function
     * 
     * @param   singlePareto            the list of ParetoObjects
     * @param   presentIndex            the present index of ParetoObject whose crowding distance is to be calculated
     * @param   objective               the objective function over which the value of i-th individual is to be calculated
     * @param   maxObjectiveValue       the maximum value for this objective function
     * @param   minObjectiveValue       the minimum value for this objective function
     * @return                          the crowding distance
     */
    private static double calculateCrowdingDistance(List<ParetoObject> singlePareto,
                                                    final int presentIndex,
                                                    final IObjectiveFunction objective,
                                                    final double maxObjectiveValue,
                                                    final double minObjectiveValue) {
        
        return (
            singlePareto.get(presentIndex).getCrowdingDistance()
            + ((objective.objectiveFunction(singlePareto.get(presentIndex + 1))
            - objective.objectiveFunction(singlePareto.get(presentIndex - 1))) / (maxObjectiveValue - minObjectiveValue))
        );
    }
    
    
    //My version of crowdingDistanceCalculation
     private static double calculateCrowdingDistance(List<ParetoObject> singlePareto,
                                                    final int presentIndex,
                                                    final IObjectiveFunction objective,
                                                    final double maxObjectiveValue,
                                                    final double minObjectiveValue,
                                                    ArrayList objectiveCost) {
         
         //System.out.println("In the claculateCrodingDistance");
        
        return (
            singlePareto.get(presentIndex).getCrowdingDistance()
            + (((Double)objectiveCost.get(presentIndex + 1)- (Double)objectiveCost.get(presentIndex - 1)) / (maxObjectiveValue - minObjectiveValue))
        );
    }
    
    /**
     * checks whether any of the dominated chromosome list of the given front is empty,
     * returns true if at least one set of dominated chromosomes is not non-empty.
     * 
     * @param   front   list of chromosomes whose dominated chromosomes are to be checked
     * @return          boolean logic whether the dominated chromosomes are empty
     */
    private static boolean isDominatedChromosomesEmpty(List<Chromosome> front) {
        return front.stream().anyMatch((chromosome) -> (!chromosome.getDominatedChromosomes().isEmpty()));
    }
    
    /**
     * checks if any of the dominated chromosomes of the input chromosome has a domination rank of 0,
     * returns true if at least one dominated chromosome contains domination rank 0.
     * 
     * @param   chromosome  chromosome to check whether it contains any dominated chromosome with rank 0
     * @return  boolean logic whether dominated chromosomes contain rank 0.
     */
    private static boolean hasRecessiveRankGreaterThanZero(Chromosome chromosome) {
        
        if(chromosome.getDominatedChromosomes().isEmpty()) return false;
        
        return chromosome.getDominatedChromosomes().stream().noneMatch((recessive) -> (recessive.getDominationRank() == 0));
    }
    
    /**
     * the child and parent population is combined to create a larger population pool
     * 
     * @param   parent  parent population
     * @param   child   child population
     * @return          combined parent + child population
     */
    public static Population createCombinedPopulation(Population parent, Population child) {
        
        List<Chromosome> combinedPopulace = new ArrayList<>();
        Population combinedPopulation = new Population();

        combinedPopulace.addAll(parent.getPopulace());
        combinedPopulace.addAll(child.getPopulace());
        combinedPopulation.setPopulace(combinedPopulace);
        
        return combinedPopulation;
    }
    
    /**
     * this method decodes the genetic code that is represented as a string of binary values, converted into
     * decimal value.
     * 
     * @param   geneticCode     the genetic code as an array of Allele. Refer Allele.java for more information
     * @return                  the decimal value of the corresponding binary string.
     */
    public static double decodeGeneticCode(final Allele[] geneticCode, String fileLocation) {
        
        ObjectConstrains objConstraint = new ObjectConstrains(fileLocation);
        
        ArrayList<String> nodes = objConstraint.getNodes();
        ArrayList<String> boObject = objConstraint.getboObject();
        ArrayList<String> nodeCalls = objConstraint.getnodeCalls();
        
        
        /*This is to identify the first locations of the business objects
        In this ArrayList the arraylist position is equal to the business object number and the value store in that position
        is equal to the current first position in the chromosome it is in
        */
        ArrayList firstPosition = new ArrayList();
      
        
        for(int i=0;i<geneticCode.length;i++){
            firstPosition.add(1000000);
            
        }
        
        
        ArrayList boPositionsBasedOnGene = new ArrayList();
        double value = 0;
        double valueBasedOnClustering = 0; //This contains the total fitness value based on clustering
        double valueBasedOnCalls = 0; //This contains the total fitness values based on number of method calls
        //This is the place where we are going to calculate the new fitness value
        
         /*************************This the place where we calculate the fitness based on BO categories****************************/
        for(int i = 0; i<geneticCode.length;i++){
            
            //First we will calculate the value based on the business object positioning
            //System.out.println("Index "+bit.getGene());
            String positionData = (String) boObject.get(geneticCode[i].getGene());
            int boObjectForPosition = Integer.parseInt(positionData); //This is to identify the business object based on the gene value and then take its position 
            boPositionsBasedOnGene.add(boObjectForPosition);
            
            //Then we have to check whether that is the position where the particular business object appears first. If not we have to find the position and save it
            if((int)firstPosition.get(boObjectForPosition) == 1000000){
                           
               firstPosition.set(boObjectForPosition, i); // so we can find the first occurence of the business object position 
            }
            
            
            //Now we can do the calculation for the clustering base on the business object
            int distanceDifference = i - (int)firstPosition.get(boObjectForPosition);
            
            //valueBasedOnClustering = valueBasedOnClustering + Math.pow(2, distanceDifference);
             valueBasedOnClustering = valueBasedOnClustering + (10*distanceDifference);
            
        }
        /*************************This the place where we calculate the fitness based on BO categories****************************/
        
        
         /*-------------------------This the place where we calculate the fitness based on message passing---------------------------*/
        int previousePositionValue = 0;
        int haveTheSameValues = 0;
        int sameValuePosition = 0; //This contains the first position which got the same value.
        List<List<Integer>> allNodeGroups = new ArrayList<List<Integer>>();//This will contain the nodes which got seperated
        ArrayList nodesGotSeperated = new ArrayList(); 
        
        
        for(int i = 0; i<boPositionsBasedOnGene.size(); i++){
            
            if(nodesGotSeperated.size() == 0){
                nodesGotSeperated = new ArrayList();
                previousePositionValue = (int)boPositionsBasedOnGene.get(i);
                if(!nodesGotSeperated.contains(geneticCode[i].getGene())){
                        nodesGotSeperated.add(geneticCode[i].getGene());
                }                
            }else if(previousePositionValue == (int)boPositionsBasedOnGene.get(i)){
                previousePositionValue = (int)boPositionsBasedOnGene.get(i);
                if(!nodesGotSeperated.contains(geneticCode[i].getGene())){
                        nodesGotSeperated.add(geneticCode[i].getGene());
                }                
            }else if(previousePositionValue != (int)boPositionsBasedOnGene.get(i)){
                allNodeGroups.add(nodesGotSeperated);
                nodesGotSeperated = new ArrayList();
                previousePositionValue = (int)boPositionsBasedOnGene.get(i);
                if(!nodesGotSeperated.contains(geneticCode[i].getGene())){
                        nodesGotSeperated.add(geneticCode[i].getGene());
                } 
            }
            
            

            
        }     
        allNodeGroups.add(nodesGotSeperated);
        int tempCallCost = 0;
        int internalCallCost = 0;
        //Processing the communications betwen the nodes. This is to get the cost for the communications
        for(int i =0; i < allNodeGroups.size(); i++){
            ArrayList nodesGotSeperatedT =  (ArrayList)allNodeGroups.get(i);
            
            for(int k = 0; k < nodesGotSeperatedT.size(); k++){
                
            }
            
            for(int j = 0; j < nodeCalls.size() ; j++){
            String splittedText[] = nodeCalls.get(j).toString().split("-");
            int one = Integer.parseInt(splittedText[0]);
            int two = Integer.parseInt(splittedText[1]);
            
            if(nodesGotSeperatedT.contains(one) && !nodesGotSeperatedT.contains(two)){
                valueBasedOnCalls = valueBasedOnCalls + (Integer.parseInt(splittedText[2]));
                tempCallCost = tempCallCost + Integer.parseInt(splittedText[2]); 
                //System.out.print("Value: "+valueBasedOnCalls);
            }else if(!nodesGotSeperatedT.contains(one) && nodesGotSeperatedT.contains(two)){
                valueBasedOnCalls = valueBasedOnCalls + (Integer.parseInt(splittedText[2])); 
                tempCallCost = tempCallCost + Integer.parseInt(splittedText[2]); 
            }
            
            if(nodesGotSeperatedT.contains(one) && nodesGotSeperatedT.contains(two)){
                internalCallCost = internalCallCost + (Integer.parseInt(splittedText[2]));
            }
            
        }
          
          tempCallCost = 0;
          internalCallCost = 0;  
        }
        
        /*-------------------------This the place where we calculate the fitness based on message passing---------------------------*/
        
        
        /*####################################This the place where we calculate the common values needed for avaialbility and scalability calculation####################################*/
        ArrayList<Double> internalCallsInEachCluster = new ArrayList<Double>(); //This will store the number of internal calls belong to each cluster
        ArrayList<Double> externalCallsInEachCluster = new ArrayList<Double>(); //This will store the number of external calls belong to each cluster
        
        List<List<Integer>> uniqueNodeClusters = new ArrayList<List<Integer>>(); //This will store the unique nodes related to each cluster
        ArrayList clusterNodes =  new ArrayList(); //Will keep the nodes related to each cluster
        double internalCallsValues = 0; //Will count the number of internal calls related to each cluster
        double externalCallValues = 0; //Will count the number of external calls related to each cluster
        
        int previouseBO = Integer.parseInt(boObject.get(geneticCode[0].getGene()).toString()); //Initaite the first BO valuse such that we can change it
        clusterNodes.add(geneticCode[0].getGene());
        
        
        //This is to identify the different clusters based on the BO each of the node belongs to 
        for(int i = 1; i < geneticCode.length; i++){
           if(Integer.parseInt(boObject.get(geneticCode[i].getGene()).toString()) == previouseBO){
               if(!clusterNodes.contains(geneticCode[i].getGene())){
                   clusterNodes.add(geneticCode[i].getGene());
               }
           }else{
               previouseBO = Integer.parseInt(boObject.get(geneticCode[i].getGene()).toString());
               uniqueNodeClusters.add(clusterNodes);
               clusterNodes =  new ArrayList();
               clusterNodes.add(geneticCode[i].getGene());
           }
            
        }
        
        uniqueNodeClusters.add(clusterNodes);
        
        //Here we calculate the internal and external call cost for each cluster
         for(int i = 0; i < uniqueNodeClusters.size(); i++){
            ArrayList temp =  (ArrayList)uniqueNodeClusters.get(i);
            if(temp.size() > 1){
                for(int j = 0; j < nodeCalls.size() ; j++){
                    String splittedText[] = nodeCalls.get(j).toString().split("-");
                    int one = Integer.parseInt(splittedText[0]);
                    int two = Integer.parseInt(splittedText[1]);
                    
                    if(temp.contains(one) && temp.contains(two)){
                        internalCallsValues  = internalCallsValues + Float.parseFloat(splittedText[2]);
                    }else if(!temp.contains(one) && temp.contains(two)){
                        externalCallValues  = externalCallValues + Float.parseFloat(splittedText[2]);
                    }else if(temp.contains(one) && !temp.contains(two)){
                        externalCallValues  = externalCallValues + Float.parseFloat(splittedText[2]);
                    }
                    
                }
                
                if(internalCallsValues == 0){
                    internalCallsValues = 1.0f; // Otherewise it would gibe an arithematic exception at the bottom when calculating the cost
                }
                
                externalCallsInEachCluster.add(externalCallValues);
                internalCallsInEachCluster.add(internalCallsValues);
                internalCallsValues = 0;
                externalCallValues = 0;
                
            }else{
                internalCallsInEachCluster.add(1.0);
                
                 for(int j = 0; j < nodeCalls.size() ; j++){
                    String splittedText[] = nodeCalls.get(j).toString().split("-");
                    int one = Integer.parseInt(splittedText[0]);
                    int two = Integer.parseInt(splittedText[1]);
                    
                    if(!temp.contains(one) && temp.contains(two)){
                        externalCallValues  = externalCallValues + Float.parseFloat(splittedText[2]);
                    }else if(temp.contains(one) && !temp.contains(two)){
                        externalCallValues  = externalCallValues + Float.parseFloat(splittedText[2]);
                    }
                   
                    
                 }
                 
                if(externalCallValues == 0){
                    externalCallValues = 1.0f; // Otherewise it would gibe an arithematic exception at the bottom when calculating the cost
                }
                 externalCallsInEachCluster.add(externalCallValues);
                 externalCallValues = 0;
            }
        }
        /*####################################This the place where we calculate the common values needed for avaialbility and scalability calculation####################################*/
        
        
        /*-------------------------This the place where we calculate the fitness based on avaialbility---------------------------*/
         float provisioing_time = Configuration.getCONTAINTER_PROVISION(Configuration.getNO_OF_CONTAINERS());
          float network_time = 0.0f;
          float execution_time = 0.0f;
          
          //Calculate the time it takes to reach the containes
          for(int i = 0; i<externalCallsInEachCluster.size(); i++){
              network_time = (float) (network_time + (externalCallsInEachCluster.get(i) * Configuration.getPACKET_SIZE())/Configuration.getNETWORK_BANDWIDTH());
          }
          
          //Calculate the execution time of the containers
          for(int i = 0; i<internalCallsInEachCluster.size(); i++){
              execution_time = (float) (execution_time + (internalCallsInEachCluster.get(i) * Configuration.getPACKET_SIZE())/Configuration.getPROCESS_COMPLEXITY());
          }
          
          
          float totalTimeForPacketProcess = provisioing_time + Configuration.getNO_OF_CONTAINERS()*network_time + Configuration.getNO_OF_CONTAINERS()*execution_time;
          
          float totalAvailabilitycost = totalTimeForPacketProcess * 100;
        /*-------------------------This the place where we calculate the fitness based on avaialbility---------------------------*/
        
        /*-------------------------This the place where we calculate the fitness based on scalability---------------------------*/
         //Total memory used for the process. Here we assume that the total memory requirement is depend on the number of internal calls (packets) and the number of internal calls (packets).
          float total_Memory = 0.0f;
          for(int i =0; i < externalCallsInEachCluster.size(); i++){
              total_Memory = total_Memory + (float) ((externalCallsInEachCluster.get(i)+ internalCallsInEachCluster.get(i))*Configuration.getPACKET_SIZE()) * 16;
          }
         
          //Based on the paper we calculated the the under provision time only
          //double scalability_Cost = total_Memory/Configuration.getCONTAINTER_PROVISION(Configuration.getNO_OF_CONTAINERS());
          double scalability_Cost = total_Memory*Configuration.getCONTAINTER_PROVISION(Configuration.getNO_OF_CONTAINERS());
          scalability_Cost = scalability_Cost;
        /*-------------------------This the place where we calculate the fitness based on scalability---------------------------*/
        
        
      
        //WriteToExternalFile.writeTextFile("datExtracted.txt", needToWrite);
        
        //This will be helpful for us in the further implementation
//        double value = 0;
//        String binaryString = "";
//        
//        for(Allele bit : geneticCode) binaryString += bit.getGene() ? "1" : "0";
//        for(int i = 0; i < binaryString.length(); i++) if(binaryString.charAt(i) == '1') value += Math.pow(2, binaryString.length() - 1 - i);
        value = valueBasedOnClustering + (valueBasedOnCalls*4) + totalAvailabilitycost + scalability_Cost;
        
        //double configValue = Math.pow(2, (Configuration.getCHROMOSOME_LENGTH()+1));
        double configValue = (Configuration.getCHROMOSOME_LENGTH()+1) *100000; //Since now we have four objective we have multiply the value by two
        
        double et = configValue - value;
        return et ;
    }
    
    /**
     * fitness is calculated using min-max normalization
     * 
     * @param   geneticCode     the genetic code whose fitness is to be calculated
     * @return                  the corresponding calculated fitness
     */
    public static double calculateFitness(Allele[] geneticCode, String fileLocation) {
       //This is the place where we have to calculate the fitness value.The total fitness value 
        
        
        //double aa = minMaxNormalization(decodeGeneticCode(geneticCode,fileLocation));
        return decodeGeneticCode(geneticCode,fileLocation);
    }
    
    /**
     * an implementation of min-max normalization
     * 
     * @param   value   the value that is to be normalized
     * @return          the normalized value
     */
    private static double minMaxNormalization(final double value) {
        return (((value - Configuration.ACTUAL_MIN) / (Configuration.ACTUAL_MAX - Configuration.ACTUAL_MIN)) * (Configuration.NORMALIZED_MAX - Configuration.NORMALIZED_MIN)) + Configuration.NORMALIZED_MIN;
    }
    
    /**
     * used to generate a random integer value
     * 
     * @return a random integer value
     */
    public static int generateRandomInt() {
        return ThreadLocalRandom.current().nextInt();
    }
    
    /**
     * a short hand for System.out.println().
     * 
     * @param string    the string to print to console.
     */
    public static void p(String string) {
        System.out.println(string);
    }
}
