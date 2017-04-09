package com.dmec.dtree;

import java.util.HashMap;

public class DecisionTree {
    
    private Node root = null;
    private Node curNode = null;
    
    public DecisionTree() {
        
    }
    
    public DataSet classify(DataSet testSet, String className) {
        //testSet.addAttrName(className); // Add class column to given test dataSet
        
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
                    } else{
                        row.put(className, nextNode.getNodeValue());
                    }
                    
                }
                else { // nextNode is not a leaf node, follow branch and continue
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
