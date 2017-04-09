package com.dmec.dtree;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;

public class Dmec {

    static CARTandID3 cart = null;

    public static void main(String[] args) {

        String trainingFilePath = "C:\\Users\\The Boss\\Documents\\GitHub\\Data-Mining-and-Enterprise-Computing\\test\\data2_train.txt"; // THIS WILL BE SERVER SIDE
        String testingFilePath = "C:\\Users\\The Boss\\Documents\\GitHub\\Data-Mining-and-Enterprise-Computing\\test\\data2_test.txt";
        String outputFilePath = "output";
        
        genTrainingFile("Raptors", "Bulls");
        
        //doCART(trainingFilePath, outputFilePath);

        //doCARTPrediction(testingFilePath, outputFilePath);
    }
    
    public static void genTrainingFile(String team1, String team2) {
        File[] files1 = getAllFiles(team1);
        File[] files2 = getAllFiles(team2);
        
        
    }
    
    /**
     * 
     * @param gameFiles
     * @return hashmap of data for 5 best players
     */
    private static HashMap<String,String>[] getFiveBestPlayers(File[] gameFiles) {
        HashMap<String,Object>[] bestPlayers = new HashMap[5];
        
        
        
        return bestPlayers;
    }
    
    /**
     * 
     * @param player
     * @return player score based on weighted statistics of past games
     */
    private static double scorePlayer(HashMap<String,String> player) {
        double score = 0;
        
        //name,playerNumber,minutesPlayed,points,+/-,fieldGoalPercent,rebounds,team
        score += Double.parseDouble(player.get("points"));
        score += Double.parseDouble(player.get("+/-"));
        score += Double.parseDouble(player.get("rebounds"));
        score *= Double.parseDouble(player.get("fieldGoalPercent"));
        score /= minsToSeconds(player.get("minutesPlayed"));
        System.out.println("Score for " + player.get("name") + ": " + score);
        return score;
    }
    
    /**
     * converts <mm:ss> string to integer of seconds
     * @param minsString
     * @return 
     */
    private static double minsToSeconds(String minsString) {
        return Double.parseDouble(minsString.split(":")[1]) + 60 * Integer.parseInt(minsString.split(":")[0]);
    }
    
    /**
     * 
     * @param teamName
     * @return list of paths to files where the given team played
     */
    private static File[] getAllFiles(String teamName) {
        File dir = new File("C:\\Users\\The Boss\\Documents\\GitHub\\Data-Mining-and-Enterprise-Computing\\test\\scraped");
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
     * Expects comma delimited file with first row being classifying attribute
     * and second being list of attributes
     *
     * @param trainingFilePath
     * @param outputFilePath
     */
    public static void doCART(String trainingFilePath, String outputFilePath) {
        try {
            DataSet trainingDataSet = new DataSet(trainingFilePath, ",");
            cart = new CARTandID3(trainingDataSet, "CART", outputFilePath);

        } catch (Exception ex) {
            System.err.println("Failed creating model with training set");
            System.exit(1);
        }
    }

    public static void doCARTPrediction(String testingFilePath, String modelFilePath) {
        try {
            DataSet testDataSet = new DataSet(testingFilePath, ","); // Create data set using test file
            DecisionTree modelFileTree = new DecisionTree(modelFilePath); // Create dtree from modelFile
            DataSet classifiedDataSet1 = modelFileTree.classify(testDataSet, testDataSet.getClassifyingAttr()); // Do classification
            classifiedDataSet1.printDataSet();
        } catch (Exception ex) {
            System.err.println("Failed classifying test set");
            System.exit(1);
        }
    }

}
