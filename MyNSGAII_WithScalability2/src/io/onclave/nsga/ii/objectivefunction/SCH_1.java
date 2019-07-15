/*
 * This repository / codebase is Open Source and free for use and rewrite.
 */
package io.onclave.nsga.ii.objectivefunction;

import com.file.read.ObjectConstrains;
import io.onclave.nsga.ii.Interface.IObjectiveFunction;
import io.onclave.nsga.ii.configuration.Configuration;
import io.onclave.nsga.ii.datastructure.Allele;
import io.onclave.nsga.ii.datastructure.Chromosome;
import io.onclave.nsga.ii.datastructure.ParetoObject;
import java.util.ArrayList;

/**
 * the SCH objective function [f(x) = x^2]
 * 
 * @author  Debabrata Acharya <debabrata.acharya@icloud.com>
 * @version 1.0
 * @since   0.1
 */
public class SCH_1 implements IObjectiveFunction {
    //This is the one which count the cost of the clustering    
    
    private static final String AXIS_TITLE = "Fitness of Clustering";
    private ArrayList nodes = new ArrayList(); //This contains the information regarding the nodes that are in the graphs 
    private ArrayList boObject = new ArrayList(); //This contains the information about the business objects to which they should belong
    private ArrayList nodeCalls = new ArrayList();//This contains the information about number of calls between different nodes
    
    public SCH_1(String fileLocation){
        ObjectConstrains objConstraint = new ObjectConstrains(fileLocation);
        
        this.nodes = objConstraint.getNodes();
        this.boObject = objConstraint.getboObject();
        this.nodeCalls = objConstraint.getnodeCalls();
        
    }
    
    
    @Override
    public double objectiveFunction(final ParetoObject paretoObject) {
        return objectiveFunction(paretoObject.getChromosome());
    }
    
    @Override
    public double objectiveFunction(final Chromosome chromosome) {
        Allele[] geneticCode = chromosome.getGeneticCode();
        
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
        //System.out.println("valueBasedOnClustering: "+valueBasedOnClustering);
        //double configValue = Math.pow(2, (Configuration.getCHROMOSOME_LENGTH()+1));
        
        //double configValue = (Configuration.getCHROMOSOME_LENGTH()+1) * 20;
        double configValue = (Configuration.getCHROMOSOME_LENGTH()+1) * 10000;
        return configValue - valueBasedOnClustering;
       //return valueBasedOnClustering;
    }

   

    @Override
    public String getAxisTitle() {
        return AXIS_TITLE;
    }

  
}
