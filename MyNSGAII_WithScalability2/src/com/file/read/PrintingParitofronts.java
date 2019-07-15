/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.file.read;

import io.onclave.nsga.ii.configuration.Configuration;
import io.onclave.nsga.ii.datastructure.Allele;
import io.onclave.nsga.ii.datastructure.Chromosome;
import io.onclave.nsga.ii.datastructure.Population;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author n9572791
 */
public class PrintingParitofronts {
    
    public static void printParitoFronts(final Population population, int position){
       
       WriteToExternalFile.writeTextFile("datExtracted.txt", "@@@@@@@@@@@@@@@@@------------- IN GRAPH PLOT"+position+"----------------------@@@@@@@@@@@@@@@@@@");
         List<Chromosome> populace = population.getPopulace();
        for(int i = 0;i < populace.size(); i++){
            String toWrite = "";
            Allele[] genes = populace.get(i).getGeneticCode();
            toWrite = toWrite+"The Gene---:";
            System.out.print("The Gene---:");
            for(int j =0; j < genes.length ; j++){
                System.out.print(genes[j].getGene()+"|");
                toWrite = toWrite+genes[j].getGene()+"|";
            }
            toWrite = toWrite+System.getProperty( "line.separator" )+"######################### Clustering Value: "+Configuration.getObjectives().get(0).objectiveFunction(populace.get(i))+ 
                    "##### CallValue: "+Configuration.getObjectives().get(1).objectiveFunction(populace.get(i))+
                    "##### Avaialability Value: "+Configuration.getObjectives().get(2).objectiveFunction(populace.get(i))+
                    "##### Scalability Value: "+Configuration.getObjectives().get(3).objectiveFunction(populace.get(i))+
                    "##### Avaialability Perentage: "+Configuration.getObjectives().get(2).objectiveFunction(populace.get(i))/(Configuration.getCHROMOSOME_LENGTH()+1) * 10000+
                    "##### Scalability Percentage: "+Configuration.getObjectives().get(3).objectiveFunction(populace.get(i))/(Configuration.getCHROMOSOME_LENGTH()+1) * 10000+
                    System.getProperty( "line.separator" );
             WriteToExternalFile.writeTextFile(Configuration.writeLocation, toWrite);
             
             decodeGeneticCodeToWrite(genes, Configuration.readLocation);
             
    
    }
    }
    
    
    
      public static double decodeGeneticCodeToWrite(final Allele[] geneticCode, String fileLocation) {
        
        ObjectConstrains objConstraint = new ObjectConstrains(fileLocation);
        
        ArrayList<String> nodes = objConstraint.getNodes();
        ArrayList<String> boObject = objConstraint.getboObject();
        ArrayList<String> nodeCalls = objConstraint.getnodeCalls();
        
        
        /*This is to identify the first locations of the business objects
        In this ArrayList the arraylist position is equal to the business object number and the value store in that position
        is equal to the current first position in the chromosome it is in
        */
        ArrayList firstPosition = new ArrayList();
        String needToWrite = "";
        needToWrite = needToWrite + System.getProperty( "line.separator" )+System.getProperty( "line.separator" );
        
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
             valueBasedOnClustering = valueBasedOnClustering + (3*distanceDifference);
            
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
            needToWrite = needToWrite + i +" Cluster Nodes: |";
            for(int k = 0; k < nodesGotSeperatedT.size(); k++){
                needToWrite = needToWrite +nodesGotSeperatedT.get(k)+",";
            }
            needToWrite = needToWrite+System.getProperty( "line.separator" );
            needToWrite = needToWrite +" Cost: |";
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
          needToWrite = needToWrite +tempCallCost+","+System.getProperty( "line.separator" ); 
          needToWrite = needToWrite +" Internal Call Cost: |"; 
          needToWrite = needToWrite +internalCallCost+","+System.getProperty( "line.separator" ); 
          tempCallCost = 0;
          internalCallCost = 0;  
        }
        
        /*-------------------------This the place where we calculate the fitness based on message passing---------------------------*/
        WriteToExternalFile.writeTextFile(Configuration.writeLocation, needToWrite);
        
        value = valueBasedOnClustering + (valueBasedOnCalls*7);
        
        //double configValue = Math.pow(2, (Configuration.getCHROMOSOME_LENGTH()+1));
        double configValue = (Configuration.getCHROMOSOME_LENGTH()+1) *10000;
        //double configValue = (Configuration.getCHROMOSOME_LENGTH()+1) *20;
        double et = configValue - value;
        return et ;
    }
    
}
