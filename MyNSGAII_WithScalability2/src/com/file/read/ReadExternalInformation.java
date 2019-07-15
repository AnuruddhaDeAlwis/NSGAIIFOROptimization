/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.file.read;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author N9572791
 */

//This class is created to give the external inputs based on our findings such as
// whether the business objects are exclusively contained or inclusively contained
public class ReadExternalInformation {
    
    
    //This is to read the text file which contains the infromation that is needed for the processing
    public static String readTextFile(String fileLocation){
        String allInformation = "";
        try (BufferedReader br = new BufferedReader(new FileReader(fileLocation))) {

			String sCurrentLine;

			while ((sCurrentLine = br.readLine()) != null) {
				allInformation = allInformation + sCurrentLine;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
        
        return allInformation;
    }
    
}
