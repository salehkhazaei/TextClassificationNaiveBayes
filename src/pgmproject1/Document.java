/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgmproject1;

import java.util.HashMap;

/**
 *
 * @author Saleh
 */
public class Document {

    public String path;
    public String strFile;
    public HashMap<String, Integer> occurance;

    public Document(String path, String strFile, HashMap<String, Integer> occurance) {
        this.path = path;
        this.strFile = strFile;
        this.occurance = occurance;
    }
}
