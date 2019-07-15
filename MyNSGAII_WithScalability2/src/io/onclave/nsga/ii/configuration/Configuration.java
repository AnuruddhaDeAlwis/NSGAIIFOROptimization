/*
 * This repository / codebase is Open Source and free for use and rewrite.
 */
package io.onclave.nsga.ii.configuration;

import io.onclave.nsga.ii.Interface.IObjectiveFunction;
import io.onclave.nsga.ii.objectivefunction.SCH_1;
import io.onclave.nsga.ii.objectivefunction.SCH_2;
import io.onclave.nsga.ii.objectivefunction.SCH_4;
import io.onclave.nsga.ii.objectivefunction.SCH_5;
import java.util.ArrayList;
import java.util.List;

/**
 * this is the Configuration file for the algorithm, where all the values are set and the initial
 * configurations are set and run.
 * to change any aspect of the algorithm, this file may be tweaked.
 * 
 * @author  Debabrata Acharya <debabrata.acharya@icloud.com>
 * @version 1.0
 * @since   0.1
 */
public class Configuration {
    
    private static final int POPULATION_SIZE = 200;
    private static final int GENERATIONS = 1000;
    private static final int CHROMOSOME_LENGTH = 52;
    private static final float CROSSOVER_PROBABILITY = 0.7f;
    private static final float MUTATION_PROBABILITY = 0.03f;
    private static List<IObjectiveFunction> objectives = null;
    
    //FOr scalability and avaialability
    private static final int NO_OF_CONTAINERS = 16;
    private static final float NETWORK_BANDWIDTH = 1.25f; //MBs-1 speed of the network
    private static final float PACKET_SIZE = 0.064f;//The packet size 
    private static final int PROCESS_COMPLEXITY = 100; //Number of MB that processor process at a time
    
    
    //Provisioning Times
    private static final int CONTAINER_1 = 9;
    private static final int CONTAINER_2 = 9;
    private static final int CONTAINER_4 = 10;
    private static final int CONTAINER_8 = 12;
    private static final int CONTAINER_16 = 16;
    private static final int CONTAINER_32 = 27;
    private static final int CONTAINER_64 = 46;
    private static final int CONTAINER_128 = 65;
    private static final int CONTAINER_256 = 111;
    
    public static final double ACTUAL_MIN = 0;
    //public static final double ACTUAL_MAX = Math.pow(2, CHROMOSOME_LENGTH+1);
    public static final double ACTUAL_MAX = 20 *(CHROMOSOME_LENGTH+1);
    public static final double NORMALIZED_MIN = 0;
    public static final double NORMALIZED_MAX = 2;
    public static final String DEFAULT_X_AXIS_TITLE = "x-axis";
    public static final String DEFAULT_Y_AXIS_TITLE = "y-axis";
    
    public static final String writeLocation = "C:\\NSGAIIData\\datExtracted.txt";
    public static final String readLocation = "C:\\NSGAIIData\\ClusteringInformation.txt";


    public static int getPOPULATION_SIZE() {
        return POPULATION_SIZE;
    }

    public static int getGENERATIONS() {
        return GENERATIONS;
    }

    public static int getCHROMOSOME_LENGTH() {
        return CHROMOSOME_LENGTH;
    }
    
    /**
     * this method sets the objective functions over which the algorithm is to operate.
     * it is a list of IObjectionFunction objects.
     */
    public static void buildObjectives(String fileLocation) {
        
        List<IObjectiveFunction> newObjectives = new ArrayList<>();
        
        newObjectives.add(new SCH_1(fileLocation));
        newObjectives.add(new SCH_2(fileLocation));
        newObjectives.add(new SCH_4(fileLocation));
        newObjectives.add(new SCH_5(fileLocation));
        
        setObjectives(newObjectives);
    }

    public static List<IObjectiveFunction> getObjectives() {
        return objectives;
    }

    public static void setObjectives(List<IObjectiveFunction> objectives) {
        Configuration.objectives = objectives;
    }

    public static float getMUTATION_PROBABILITY() {
        return MUTATION_PROBABILITY;
    }

    public static float getCROSSOVER_PROBABILITY() {
        return CROSSOVER_PROBABILITY;
    }
    
    
    public static int getNO_OF_CONTAINERS() {
        return NO_OF_CONTAINERS;
    }
    
    public static float getNETWORK_BANDWIDTH() {
        return NETWORK_BANDWIDTH;
    }
    
    public static float getPACKET_SIZE() {
        return PACKET_SIZE;
    }
    
    public static float getPROCESS_COMPLEXITY() {
        return PROCESS_COMPLEXITY;
    }
    
    
     public static int getCONTAINTER_PROVISION(int number) {
         if(number == 1){
             return CONTAINER_1;
         }else if(number == 2){
             return CONTAINER_2;
         }else if(number == 4){
             return CONTAINER_4;
         }else if(number == 8){
             return CONTAINER_8;
         }else if(number == 16){
             return CONTAINER_16;
         }else if(number == 32){
             return CONTAINER_32;
         }else if(number == 64){
             return CONTAINER_64;
         }else if(number == 128){
             return CONTAINER_128;
         }else{
             return CONTAINER_256;
         }
    }
    
    
    
    public static String getXaxisTitle() {
        return getObjectives().size() > 2 ? DEFAULT_X_AXIS_TITLE : getObjectives().get(0).getAxisTitle();
    }
    
    public static String getYaxisTitle() {
        return getObjectives().size() > 2 ? DEFAULT_Y_AXIS_TITLE : getObjectives().get(1).getAxisTitle();
    }
}
