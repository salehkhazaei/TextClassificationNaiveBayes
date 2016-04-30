/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgmproject1;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Saleh
 */
public class Feature {

    String word;
    HashMap<Class, Double> p = new HashMap<>();

    public Feature(String word) {
        this.word = word;
    }

    public void learn(ArrayList<Class> c) {
        for (int j = 0; j < c.size(); j++) {
            Integer count = c.get(j).getBasicFeatures().get(word);
            if (count == null) {
                count = 0;
            }
            p.put(c.get(j), (double) count / c.get(j).getTrainingDocs().size());
        }
    }
    
}
