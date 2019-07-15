///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package io.onclave.nsga.ii.objectivefunction;
//
//import com.file.read.ObjectConstrains;
//import io.onclave.nsga.ii.Interface.IObjectiveFunction;
//import io.onclave.nsga.ii.datastructure.Allele;
//import io.onclave.nsga.ii.datastructure.Chromosome;
//import io.onclave.nsga.ii.datastructure.ParetoObject;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// *
// * @author N9572791
// */
//
//
////The initail idea of using the number of classes to decide the time it takes to provision thte services has changes. Instead we use the linear equations thing when you have 
////multiple request initiating multiple nodes. This is done in the SCH_4 class
//public class SCH_3 implements IObjectiveFunction{
//    
//    private static final String AXIS_TITLE = "Fitness of Clustering";
//    private ArrayList nodes = new ArrayList(); //This contains the information regarding the nodes that are in the graphs 
//    private ArrayList boObject = new ArrayList(); //This contains the information about the business objects to which they should belong
//    private ArrayList nodeCalls = new ArrayList();//This contains the information about number of calls between different nodes
//    private ArrayList nodeClasses = new ArrayList(); //This contians the information about the classes each node belongs to
//
//    
//    public SCH_3(String fileLocation){
//        ObjectConstrains objConstraint = new ObjectConstrains(fileLocation);
//        
//        this.nodes = objConstraint.getNodes();
//        this.boObject = objConstraint.getboObject();
//        this.nodeCalls = objConstraint.getnodeCalls();
//        this.nodeClasses = objConstraint.getnodeClasses();
//        
//        
//    }
//    
//    @Override
//    public double objectiveFunction(final ParetoObject paretoObject) {
//        return objectiveFunction(paretoObject.getChromosome());
//    }
//    
//    @Override
//    public double objectiveFunction(final Chromosome chromosome) {
//        Allele[] geneticCode = chromosome.getGeneticCode();
//        
//        List<List<Integer>> uniqueClassesCluster = new ArrayList<List<Integer>>(); //This will store the unique classes related to each cluster
//        ArrayList internalCallsInEachCluster = new ArrayList(); //This will store the number of internal calls belong to each cluster
//        
//        ArrayList clusterClasses =  new ArrayList(); //Will keep the classes related to each cluster
//        int internalCallsValues = 0; //Will count the number of internal calls related to each cluster
//        
//        int previouseBO = Integer.parseInt(boObject.get(geneticCode[0].getGene()).toString()); //Initaite the first BO valuse such that we can change it
//        clusterClasses.add(nodeClasses.get(geneticCode[0].getGene()));
//      
//       
//        //Have filter out the clusters based on BOs and then identify the classes related to each cluster and number of internal calls related to each cluster
//        for(int i = 1; i < geneticCode.length; i++){
//           if(Integer.parseInt(boObject.get(geneticCode[i].getGene()).toString()) == previouseBO){
//               if(!clusterClasses.contains(nodeClasses.get(geneticCode[i].getGene()))){
//                   clusterClasses.add(nodeClasses.get(geneticCode[i].getGene()));
//               }
//           }else{
//               previouseBO = Integer.parseInt(boObject.get(geneticCode[i].getGene()).toString());
//               uniqueClassesCluster.add(clusterClasses);
//               clusterClasses =  new ArrayList();
//               clusterClasses.add(nodeClasses.get(geneticCode[i].getGene()));
//           }
//            
//        }
//        
//        
//        
//        
//        
//        
//        
//        
//        for(int i = 0; i < uniqueClassesCluster.size(); i++){
//            ArrayList temp =  (ArrayList)uniqueClassesCluster.get(i);
//            if(temp.size() > 1){
//                for(int j = 0; j < nodeCalls.size() ; j++){
//                    String splittedText[] = nodeCalls.get(j).toString().split("-");
//                    String one = splittedText[0];
//                    String two = splittedText[1];
//                    
//                    if(temp.contains(one) && temp.contains(two)){
//                        internalCallsValues  = internalCallsValues + Integer.parseInt(splittedText[2]);
//                    }
//                    
//                }
//                
//                internalCallsInEachCluster.add(internalCallsValues);
//                internalCallsValues = 0;
//                
//            }else{
//                internalCallsInEachCluster.add(0);
//            }
//        }
//        
//        
//       
//        return 10;
//    }
//
//   
//
//    @Override
//    public String getAxisTitle() {
//        return AXIS_TITLE;
//    }
//    
//}
