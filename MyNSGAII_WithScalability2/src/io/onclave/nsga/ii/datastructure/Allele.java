/*
 * This repository / codebase is Open Source and free for use and rewrite.
 */
package io.onclave.nsga.ii.datastructure;

import java.util.ArrayList;
import java.util.Random;

/**
 * this is a simulation of an allele in a biological chromosome that contains a gene value.
 * an array of alleles create the genetic code for the chromosome.
 * 
 * @author  Debabrata Acharya <debabrata.acharya@icloud.com>
 * @version 1.0
 * @since   0.1
 */
public class Allele {
    
    public Allele() {
        this(1);
    }
    
    public Allele(final int gene) {
        this.gene = gene;
    }
    
    private int gene;

    public int getGene() {
        return gene;
    }

    public void setGene(int gene) {
        this.gene = gene;
    }
    
    public void bitFlip() {
//        int changePosition1 = getRandomNumberInRange(0,8);
//        int changePostion2 = getRandomNumberInRange(0,8);
//        while(changePostion2 == changePosition1){
//            changePostion2 = getRandomNumberInRange(0,8);
//        }
//               
//        
//        if(this.gene == 9){
//            //this.gene = this.gene - getRandomNumberInRange(1,8);
//        }else{
//            //this.gene = this.gene+1;
//        }
        
    }
    
    
   
    
}
