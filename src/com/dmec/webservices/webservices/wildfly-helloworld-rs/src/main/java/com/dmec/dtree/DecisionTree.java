package com.dmec.dtree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class DecisionTree {

    private Node root = null;
    private Node curNode = null;

    public DecisionTree() {
        // Empty constructor to create blank tree
    }

    /**
     * Initialize dtree from model file
     *
     * @param modelFilePath
     */
    public DecisionTree(String modelFilePath) throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(modelFilePath));

            String line;
            
            // Get root
            if ((line = br.readLine()) != null) {
                System.out.println("Adding root having value: " + line.split(",")[0]);
                this.root = new Node(line.split(",")[0], false);
            }
            
            this.curNode = this.root;
            
            while ((line = br.readLine()) != null) {
                String[] lineArr = line.split(",");
                if (lineArr[2].equals("true")) {
                    this.curNode.addChild(lineArr[1], lineArr[0], true, false);
                } else if (lineArr[2].equals("false")) {
                    this.curNode.addChild(lineArr[1], lineArr[0], true, true);
                    this.curNode = this.curNode.getChildren().get(lineArr[1]);
                } else {
                    System.err.println("Bad model file");
                    System.exit(1);
                }
            }
            
        } catch (IOException e) {
            System.err.println("Failed while inserting data into dataSet object");
            System.exit(1);
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }

    public DataSet classify(DataSet testSet, String className) {
        Node cur = this.root;

        // Classify each row in given data set
        for (HashMap<String, Object> row : testSet.getDataSet()) {
            row.put(className, null);

            while (row.get(className) == null) {

                // Check if the branch value the row will follow leads to a leaf node
                Node nextNode = cur.getChildren().get(row.get(cur.getNodeValue())); // branching on the row value got us to this node
                if (nextNode.isLeaf()) { // Branch lead to a leaf node. Classify the row

                    if (nextNode.getNodeValue() == null) { // Indecisive leaf node
                        row.put(className, "?   ");
                    } else {
                        row.put(className, nextNode.getNodeValue());
                    }

                } else { // nextNode is not a leaf node, follow branch and continue
                    cur = nextNode;
                }

            }

        }

        return testSet;
    }

    public void addNode(String attrName, boolean isClassifier) {
        Node newNode = new Node(attrName, isClassifier);

        // if curNode is null then root will be null so ignore this
        // if curNode.curBranch is not null then a branch has been taken and it leads to a null node
        // assign the branch taken to a new node having the given attrName
        if (this.curNode != null && this.curNode.getCurBranch() != null) {
            System.out.println("Assigning the branch a new node having value " + attrName);
            this.curNode.getChildren().put(this.curNode.getCurBranch(), newNode);
        }

        this.curNode = newNode;

        if (root == null) {
            System.out.println("New root having value " + attrName);
            this.root = this.curNode;
        }
    }

    public Node getCurNode() {
        return this.curNode;
    }

    public Node getRoot() {
        return this.root;
    }
}