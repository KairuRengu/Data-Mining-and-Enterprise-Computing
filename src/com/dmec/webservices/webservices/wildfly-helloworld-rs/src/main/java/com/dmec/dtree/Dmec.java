package com.dmec.dtree;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Dmec {

    static CARTandID3 cart = null;

    public static void main(String[] args) {

        //String trainingFilePath = "C:\\Users\\The Boss\\Documents\\GitHub\\Data-Mining-and-Enterprise-Computing\\test\\data_train.txt"; // THIS WILL BE SERVER SIDE
        String testingFilePath = "C:\\Users\\The Boss\\Documents\\GitHub\\Data-Mining-and-Enterprise-Computing\\test\\data_train.txt";
        
        
        Dmec dmec = new Dmec();
        dmec.doCART("Brooklyn Nets", "Cleveland Cavaliers");

        //dmec.doCARTPrediction(testingFilePath);
        
        System.out.println(dmec.doCARTPrediction(testingFilePath));
    }
    
    public double doCARTPrediction(String testingFile) {
        Double winPercentA = 100.0;
        int aGreaterThanBCount = 0;
        int aEqualToBCount = 0;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(testingFile));

            br.readLine();
            br.readLine();
            String[] attrsA = br.readLine().split(",");
            String[] attrsB = br.readLine().split(",");
            System.out.println(Arrays.toString(attrsA));
            for (int i = 0; i < attrsA.length-1; i ++) {
                if (Double.parseDouble(attrsA[i]) > Double.parseDouble(attrsB[i])) { // A > B
                    aGreaterThanBCount ++;
                }else if (Double.parseDouble(attrsA[i]) == Double.parseDouble(attrsB[i])) { // A = B
                    aEqualToBCount ++;
                }
            }

            if (aGreaterThanBCount == 4) {
                winPercentA = 99.0;
            } else if (aGreaterThanBCount == 3) {
                if (aEqualToBCount == 1) {
                    winPercentA = 90.0;
                } else {
                    winPercentA = 75.0;
                }
            } else if (aGreaterThanBCount == 2) {
                if (aEqualToBCount == 2) {
                    winPercentA = 75.0;
                } else if (aEqualToBCount == 1) {
                    winPercentA = 65.0;
                } else {
                    winPercentA = 51.0;
                }
            } else if (aGreaterThanBCount == 1) {
                if (aEqualToBCount == 3) {
                    winPercentA = 60.0;
                } else if (aEqualToBCount == 2) {
                    winPercentA = 49.0;
                } else if (aEqualToBCount == 1) {
                    winPercentA = 35.0;
                } else {
                    winPercentA = 25.0;
                }
            } else if (aGreaterThanBCount == 0) {
                if (aEqualToBCount == 4) {
                    winPercentA = 49.0;
                } else if (aEqualToBCount == 3) {
                    winPercentA = 40.0;
                } else if (aEqualToBCount == 2) {
                    winPercentA = 25.0;
                } else if (aEqualToBCount == 1) {
                    winPercentA = 15.0;
                } else {
                    winPercentA = 1.00;
                }
            }

        } catch (IOException e) {
            System.err.println("Failed while classifying");
            System.exit(1);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ex) {
                    Logger.getLogger(Dmec.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        return winPercentA;
    }
    
    public void genTrainingFile(String team1, String team2) {
        
        HashMap<String, Object> team1Score = null;
        HashMap<String, Object> team2Score = null;
        team1Score = scoreTeam(getAllFiles(team1), team1);
        team2Score = scoreTeam(getAllFiles(team2), team2);
        
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("C:\\Users\\norr5300\\Downloads\\data_train.txt"), "utf-8"));
            writer.write("win\n");
            writer.write("points,+/-,rebounds,fieldGoalPercent,win\n");
            writer.write(team1Score.get("points") + "," + team1Score.get("+/-") + "," + team1Score.get("rebounds") + "," + team1Score.get("fieldGoalPercent") + "," + team1Score.get("win") + "\n");
            writer.write(team2Score.get("points") + "," + team2Score.get("+/-") + "," + team2Score.get("rebounds") + "," + team2Score.get("fieldGoalPercent") + "," + team2Score.get("win"));
            
        } catch (IOException e) {
            System.err.println("Failed to create output model file");
            System.exit(1);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ex) {
                    Logger.getLogger(Dmec.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
    }
    
    /**
     * takes average of all stats from all scraped games. e.g. HashMap.get("points") will return average points scored for
     * team over all scraped games
     * @param player
     * @return team score based on weighted statistics of past games
     */
    private HashMap<String, Object> scoreTeam(File[] statsFiles, String team) {
        HashMap<String, Object> overallScore = new HashMap();
        for (int i = 0; i < statsFiles.length; i ++) {
            
            if (i == 0) {
                overallScore.put("points", 0.0);
                overallScore.put("+/-", 0.0);
                overallScore.put("rebounds", 0.0);
                overallScore.put("fieldGoalPercent", 0.0);
            }
            
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(statsFiles[i]));

                String line;
                
                // get line of file corresponding to given team and store in line
                while (!((line = br.readLine()).split(",")[0]).equals(team)) {
                    // no op
                }
                
                if (i == 0) {
                    overallScore.put("team", line.split(",")[0]);
                    overallScore.put("win", line.split(",")[5]);
                    
                }
                
                //team,points,+/-,rebounds,fieldGoalPercent
                overallScore.put("points", (Double)overallScore.get("points") + Double.parseDouble(line.split(",")[1])); // points
                overallScore.put("+/-", (Double)overallScore.get("+/-") + Double.parseDouble(line.split(",")[2])); // +/-
                overallScore.put("rebounds", (Double)overallScore.get("rebounds") + Double.parseDouble(line.split(",")[3])); // rebounds
                overallScore.put("fieldGoalPercent", (Double)overallScore.get("fieldGoalPercent") + Double.parseDouble(line.split(",")[4])); // fieldGoalPercent
                

                
            } catch (IOException e) {
                System.err.println("Failed while inserting data into dataSet object");
                System.exit(1);
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException ex) {
                        Logger.getLogger(Dmec.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        
        // take average score to be overall score and return
        overallScore.put("points", (Double)overallScore.get("points") / statsFiles.length); // points
        overallScore.put("+/-", (Double)overallScore.get("+/-") / statsFiles.length); // +/-
        overallScore.put("rebounds", (Double)overallScore.get("rebounds") / statsFiles.length); // rebounds
        overallScore.put("fieldGoalPercent", (Double)overallScore.get("fieldGoalPercent") / statsFiles.length); // fieldGoalPercent
        
        return overallScore;
    }
    
    /**
     * converts <mm:ss> string to integer of seconds
     * @param minsString
     * @return 
     */
    /*private double minsToSeconds(String minsString) {
        return Double.parseDouble(minsString.split(":")[1]) + 60 * Integer.parseInt(minsString.split(":")[0]);
    }*/
    
    /**
     * 
     * @param teamName
     * @return list of paths to files where the given team played
     */
    private File[] getAllFiles(String teamName) {
        File dir = new File("C:\\Users\\norr5300\\Downloads\\box-scores");
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.contains(teamName);
            }
        });

        /*for (File fileName : files) {
            System.out.println(fileName);
        }*/
        return files;
    }

    /**
     * generate model file
     * @param teamA
     * @param teamB 
     */
    public void doCART(String teamA, String teamB) {
        String outputFilePath = "output";
        String trainingFilePath = "C:\\Users\\norr5300\\Downloads\\data_train.txt";
        genTrainingFile(teamA, teamB);
        
        try {
            DataSet trainingDataSet = new DataSet(trainingFilePath, ",");
            cart = new CARTandID3(trainingDataSet, "CART", outputFilePath);

        } catch (Exception ex) {
            System.err.println("Failed creating model with training set");
            System.exit(1);
        }
    }

    /*public void doCARTPrediction(String testingFilePath, String modelFilePath) {
        try {
            DataSet testDataSet = new DataSet(testingFilePath, ","); // Create data set using test file
            DecisionTree modelFileTree = new DecisionTree(modelFilePath); // Create dtree from modelFile
            DataSet classifiedDataSet1 = modelFileTree.classify(testDataSet, testDataSet.getClassifyingAttr()); // Do classification
            classifiedDataSet1.printDataSet();
        } catch (Exception ex) {
            System.err.println("Failed classifying test set");
            System.exit(1);
        }
    }*/

}