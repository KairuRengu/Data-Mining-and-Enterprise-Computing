package com.dmec.dtree;

public class Dmec {

    static int numClusters = 3;
    static double mins = 0.5;
    static CARTandID3 cart = null;

    public static void main(String[] args) {

        String trainingFilePath = "C:\\Users\\The Boss\\Documents\\GitHub\\Data-Mining-and-Enterprise-Computing\\test\\data2_train.txt"; // THIS WILL BE SERVER SIDE
        String testingFilePath = "C:\\Users\\The Boss\\Documents\\GitHub\\Data-Mining-and-Enterprise-Computing\\test\\data2_test.txt";
        String outputFilePath = "output";

        //doCART(trainingFilePath, outputFilePath);

        doCARTPrediction(testingFilePath, outputFilePath);
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
