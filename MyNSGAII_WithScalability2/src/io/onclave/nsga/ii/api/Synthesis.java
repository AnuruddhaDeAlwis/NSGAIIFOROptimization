/*
 * This repository / codebase is Open Source and free for use and rewrite.
 */
package io.onclave.nsga.ii.api;

import com.file.read.ObjectConstrains;
import io.onclave.nsga.ii.configuration.Configuration;
import io.onclave.nsga.ii.datastructure.Allele;
import io.onclave.nsga.ii.datastructure.Chromosome;
import io.onclave.nsga.ii.datastructure.Population;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This is the synthesis class that does many of the under-the-hood work (biological simulation) that is abstracted/encapsulated
 * by other classes at the business/controller layer.
 * 
 * @author  Debabrata Acharya <debabrata.acharya@icloud.com>
 * @version 1.1
 * @since   0.2
 */
public class Synthesis {
    
    private static final Random LOCAL_RANDOM = new Random();
    
    /**
     * depending on the settings available in the Configuration.java file, this method synthesizes a
     * random population of chromosomes with pseudo-randomly generated genetic code for each chromosome.
     * 
     * @return  a randomly generated population
     */
    public static Population syntesizePopulation() {
        
        List<Chromosome> populace = new ArrayList<>();
        
        /**
         * the number of chromosomes in the population is received from the Configuration.java file
         */
        for(int i = 0; i < Configuration.getPOPULATION_SIZE(); i++) {
            
            Chromosome chromosome = new Chromosome();
            chromosome.setGeneticCode(synthesizeGeneticCode(Configuration.getCHROMOSOME_LENGTH()));
            populace.add(chromosome);
        }
        
        return new Population(populace);
    }
    
    /**
     * a child population of the same size as the parent is synthesized from the parent population
     * 
     * @param   parent  the parent population object
     * @return          a child population synthesized from the parent population
     */
    public static Population synthesizeChild(Population parent, ObjectConstrains obj) {
        
        Population child = new Population();
        List<Chromosome> populace = new ArrayList<>();
        
        /**
         * child chromosomes undergo crossover and mutation.
         * the child chromosomes are selected using binary tournament selection.
         * crossover returns an array of exactly two child chromosomes synthesized from two parent
         * chromosomes.
         */
        while(populace.size() < Configuration.getPOPULATION_SIZE())
            for(Chromosome chromosome : crossover(binaryTournamentSelection(parent), binaryTournamentSelection(parent), obj))
                populace.add(mutation(chromosome));
       
        
        
        child.setPopulace(populace);
        
        return child;
    }
    
    /**
     * this is an implementation of basic binary tournament selection.
     * for a tournament of size t, select t individuals (randomly) from population and determine winner of
     * tournament with the highest fitness value.
     * in case of binary tournament selection, t = 2.
     * 
     * refer [https://stackoverflow.com/questions/36989783/binary-tournament-selection] for more information.
     * 
     * @param   population  the population from which a child chromosome is to be selected
     * @return              the selected child chromosome
     */
    private static Chromosome binaryTournamentSelection(Population population) {
        
        Chromosome individual1 = population.getPopulace().get(LOCAL_RANDOM.nextInt(population.getPopulace().size()));
        Chromosome individual2 = population.getPopulace().get(LOCAL_RANDOM.nextInt(population.getPopulace().size()));
        
        if(individual1.getFitness() > individual2.getFitness()) return individual1; else return individual2;
    }
    
