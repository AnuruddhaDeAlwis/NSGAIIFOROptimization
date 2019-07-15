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
import java.util.List;

/**
 * the SCH objective function [f(x) = (x - 2)^2]
 * 
 * @author  Debabrata Acharya <debabrata.acharya@icloud.com>
 * @version 1.0
 * @since   0.1
 */
public class SCH_2 implements IObjectiveFunction {
    
    //This is the one which count the cost of the node calls
        
    private static final String AXIS_TITLE = "Fitness of calls";
    private ArrayList nodes = new ArrayList(); //This contains the information regarding the nodes that are in the graphs 
    private ArrayList boObject = new ArrayList(); //This contains the information about the business objects to which they should belong
    private ArrayList nodeCalls = new ArrayList();//This contains the information about number of calls between different nodes
    
    public SCH_2(String fileLocation){
         ObjectConstrains objConstraint = new ObjectConstrains(fileLocation);
        
        this.nodes = objConstraint.getNodes();
        this.boObject = objConstraint.getboObject();
        this.nodeCalls = objConstraint.getnodeCalls();
    }

    @Override
    public double objectiveFunction(Chromosome chromosome) {
        ArrayList boPositionsBasedOnGene = new ArrayList();
        Allele[] geneticCode = chromosome.getGeneticCode();
        double valueBasedOnCalls = 0; //This contains the total fitness values based on number of method calls
        
         for(int i = 0; i<geneticCode.length;i++){
            
            //First we will calculate the value based on the business object positioning
            String boObjectSt = (String) boObject.get(geneticCode[i].getGene());
            int boObjectForPosition = Integer.parseInt(boObjectSt); //This is to identify the business object based on the gene value and then take its position 
            boPositionsBasedOnGene.add(boObjectForPosition);            
        }
        
        
         /*-------------------------This the place where we calculate the fitness based on message passing---------------------------*/
        int previousePositionValue = 0;
        int haveTheSameValues = 0;
        int sameValuePosition = 0; //This contains the first position which got the same value.
        List<List<Integer>> allNodeGroups = new ArrayList<List<Integer>>();
        ArrayList nodesGotSeperated = new ArrayList(); //This will contain the nodes which got seperated
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
            
            
//            if(previousePositionValue != (int)boPositionsBasedOnGene.get(i) && haveTheSameValues == 0){
//                previousePositionValue = (int)boPositionsBasedOnGene.get(i);
//                Allele bit = (Allele)geneticCode[i];
//                if(!nodesGotSeperated.contains(bit.getGene())){
//                        nodesGotSeperated.add(bit.getGene());
//                }                
//            }else if(previousePositionValue != (int)boPositionsBasedOnGene.get(i) && haveTheSameValues == 1){
//                previousePositionValue = (int)boPositionsBasedOnGene.get(i);
//                               
//                Allele bit = (Allele)geneticCode[sameValuePosition];
//                Allele bit1 = (Allele)geneticCode[i];
//                if(!nodesGotSeperated.contains(bit.getGene())){
//                    nodesGotSeperated.add(bit.getGene());
//                }
//                
//                if(!nodesGotSeperated.contains(bit1.getGene())){
//                    nodesGotSeperated.add(bit1.getGene()); 
//                }
//                
//                haveTheSameValues = 0;
//                sameValuePosition = 0;
//                
//            }else{
//                haveTheSameValues = 1;
//                if(sameValuePosition == 0){
//                    sameValuePosition = i;
//                }
//                
//            }
//            
//            if(i == boPositionsBasedOnGene.size()-1){
//                Allele bit = (Allele)geneticCode[i];
//                 if(!nodesGotSeperated.contains(bit.getGene())){
//                    nodesGotSeperated.add(bit.getGene());
//                }
//            }
//            
            
        }     
        allNodeGroups.add(nodesGotSeperated);
        //Processing the communications betwen the nodes. This is to get the cost for the communications
        
        for(int i =0; i < allNodeGroups.size(); i++){
            ArrayList nodesGotSeperatedT =  (ArrayList)allNodeGroups.get(i);
            
            for(int j = 0; j < nodeCalls.size() ; j++){
            String splittedText[] = nodeCalls.get(j).toString().split("-");
            int one = Integer.parseInt(splittedText[0]);
            int two = Integer.parseInt(splittedText[1]);
            
            if(nodesGotSeperatedT.contains(one) && !nodesGotSeperatedT.contains(two)){
                valueBasedOnCalls = valueBasedOnCalls + (Integer.parseInt(splittedText[2]));
                //System.out.print("Value: "+valueBasedOnCalls);
            }else if(!nodesGotSeperatedT.contains(one) && nodesGotSeperatedT.contains(two)){
                valueBasedOnCalls = valueBasedOnCalls + (Integer.parseInt(splittedText[2]));
            }

        }
            
        }
        
        /*-------------------------This the place where we calculate the fitness based on message passing---------------------------*/
        
       // System.out.println("valueBasedOnCalls: "+valueBasedOnCalls);
       //double configValue = Math.pow(2, (Configuration.getCHROMOSOME_LENGTH()+1));
       valueBasedOnCalls = valueBasedOnCalls*4;
       //double configValue = (Configuration.getCHROMOSOME_LENGTH()+1) * 20;
       double configValue = (Configuration.getCHROMOSOME_LENGTH()+1) * 10000;
        return configValue - valueBasedOnCalls;
        //return valueBasedOnCalls;
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
