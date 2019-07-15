/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.file.read;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author n9572791
 */
public class WriteToExternalFile {
    
    public static void writeTextFile(String fileLocation,String otherDetails){
//        String allInformation = "";
//        try (BufferedReader br = new BufferedReader(new FileReader(fileLocation))) {
//
//			String sCurrentLine;
//
//			while ((sCurrentLine = br.readLine()) != null) {
//				allInformation = allInformation + sCurrentLine;
//			}
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//        
//       
//        
//        allInformation = allInformation + otherDetails;
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(fileLocation,true));
            writer.write(otherDetails);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(WriteToExternalFile.class.getName()).log(Level.SEVERE, null, ex);
            
        }
        
        
    }
    
}
