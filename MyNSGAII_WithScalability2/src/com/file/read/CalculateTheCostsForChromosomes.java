/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.file.read;

import io.onclave.nsga.ii.Interface.IObjectiveFunction;
import io.onclave.nsga.ii.configuration.Configuration;
import io.onclave.nsga.ii.datastructure.Chromosome;
import io.onclave.nsga.ii.datastructure.Population;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author N9572791
 */
public class CalculateTheCostsForChromosomes {
    
    /*
    * In here we calculate the cost for all the chromosomes to make sure that it would be claculated only one time. Otherwise it would take lot of time to calculate it an the
    * it will slow down the whole process
    */
    
    public static List<List<Double>> calculateTheCost(Population population){
       List<Chromosome> populace = population.getPopulace();
       
        List<IObjectiveFunction> objectives = Configuration.getObjectives();
        List<List<Double>> allObjectiveValues = new ArrayList<List<Double>>();
        ArrayList objectiveOneCost = new ArrayList();
        ArrayList objectiveTwoCost = new ArrayList();
        ArrayList objeciveThreeCost = new ArrayList();
        ArrayList objeciveFourCost = new ArrayList();
       
       
       
       for(Chromosome chromosome : populace) {
           objectiveOneCost.add(objectives.get(0).objectiveFunction(chromosome));
           objectiveTwoCost.add(objectives.get(1).objectiveFunction(chromosome)); 
           objeciveThreeCost.add(objectives.get(2).objectiveFunction(chromosome));
           objeciveFourCost.add(objectives.get(3).objectiveFunction(chromosome));
       }
       
       allObjectiveValues.add(objectiveOneCost);
       allObjectiveValues.add(objectiveTwoCost);
       allObjectiveValues.add(objeciveThreeCost);
       allObjectiveValues.add(objeciveFourCost);
       return allObjectiveValues;
    }
    
    
    public static List<List<Double>> calculateTheCostChromos(List<Chromosome> population){
       
       
        List<IObjectiveFunction> objectives = Configuration.getObjectives();
        List<List<Double>> allObjectiveValues = new ArrayList<List<Double>>();
        ArrayList objectiveOneCost = new ArrayList();
        ArrayList objectiveTwoCost = new ArrayList();
        ArrayList objeciveThreeCost = new ArrayList();
        ArrayList objeciveFourCost = new ArrayList();
       
       
       
       for(int i = 0; i < population.size(); i++ ) {
           objectiveOneCost.add(objectives.get(0).objectiveFunction(population.get(i)));
           objectiveTwoCost.add(objectives.get(1).objectiveFunction(population.get(i)));  
           objeciveThreeCost.add(objectives.get(2).objectiveFunction(population.get(i)));
           objeciveFourCost.add(objectives.get(3).objectiveFunction(population.get(i)));
       }
       allObjectiveValues.add(objectiveOneCost);
       allObjectiveValues.add(objectiveTwoCost);
       allObjectiveValues.add(objeciveThreeCost);
       allObjectiveValues.add(objeciveFourCost);
       return allObjectiveValues;
    }
}