 /**
     * this is a basic implementation of uniform crossover where the crossover/break point is the middle
     * of the chromosomes. The genetic code of both the parent chromosomes are broken from the middle
     * and crossover is done to create two child chromosomes.
     * crossover probability is considered.
     * 
     * @param   chromosome1     the first parent chromosome taking part in crossover
     * @param   chromosome2     the second parent chromosome taking part in crossover
     * @return                  an array of exactly two child chromosomes synthesized from two parent chromosomes.
     */
    public static Chromosome[] crossover(Chromosome chromosomeOne,Chromosome chromosomeTwo, ObjectConstrains obj) {
        
      
        
       
       
       
       Allele[] chromosomeOnegeneCode = chromosomeOne.getGeneticCode();
       Allele[] chromosomeTwogeneCode = chromosomeTwo.getGeneticCode();
       
       Chromosome chromosome1 = novelMutatedChromosome(chromosomeOnegeneCode, obj);
       Chromosome chromosome2 = novelMutatedChromosome(chromosomeTwogeneCode, obj); 
       
        
      ArrayList genticC1 = new ArrayList();
       ArrayList genticC2 = new ArrayList();
        
       Allele[] geneticCode1 = new Allele[Configuration.getCHROMOSOME_LENGTH()];
        Allele[] geneticCode2 = new Allele[Configuration.getCHROMOSOME_LENGTH()];
        Allele[] chromosome1geneCode = chromosome1.getGeneticCode();
        Allele[] chromosome2geneCode = chromosome2.getGeneticCode();
        Chromosome[] childChromosomes = {new Chromosome(), new Chromosome()};
        int breakPoint = Configuration.getCHROMOSOME_LENGTH() / 2;
        /**
         * generating a new random float value and if this value is less than equal to the
         * crossover probability mentioned in the Configuration file, then crossover occurs,
         * otherwise the parents themselves are copied as child chromosomes.
         */
        if(LOCAL_RANDOM.nextFloat() <= Configuration.getCROSSOVER_PROBABILITY()) {
            
            for(int i = 0; i < Configuration.getCHROMOSOME_LENGTH(); i++) {
            
                if(i <= breakPoint) {
                    genticC1.add(chromosome1geneCode[i].getGene());
                    genticC2.add(chromosome2geneCode[i].getGene());
                    geneticCode1[i] = chromosome1geneCode[i];
                    geneticCode2[i] = chromosome2geneCode[i];
                } else {
                    int changedGene1 = 0;
                    int changedGene2 = 0;
                    for(int j =0; j < Configuration.getCHROMOSOME_LENGTH(); j++){
                        if(!genticC1.contains(chromosome2geneCode[j].getGene()) && changedGene1 == 0){
                            genticC1.add(chromosome2geneCode[j].getGene());
                            geneticCode1[i] = chromosome2geneCode[j];
                            changedGene1 = 1;
                        }
                         if(!genticC2.contains(chromosome1geneCode[j].getGene()) && changedGene2 == 0){
                             genticC2.add(chromosome1geneCode[j].getGene());
                             geneticCode2[i] = chromosome1geneCode[j];
                             changedGene2 = 1;
                        }
                    }
                    
//                    geneticCode1[i] = chromosome2geneCode[i];
//                    geneticCode2[i] = chromosome1geneCode[i];
                }
            }
            
            int sizeNeeded = genticC1.size();
            
//            for(int i = 0; i < genticC1.size(); i++ ){
//                geneticCode1[i] = (Allele)genticC1.get(i);
//                geneticCode2[i] = (Allele)genticC2.get(i);
//            }
            

            childChromosomes[0].setGeneticCode(geneticCode1);
            childChromosomes[1].setGeneticCode(geneticCode2);
        } else {
            childChromosomes[0] = chromosome1;
            childChromosomes[1] = chromosome2;
        }
        
        //System.out.println("Cross over");
        return childChromosomes;
    }
    
    
    
    public static Chromosome novelMutatedChromosome(Allele[] chromosome1geneCode, ObjectConstrains obj){
        
         ArrayList genticC1 = new ArrayList();
         Allele[] geneticCode1 = new Allele[Configuration.getCHROMOSOME_LENGTH()];
      
        
        List<List<Integer>> allNodeGroups = new ArrayList<List<Integer>>();
        ArrayList restOfTheNodes = new ArrayList();
        
        ArrayList temp = new ArrayList();
        temp.add(0);
        ArrayList boObjects = obj.getboObject();
        int boObjectRelated = Integer.parseInt(boObjects.get(chromosome1geneCode[0].getGene()).toString());
        
        for(int i = 1; i < chromosome1geneCode.length; i++){
            
            if(Integer.parseInt(boObjects.get(chromosome1geneCode[i].getGene()).toString()) == boObjectRelated){
                //temp.add(chromosome1geneCode[i].getGene());
                temp.add(i);
            }else if(temp.size() > 1){
                allNodeGroups.add(temp);
                temp = new ArrayList();
                temp.add(i);
//                temp.add(chromosome1geneCode[i].getGene());
                boObjectRelated = Integer.parseInt(boObjects.get(chromosome1geneCode[i].getGene()).toString());
            }else if(temp.size()== 1 && !restOfTheNodes.contains(temp.get(0))){
                restOfTheNodes.add(temp.get(0));
                temp = new ArrayList();
                temp.add(i);
                //temp.add(chromosome1geneCode[i].getGene());
                boObjectRelated = Integer.parseInt(boObjects.get(chromosome1geneCode[i].getGene()).toString());
            }
            
        }
        
        if(temp.size() > 1){
            allNodeGroups.add(temp);
        }else{
            restOfTheNodes.add(temp.get(0));
        }
        
       
        
        
        int numbersToBeSpserated = restOfTheNodes.size()/allNodeGroups.size();
        
        int constant = 0;
        while(allNodeGroups.size() > 0){
            if(allNodeGroups.size() > 1){
                int pos = getRandomNumberInRange(0, allNodeGroups.size()-1);
                ArrayList temp1 = (ArrayList)allNodeGroups.get(pos);
                for(int j = 0; j < temp1.size(); j++){
                    genticC1.add((int)temp1.get(j));
                }
                allNodeGroups.remove(pos);
            }else if(allNodeGroups.size() != 0){
                ArrayList temp1 = (ArrayList)allNodeGroups.get(0);
                for(int j = 0; j < temp1.size(); j++){
                    genticC1.add((int)temp1.get(j));
                }
                allNodeGroups.remove(0);
            }
            
             if(restOfTheNodes.size() > 1){
                int position = getRandomNumberInRange(0, restOfTheNodes.size()-1);
                genticC1.add((int)restOfTheNodes.get(position));
                restOfTheNodes.remove(position);
            }else if(restOfTheNodes.size() != 0){
                genticC1.add((int)restOfTheNodes.get(0));
                restOfTheNodes.remove(0);
            }

        }
        
        
        for(int i = 0; i < restOfTheNodes.size(); i++){
            genticC1.add((int)restOfTheNodes.get(i));
            
            //genticC1.add(restOfTheNodes.get(i));
        }
        
        
        for(int i = 0; i < genticC1.size(); i++ ){
                geneticCode1[i] = chromosome1geneCode[(int)genticC1.get(i)];
                //System.out.print(chromosome1geneCode[(int)genticC1.get(i)].getGene()+",");
            }
        
        Chromosome childChromosomes = new Chromosome();
        childChromosomes.setGeneticCode(geneticCode1);
        
        return childChromosomes;
    }
    
