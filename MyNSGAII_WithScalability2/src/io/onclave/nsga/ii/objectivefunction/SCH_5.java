/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.onclave.nsga.ii.objectivefunction;

import com.file.read.ObjectConstrains;
import io.onclave.nsga.ii.Interface.IObjectiveFunction;
import io.onclave.nsga.ii.configuration.Configuration;
import io.onclave.nsga.ii.datastructure.Allele;
import io.onclave.nsga.ii.datastructure.Chromosome;
import io.onclave.nsga.ii.datastructure.ParetoObject;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author N9572791
 */
public class SCH_5 implements IObjectiveFunction{
    
     private static final String AXIS_TITLE = "Scalability of Clustering";
    private ArrayList nodes = new ArrayList(); //This contains the information regarding the nodes that are in the graphs 
    private ArrayList boObject = new ArrayList(); //This contains the information about the business objects to which they should belong
    private ArrayList nodeCalls = new ArrayList();//This contains the information about number of calls between different nodes

    
    public SCH_5(String fileLocation){
        ObjectConstrains objConstraint = new ObjectConstrains(fileLocation);
        
        this.nodes = objConstraint.getNodes();
        this.boObject = objConstraint.getboObject();
        this.nodeCalls = objConstraint.getnodeCalls();
        
        
    }
    
   
    
    @Override
    public double objectiveFunction(Chromosome chromosome) {
        Allele[] geneticCode = chromosome.getGeneticCode();
        
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
        
        
         //Now we need to calcualte the cost for the scalability
          double configValue = (Configuration.getCHROMOSOME_LENGTH()+1) * 10000;
          
          
          //Total memory used for the process. Here we assume that the total memory requirement is depend on the number of internal calls (packets) and the number of internal calls (packets).
          float total_Memory = 0.0f;
          for(int i =0; i < externalCallsInEachCluster.size(); i++){
              total_Memory = total_Memory + (float) ((externalCallsInEachCluster.get(i)+ internalCallsInEachCluster.get(i))*Configuration.getPACKET_SIZE()) * 16;
          }
         
          //Based on the paper we calculated the the under provision time only
          //double scalability_Cost = total_Memory/Configuration.getCONTAINTER_PROVISION(Configuration.getNO_OF_CONTAINERS());
          double scalability_Cost = total_Memory*Configuration.getCONTAINTER_PROVISION(Configuration.getNO_OF_CONTAINERS());
          scalability_Cost = scalability_Cost;
        
        return configValue - (int)scalability_Cost;  
    }

    @Override
    public double objectiveFunction(ParetoObject paretoObject) {
        return objectiveFunction(paretoObject.getChromosome());
    }

    @Override
    public String getAxisTitle() {
        return AXIS_TITLE;
    }
    
}
