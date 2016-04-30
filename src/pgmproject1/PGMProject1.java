/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgmproject1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;

/**
 *
 * @author Saleh
 */
public class PGMProject1 {

    public static class StrOcu {

        public String word;
        public int classOccurance;
        public int occurance;
        public int occuranceSum;
    }

    public static String readFile(File file)
            throws IOException {
        Scanner scan = new Scanner(file);
        String str = "";
        boolean save = false;
        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            if (line.isEmpty()) {
                save = true;
            }
            if (line.startsWith("Archive-name")) {
                save = false;
                continue;
            }
            if (save) {
                str += line + "\n";
            }
        }
        return str;
    }

    public static ArrayList<Class> categories;
    public static double max_res = 0;
    public static int max_i = 0;
    public static int max_j = 0;
    public static int counter = 0;

    public static void main(String[] args) throws FileNotFoundException, IOException {
        for (int i = 100; i <= 700; i += 100) {
            File mainfolder = new File("F:\\20_newsgroups\\20_newsgroups");
            categories = new ArrayList<>();

            System.out.println("Initialize");
            int index = 0;
            for (File folder : mainfolder.listFiles()) {
                Class cat = new Class(folder,i);
                cat.index = index;
                index++;
                categories.add(cat);
            }

            System.setOut(new PrintStream(new OutputStream() {
                @Override
                public void write(int arg0) throws IOException {
                }
            }));

            System.err.println("start");

            try {
                FileOutputStream fos = new FileOutputStream("hyperparameters.csv");
                fos.write(("feature per class, feature max repitition, result\n").getBytes());

                int ii = 100;
                int jj = 5;
                double res = doCategorize(ii, jj);
                for (int k = 0; k < categories.size(); k++) {
                    categories.get(k).extractBasicFeature();
                }
                System.err.println("did categorize with (" + ii + ", " + jj + ") result was " + res);
                fos.write((ii + "," + jj + "," + res + "\n").getBytes());
                if (res > max_res) {
                    max_res = res;
                    max_i = ii;
                    max_j = jj;
                }
                counter++;
                fos.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(PGMProject1.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(PGMProject1.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.err.println("--------------------------");
            System.err.println("max_res: " + max_res);
            System.err.println("max_i: " + max_i);
            System.err.println("max_j: " + max_j);
        }
    }

    public static double doCategorize(int feature_per_class, int feature_maxrep) throws FileNotFoundException, IOException {

        FileOutputStream fos2 = new FileOutputStream(new File("feature_class.txt"));
        System.out.println("Find good features");
        HashMap<String, Integer> occurrences = new HashMap();
        for (Class cat : categories) {
            for (String word : cat.getBasicFeatures().keySet()) {
                Integer oldCount = occurrences.get(word);
                if (oldCount == null) {
                    oldCount = 0;
                }
                occurrences.put(word, oldCount + 1);
            }
        }

        ArrayList<String> shouldRemove = new ArrayList<>();
        for (String word : occurrences.keySet()) {
            if (occurrences.get(word) > feature_maxrep) {
                shouldRemove.add(word);
            }
        }

        System.out.println("Set features");
        ArrayList<Feature> features = new ArrayList<>();
        for (Class cc : categories) {
            Class cat = cc;
            for (String word : shouldRemove) {
                cat.getBasicFeatures().remove(word);
            }

            ArrayList<StrOcu> ar = new ArrayList();
            for (String word : cat.getBasicFeatures().keySet()) {
                StrOcu so = new StrOcu();
                so.word = word;
                so.occurance = cat.getBasicFeatures().get(word);
                so.occuranceSum = cat.basicOccurance.get(word);
                so.classOccurance = 0;
                ar.add(so);
            }
            for (StrOcu s : ar) {
                for (Class cw : categories) {
                    if (cw.basicOccurance.containsKey(s.word)) {
                        s.classOccurance++;
                    }
                }
            }
            fos2.write(("*********************************************************\n" + cc.getName() + " [" + ar.size() + "]" + "\n").getBytes());
            ar.sort(new Comparator<StrOcu>() {
                @Override
                public int compare(StrOcu o1, StrOcu o2) {
                    return o2.occuranceSum - o1.occuranceSum;
                }
            });
            for (StrOcu so : ar) {
                fos2.write((so.word + "\t" + so.occurance + "\t" + so.occuranceSum + "\t" + so.classOccurance + "\n").getBytes());
            }
            int c2 = 0;
            for (StrOcu u : ar) {
                Feature f = new Feature(u.word);
                f.learn(categories);

                synchronized (features) {
                    features.add(f);
                }
                c2++;
                if (c2 >= feature_per_class) {
                    break;
                }
            }
        }

        for (Class c : categories) {
            Feature f1 = null;
            Feature f2 = null;
            for (Feature f : features) {
                double p = f.p.get(c);
                if (p > 0.05) {
                    if (f1 == null) {
                        f1 = f;
                    } else if (f2 == null) {
                        f2 = f;
                    } else {
                        break;
                    }
                }
            }
            if (f1 != null && f2 != null) {
//                System.err.println("Class [" + c.getName() + "] F1[" + f1.word + " " + f1.p.get(c) + "] F2[" + f2.word + " " + f2.p.get(c) + "] [" + f1.p.get(c) * f2.p.get(c) + " " + c.p(f1, f2) + "]");
            }
        }
        fos2.close();

        try {
            FileOutputStream fos = new FileOutputStream("features.txt");
            {
                fos.write(("word").getBytes());
                int spaces = 21;
                String strspc = "";
                for (int k = 0; k < spaces; k++) {
                    strspc += " ";
                }
                fos.write((strspc).getBytes());
            }
            for (Class c : categories) {
                fos.write((c.getName()).getBytes());
                int spaces = 25 - c.getName().length();
                String strspc = "";
                for (int k = 0; k < spaces; k++) {
                    strspc += " ";
                }
                fos.write((strspc).getBytes());
            }
            fos.write(("\n").getBytes());

            for (Feature f : features) {
                {
                    fos.write((f.word).getBytes());
                    int spaces = 25 - f.word.length();
                    String strspc = "";
                    for (int k = 0; k < spaces; k++) {
                        strspc += " ";
                    }
                    fos.write((strspc).getBytes());
                }
                for (Class c : f.p.keySet()) {
                    int spaces = 25 - (f.p.get(c) + "").length();
                    String strspc = "";
                    for (int k = 0; k < spaces; k++) {
                        strspc += " ";
                    }
                    fos.write((f.p.get(c) + strspc).getBytes());
                }
                fos.write(("\n").getBytes());
            }
            fos.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PGMProject1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PGMProject1.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Now, let the battle begin :D");
        System.out.println("Initialize test documents");
        int right_count = 0;
        ArrayList<Pair<Document, Class>> testDocs = new ArrayList<>();

        for (Class c : categories) {
            for (int i = 0; i < c.getTestDocs().size(); i++) {
                testDocs.add(new Pair<>(c.getTestDocs().get(i), c));
            }
        }

        System.out.println("Start testing ...");

        double max_p = 0;
        double min_p = 99999999;
        double sum_p = 0;
        double ave_p;
        int zero_count = 0;

        int confusion_matrix[][] = new int[20][20];

        for (int i = 0; i < testDocs.size(); i++) {
            Pair<Document, Class> doc = testDocs.get(i);

            double max_prob = 0;
            Class max_class = null;

            for (Class c : categories) {
                double prob = 1;

                for (int j = 0; j < features.size(); j++) {
                    Integer ii = doc.getKey().occurance.get(features.get(j).word);
                    double pp = 0;
                    if (ii != null) {
                        pp = features.get(j).p.get(c);
                    }
                    for (int k = 0; k < (ii == null ? 0 : ii); k++) {
                        prob *= Math.exp(pp);
                    }
                }
                if (prob > max_prob) {
                    max_class = c;
                    max_prob = prob;
                }
            }

            if (max_class == null) {
                System.out.println("WHAT?!");
            }
            confusion_matrix[doc.getValue().index][max_class.index]++;
            if (max_class == doc.getValue()) {
                right_count++;
            } else {
                right_count = right_count;
            }
            if (max_prob > max_p) {
                max_p = max_prob;
            }
            if (min_p > max_prob) {
                min_p = max_prob;
            }
            sum_p += max_prob;

            if (max_prob == 1.0) {
                zero_count++;
                //System.out.println(doc.getKey().path);
            }
        }
        ave_p = sum_p / testDocs.size();

        double res = ((double) right_count / (double) (testDocs.size()));
        System.out.println("test result: " + res);
        System.out.println("min p: " + min_p);
        System.out.println("max p: " + max_p);
        System.out.println("ave p: " + ave_p);
        System.out.println("no of files: " + testDocs.size());
        System.out.println(":|: " + zero_count);

        FileOutputStream fos = new FileOutputStream(new File("confusion_matrix.csv"));
        for (int j = 0; j < 20; j++) {
            fos.write((categories.get(j).getName() + (j != 19 ? "," : "")).getBytes());
        }
        fos.write("\n".getBytes());
        for (int i = 0; i < 20; i++) {
            fos.write((categories.get(i).getName() + ",").getBytes());
            for (int j = 0; j < 20; j++) {
                fos.write((confusion_matrix[i][j] + (j != 19 ? "," : "")).getBytes());
            }
            fos.write("\n".getBytes());
        }
        fos.close();
        return res;
    }
}