    /**
     * in this mutation operation implementation, a random bit-flip takes place.
     * a random float value is generated and if this value is less than equal to the mutation
     * probability defined in Configuration, then mutation takes place, otherwise the original
     * chromosome is returned.
     * 
     * @param   chromosome  the chromosome over which the mutation takes place
     * @return              the mutated chromosome
     */
    private static Chromosome mutation(Chromosome chromosome) {
        
        if(LOCAL_RANDOM.nextFloat() <= Configuration.getMUTATION_PROBABILITY()) {
            
            Allele[] geneticCode = chromosome.getGeneticCode();
            
            int changePosition1 = getRandomNumberInRange(0,Configuration.getCHROMOSOME_LENGTH()-1);
            int changePosition2 = getRandomNumberInRange(0,Configuration.getCHROMOSOME_LENGTH()-1);
            while(changePosition2 == changePosition1){
                changePosition2 = getRandomNumberInRange(0,Configuration.getCHROMOSOME_LENGTH()-1);
            }
            
            Allele one = geneticCode[changePosition1];
            Allele two = geneticCode[changePosition2]; 
            
            geneticCode[changePosition1] = two;
            geneticCode[changePosition2] = one;
            
            //geneticCode[LOCAL_RANDOM.nextInt(geneticCode.length)].bitFlip();
            chromosome.setGeneticCode(geneticCode);
        }
        
        return chromosome;
    }
    
    
    
    /*
    This is used to create a random number for the changin of the chromosome
    */
     private static int getRandomNumberInRange(int min, int max) {

		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}

		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}
    
    
    
    
    
    
    /**
     * a genetic code as an array of Alleles is synthesized.
     * refer Allele.java for more information.
     * 
     * @param   length  the required length of the genetic code.
     * @return          the synthesized genetic code.
     */
    public static Allele[] synthesizeGeneticCode(final int length) {
        
        Allele[] geneticCode = new Allele[length];
        
        ArrayList<Integer> theGeneticCode = new ArrayList();
        
        
        for(int i = 0; i < length; i++){
                //int x = synthesizeAllele(theGeneticCode);
                theGeneticCode.add(i);
        }
        
        int one = getRandomNumberInRange(0,Configuration.getCHROMOSOME_LENGTH()-1);
        int two = 0;
        
        while(one==two){
            two = getRandomNumberInRange(0,Configuration.getCHROMOSOME_LENGTH()-1);
        }
        
        int firstOne = theGeneticCode.get(one);
        int secondOne = theGeneticCode.get(two);
        
        for(int i = 0; i < length; i++){
            if(i == one){
                geneticCode[i] = new Allele(secondOne);
            }else if(i == two){
                geneticCode[i] = new Allele(firstOne);  
            }else{
                geneticCode[i] = new Allele((int)theGeneticCode.get(i));  
            }
        }
            
        
        return geneticCode;
    }
    
    /**
     * an allele object with a randomly selected boolean gene value is synthesized.
     * 
     * @return  a randomly generated Allele object
     */
    public static int synthesizeAllele(ArrayList theGeneticCode) {
        return (getRandomNumberInRange(0, Configuration.getCHROMOSOME_LENGTH()-1, theGeneticCode));
    }
    
    
    private static int getRandomNumberInRange(int min, int max, ArrayList theGeneticCode) {

		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}

                int randomValue = 0;
                
                while(true){
                    randomValue = new Random().nextInt((max - min) + 1) + min;
                    if(!theGeneticCode.contains(randomValue)){
                        break;
                    }
                }
                                
		
              return randomValue;  
		
	}
}
