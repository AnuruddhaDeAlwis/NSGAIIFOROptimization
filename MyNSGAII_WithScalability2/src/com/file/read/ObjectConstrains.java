/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.file.read;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author N9572791
 */
public class ObjectConstrains {
    
    private ArrayList nodes = new ArrayList(); //This contains the information regarding the nodes that are in the graphs 
    private ArrayList boObject = new ArrayList(); //This contains the information about the business objects to which they should belong
    private ArrayList nodeCalls = new ArrayList(); //This contains the numebr of calls between different nodes
   // private ArrayList nodeClasses = new ArrayList(); //This contians the classes each node belongs to
    
    
       public ObjectConstrains(String fileLocation) {
           
           String fileInformation = ReadExternalInformation.readTextFile(fileLocation);
           
           String splittedText[] = fileInformation.split("@@@");
           String nodesSplitted[] = splittedText[0].split(",");
           String boObjectSplitted[] = splittedText[1].split(",");
           String nodeCallsSplitted[] = splittedText[2].split(",");
           //String nodeClassesSplitted[] = splittedText[3].split(",");
           
           
           for(int i = 0; i < nodesSplitted.length; i++){
               nodes.add(nodesSplitted[i]);
               boObject.add(boObjectSplitted[i]);
               //nodeClasses.add(nodeClassesSplitted[i]);
           }
           
           
           for(int i = 0; i < nodeCallsSplitted.length; i++){
               nodeCalls.add(nodeCallsSplitted[i]);               
           }
          
       }
    
    
       public ArrayList getNodes(){
           return this.nodes;
       }
   
       
       public ArrayList getboObject(){
           return this.boObject;
       }
       
       
       public ArrayList getnodeCalls(){
           return this.nodeCalls;
       }
    
//       public ArrayList getnodeClasses(){
//           return this.nodeClasses;
//       }
    
}
