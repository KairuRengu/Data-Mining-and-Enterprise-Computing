package com.dmec.dtree;

public class Dmec {
    
    static int numClusters = 3;
    static double mins = 0.5;
    
    public static void main(String[] args) {
        
        String trainingFilePath = null;
        String testingFilePath;
        String outputFilePath;
        
        int index_a = -1;
        int index_t = -1;
        int index_T = -1;
        int index_o = -1;
        
        for (int i = 0; i < args.length; i += 2) {
        	//System.out.println("args[" + i + "] = " + args[i]);
            switch(args[i]) {
                case "-a":
                    index_a = i;
                    break;
                case "-t":
                    index_t = i;
                    break;
                case "-T":
                    index_T = i;
                    break;
                case "-o":
                    index_o = i;
                    break;
                default:
                    printUsage();
                    System.exit(1);
                    break;
            }
        }
        
        
        // debug, print arguments
        //System.out.println(Arrays.toString(args));
        /*System.out.println("index_a: " + index_a);
        System.out.println("index_t: " + index_t);
        System.out.println("index_T: " + index_T);
        System.out.println("index_o: " + index_o);*/
        
        // if no alg or test file or output path specified then invalid. if alg is 1 or 2 and there is no training file then invalid
        if (index_a == -1 || index_T == -1 || index_o == -1 || (index_t == -1 && (args[index_a+1].equals("1") || args[index_a+1].equals("2")))) {
            printUsage();
            System.exit(1);
        }
        
        // Make use of training file
        if (index_t != 0) {
            trainingFilePath = args[index_t + 1];
        } else {
            System.err.println("No training file provided");
            System.exit(1);
        }
        
        // Make use of testing file
        testingFilePath = args[index_T + 1];
        
        // Make use of output file
        outputFilePath = args [index_o + 1];
        
        // Call specified algorithm
        // 1 - CART, 2 - ID3,  3 - k-mean, 4 - Apriori
        switch (Integer.parseInt(args[index_a + 1])) {

            case 1:
                doCART(trainingFilePath, testingFilePath, outputFilePath);
                break;

            case 2:
                doID3(trainingFilePath, testingFilePath, outputFilePath);
                break;

            case 3:
                //doKMeans(testingFilePath);
                break;

            case 4:
                //doApriori(testingFilePath);
                break;

            default:
                System.out.println("Invalid algorithm number.");
                System.exit(1);
                break;
        }
        
        
        
    }
    
    static void printUsage() {
        System.out.println("Usage: -a <alg-#> -t <training-file-path> -T <testing-file-path> -o <output-model-file-path>");
    }
    
    /**
     * Expects file to by comma (,) delimited
     * 
     * @param testingFilePath
     */
   /* public static void doKMeans(String testingFilePath) {
        KMeans km;
        try {
            km = new KMeans(testingFilePath, numClusters);
            km.cluster();
        } catch (IOException ex) {
            System.err.println("File not found");
        }
    }*/
    
    /**
     * Expects file to be space ( ) delimited
     * 
     * @param testingFilePath
     */
    /*public static void doApriori(String testingFilePath) {
        Apriori a;
        try {
            a = new Apriori(testingFilePath, mins);
            a.associationMine();
        } catch (IOException ex) {
            System.err.println("File not found");
        }
    }*/
    
    /**
     * Expects comma delimited file with first row being classifying attribute and second being list of attributes
     * 
     * @param trainingFilePath
     * @param testingFilePath
     */
    public static void doCART(String trainingFilePath, String testingFilePath, String outputFilePath) {
    	CARTandID3 cart = null;
        
		try {
			DataSet trainingDataSet = new DataSet(trainingFilePath, ",", true);
			cart = new CARTandID3(trainingDataSet, "CART", outputFilePath);
            
		} catch (Exception ex) {
			System.err.println("Failed creating model with training set");
            System.exit(1);
		}
        
		try {
			DataSet testDataSet = new DataSet(testingFilePath, ",", false);
	        DataSet classifiedDataSet1 = cart.classify(testDataSet);
	        classifiedDataSet1.printDataSet();
		} catch (Exception ex) {
			System.err.println("Failed classifying test set");
            System.exit(1);
		}
    }
    
    public static void doID3(String trainingFilePath, String testingFilePath, String outputFilePath) {
    	CARTandID3 id3 = null;
        
		try {
			DataSet trainingDataSet = new DataSet(trainingFilePath, ",", true);
			id3 = new CARTandID3(trainingDataSet, "ID3", outputFilePath);
            
		} catch (Exception ex) {
			System.err.println("Failed creating model with training set");
            System.exit(1);
		}
        
		try {
			DataSet testDataSet = new DataSet(testingFilePath, ",", false);
	        DataSet classifiedDataSet1 = id3.classify(testDataSet);
	        classifiedDataSet1.printDataSet();
		} catch (Exception ex) {
			System.err.println("Failed classifying test set");
            System.exit(1);
		}
    }
    
    public static void doCARTPrediction(String testingFile) {
    	
    }
    
    public static void doID3Prediction(String testingFile) {
	    	
    }
	
	/*public static void doKMeansPrediction(String testingFile) {
		
	}
	
	public static void doAprioriPrediction(String testingFile) {
		
	}*/
    
}
