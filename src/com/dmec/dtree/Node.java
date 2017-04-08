package com.dmec.dtree;

import java.util.HashMap;
import java.util.Map;


public class Node {
    
    private final String value;
    private final HashMap<Object, Node> children; // <branchValue, node-with-attrValue>
    private Object curBranch = null;
    
    public Node(String attrName) {
        this.value = attrName;
        this.children = new HashMap();
    }
    
    /**
     * 
     * @param attrName - attribute name for new child node
     * @param branchValue - value used to branch to the child node
     */
    public void addChild(Object branchValue, String attrName, boolean takeBranch) {
        this.children.put(branchValue, new Node(attrName));
        //System.out.println("New child has just been added with key " + branchValue);
        if (takeBranch) {
            System.out.println("Taking branch " + branchValue);
            curBranch = branchValue;
        }
    }
    
    public Object getCurBranch() {
        return this.curBranch;
    }
    
    public HashMap<Object, Node> getChildren() {
        return this.children;
    }
    
    public boolean hasChild(Object branchValue) {
        //System.out.println("dTree.hasChild(" + branchValue + ") = " + this.children.containsKey(branchValue));
        //System.out.println(this.children.size());
        //printChildren();
        return this.children.containsKey(branchValue);
    }
    
    public void printChildren() {
        System.out.println("Printing child keys: ");
        for (Map.Entry<Object, Node> entry : this.children.entrySet()) {
            System.out.print(entry.getKey() + ", ");
        }
        System.out.println("\n");
    }
    
    public boolean isLeaf() {
        return children.isEmpty();
    }
    
    public String getNodeValue() {
        return this.value;
    }
    
    public Node getLeftNode() {
    	// Sketchy way to always take the first entry as the left child or null if no children
    	for (Map.Entry<Object, Node> entry : children.entrySet()) { 
    		return entry.getValue();
    	}

    	return null;
    }
    
    public Object getLeftBranchValue() {
    	// Sketchy way to always take the first entry as the left child or null if no children
    	for (Map.Entry<Object, Node> entry : children.entrySet()) { 
    		return entry.getKey();
    	}

    	return null;
    }
    
    public Node getRightNode() {
    	// Sketchy way to always take the second entry as the right child or null if < 2 children
    	int count = 0;
    	for (Map.Entry<Object, Node> entry : children.entrySet()) {
    		if (count == 0) {
    			count ++;
    		} else {
    			return entry.getValue();
    		}
    	}

    	return null;
    }
    
    public Object getRightBranchValue() {
    	// Sketchy way to always take the second entry as the right child or null if < 2 children
    	int count = 0;
    	for (Map.Entry<Object, Node> entry : children.entrySet()) {
    		if (count == 0) {
    			count ++;
    		} else {
    			return entry.getKey();
    		}
    	}

    	return null;
    }
}
