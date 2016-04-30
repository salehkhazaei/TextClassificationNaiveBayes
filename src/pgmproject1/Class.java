/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgmproject1;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import static pgmproject1.PGMProject1.readFile;

/**
 *
 * @author Saleh
 */
public class Class {

    private String name;
    private File folder;
    public int index;
    private int training_count = 700;
    private ArrayList<Document> trainingDocs = new ArrayList();
    private ArrayList<Document> testDocs = new ArrayList();

    private HashMap<String, Integer> basicFeatures;
    public HashMap<String, Integer> basicOccurance;

    public Class(File folder) {
        name = folder.getName();
        this.folder = folder;
        loadDocuments();
        extractBasicFeature();
    }

    public Class(File folder, int train) {
        training_count = train;
        name = folder.getName();
        this.folder = folder;
        loadDocuments();
        extractBasicFeature();
    }

    public String getName() {
        return name;
    }

    public File getFolder() {
        return folder;
    }

    public HashMap<String, Integer> getBasicFeatures() {
        return basicFeatures;
    }

    public ArrayList<Document> getTestDocs() {
        return testDocs;
    }

    public ArrayList<Document> getTrainingDocs() {
        return trainingDocs;
    }

    public void loadDocuments() {
        int i = 0;
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
            } else {
                try {
                    String str = readFile(file);
                    str = str.replaceAll("[^A-Za-z]+", " ");
                    HashMap<String, Integer> occurrences = new HashMap();

                    String[] arr = str.split(" ");

                    for (String word : arr) {
                        if (word.trim().length() == 0) {
                            continue;
                        }
                        String w = word.toLowerCase();
                        Stemmer s = new Stemmer();
                        w = s.stripAffixes(w);
                        if (w.length() < 2) {
                            continue;
                        }

                        Integer oldCount = occurrences.get(w);
                        if (oldCount == null) {
                            oldCount = 0;
                        }
                        occurrences.put(w, oldCount + 1);
                    }

                    if (i < training_count) {
                        trainingDocs.add(new Document(file.getPath(), str, occurrences));
                    } else if (i < 700) {
                    } else {
                        testDocs.add(new Document(file.getPath(), str, occurrences));
                    }
                    i++;
                } catch (IOException ex) {
                    Logger.getLogger(PGMProject1.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void extractBasicFeature() {
        HashMap<String, Integer> occurrences = new HashMap();
        HashMap<String, Integer> occurrence2 = new HashMap();
        for (Document doc : trainingDocs) {
            for (String s : doc.occurance.keySet()) {
                Integer oldCount = occurrences.get(s);
                if (oldCount == null) {
                    oldCount = 0;
                }
                occurrences.put(s, oldCount + 1);

                Integer oldCount2 = occurrence2.get(s);
                if (oldCount2 == null) {
                    oldCount2 = 0;
                }
                occurrence2.put(s, oldCount2 + doc.occurance.get(s));
            }
        }
        basicFeatures = occurrences;
        basicOccurance = occurrence2;
    }

    public double p(Feature f1, Feature f2) {
        int c = 0;
        for (Document d : trainingDocs) {
            if (d.occurance.containsKey(f1.word) && d.occurance.containsKey(f2.word)) {
                c++;
            }
        }
        return (double) c / trainingDocs.size();
    }
}
