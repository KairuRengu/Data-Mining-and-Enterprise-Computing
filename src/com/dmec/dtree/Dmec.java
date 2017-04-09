package com.dmec.dtree;

public class Dmec {

    static int numClusters = 3;
    static double mins = 0.5;
    static CARTandID3 cart = null;

    public static void main(String[] args) {

        String trainingFilePath = "C:\\Users\\The Boss\\Documents\\GitHub\\Data-Mining-and-Enterprise-Computing\\test\\data2.txt"; // THIS WILL BE SERVER SIDE
        String testingFilePath = "";
        String outputFilePath = "output";

        doCART(trainingFilePath, outputFilePath);
        
        //doCARTPrediction(testingFilePath);
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
            DataSet trainingDataSet = new DataSet(trainingFilePath, ",", true);
            cart = new CARTandID3(trainingDataSet, "CART", outputFilePath);

        } catch (Exception ex) {
            System.err.println("Failed creating model with training set");
            System.exit(1);
        }
    }

    public static void doCARTPrediction(String testingFilePath) {
        try {
            DataSet testDataSet = new DataSet(testingFilePath, ",", false);
            DataSet classifiedDataSet1 = cart.classify(testDataSet);
            classifiedDataSet1.printDataSet();
        } catch (Exception ex) {
            System.err.println("Failed classifying test set");
            System.exit(1);
        }
    }

}
