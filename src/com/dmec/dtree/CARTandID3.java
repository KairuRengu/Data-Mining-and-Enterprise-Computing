package com.dmec.dtree;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CARTandID3 {

    private final DataSet data;
    private final ArrayList<String> attributeNames;
    private final String className; // The name of the class attribute. E.g. Shape
    private final DecisionTree dTree;
    private final String alg;
    private Writer globalWriter;

    /**
     *
     * @param d - data set to learn from
     * @param outputFilePath - path to file generated
     * @param alg - supports "CART" and "ID3
     */
    public CARTandID3(DataSet d, String alg, String outputFilePath) {
        this.data = d;
        this.attributeNames = this.data.getattributeNames();
        this.className = d.getClassifyingAttr();
        this.dTree = new DecisionTree();
        this.alg = alg;
        try {
            this.globalWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFilePath), "utf-8"));
        } catch (IOException e) {
            System.err.println("Failed to create output model file");
            System.exit(1);
        }
        constructDecisionTree();
    }

    private void constructDecisionTree() {

        while (!this.data.getDataSet().isEmpty()) {
            System.out.println("***");
            HashMap<String, Double> splitValues = new HashMap();

            // HashMap<String, HashMap<Object, HashMap<Object, Integer>>> - <attrName, <attrValue, <class-value, #-occurrences>>>
            HashMap<String, HashMap<Object, HashMap<Object, Integer>>> classDistributionSet = new HashMap();

            // Calculate GINIsplit for each attribute of dataSet
            for (String attrName : this.attributeNames) {
                if (!attrName.equals(this.className)) { // skip the class attribute
                    System.out.println("Finding class distribution for " + attrName);
                    // 1. find class distribution for attr
                    HashMap<Object, HashMap<Object, Integer>> classDist = findClassDist(attrName);
                    printClassDist(classDist);
                    // 2. call GINISplit on the classDist to calculate GINIsplit for the attribute
                    //System.out.println("Adding " + attrName + " : " + GINISplit(classDist) + " to ArrayList");
                    if (this.alg.equals("CART")) {
                        splitValues.put(attrName, GINISplit(classDist, attrName));
                    } else if (this.alg.equals("ID3")) {
                        splitValues.put(attrName, GAINSplit(classDist, attrName));
                    } else {
                        System.err.println("Algorithm requested is not recognized");
                        System.exit(1);
                    }
                    classDistributionSet.put(attrName, classDist);
                }
            }
            // 3. take smallest GINISplit value as node => remove from this.attributeNames

            String nodeAttrName = null;
            if (this.alg.equals("CART")) {
                nodeAttrName = determineMinSplitGINIAttr(splitValues); // Get name of attribute that yielded the smalled GINIsplit value
            } else if (this.alg.equals("ID3")) {
                nodeAttrName = determineMaxSplitGAINAttr(splitValues);
            } else {
                System.err.println("Algorithm requested is not recognized");
                System.exit(1);
            }

            HashMap<Object, HashMap<Object, Integer>> minClassDist = null;
            // Get class distribution for the nodeAttr
            for (Map.Entry<String, HashMap<Object, HashMap<Object, Integer>>> classDist : classDistributionSet.entrySet()) {
                //System.out.println("-Looking for minClassDist(" + nodeAttrName + "): " + classDist.getKey());
                if (classDist.getKey().equals(nodeAttrName)) {
                    //System.out.println("*** found minClassDist: " + classDist.getKey());
                    minClassDist = classDist.getValue();
                    break;
                }
            }

            System.out.println("Adding node to tree: " + nodeAttrName);
            addNodeToDecisionTree(nodeAttrName, minClassDist); // Also removes necessary rows from data set for the next branch
            System.out.println("***\n\n\n");

            if (nodeAttrName == null) {
                break;
            }
        }

        System.out.println("\n\nDecision tree successfully constructed.\n");

        genOutputFile(this.dTree.getRoot(), "null");
        try {
            this.globalWriter.close();
        } catch (IOException e) {
            // no problem
        }
    }

    public DataSet classify(DataSet d) {
        return this.dTree.classify(d, this.className);
    }

    /**
     *
     * @param nodeAttrName - String of attribute name that will be a node in the
     * decision tree
     * @param minClassDist - HashMap<Object, HashMap<Object, Integer>> -
     * <attrValue, <class-value, #-occurrences>>
     */
    private void addNodeToDecisionTree(String nodeAttrName, HashMap<Object, HashMap<Object, Integer>> minClassDist) {
        //System.out.println("Adding node " + nodeAttrName);
        this.dTree.addNode(nodeAttrName, false); // false because this is an attribute, not a classifier

        if (nodeAttrName == null) {
            return;
        }

        //System.out.print("\"" + nodeAttrName + "\" removed from this.attributeNames. Size " + this.attributeNames.size() + " -> ");
        this.attributeNames.remove(nodeAttrName);
        //System.out.println(this.attributeNames.size());

        // Add leaf(s) if exists
        HashMap<Object, Object> stagedLeaf = new HashMap();
        // this if statement checks if there should be leaf node(s) created and gets the attrName for it/them
        if ((stagedLeaf = this.data.getStagedLeafValue(nodeAttrName)) != null) {
            for (Map.Entry<Object, Object> leaf : stagedLeaf.entrySet()) {
                //System.out.println("Adding leaf " + (String)leaf.getKey() + ", " + (String)leaf.getValue());
                this.dTree.getCurNode().addChild(leaf.getKey(), (String) leaf.getValue(), true, false); //addChild(attrValue, classValue, thisIsAClassifier, takeBranch?)
                updateDataSetForLeafNode(nodeAttrName, leaf.getKey());
            }
        }

        this.data.clearStagedLeafs();

        // Add remaining branch(es)
        for (Map.Entry<Object, HashMap<Object, Integer>> entry : minClassDist.entrySet()) {
            // if this branchValue has not already been added as a leaf child, add as an empty branch
            if (!this.dTree.getCurNode().hasChild(entry.getKey())) {
                // Take the branch because need to add a node to it
                //System.out.println("Adding empty branch for " + entry.getKey());
                this.dTree.getCurNode().addChild(entry.getKey(), null, true, true); //addChild(branchValue, null (empty branch), thisIsAClassifier, takeBranch?)
            }
        }
    }

    /**
     * Removes all rows from dataSet that contain the node name and branch value
     * given
     *
     * @param attrName - node name
     * @param attrValue - branch value
     */
    private void updateDataSetForLeafNode(String attrName, Object attrValue) {
        // Iterate through the data set one row(HashMap<String, Object>) at a time
        for (int i = 0; i < this.data.getDataSet().size(); i++) {

            // If the row has an occurrence of given <attrName, !attrValue>, remove the row. I.e. does not contain the branch value
            if (this.data.getDataRow(i).containsKey(attrName) && data.getDataRow(i).get(attrName).equals(attrValue)) {
                //System.out.println("Removing row " + i + " because it contains <" + attrName + ", " + data.getDataRow(i).get(attrName) + ">");
                this.data.getDataSet().remove(i);
                i--;
            }
        }

        //System.out.println();
        this.data.printDataSet();
    }

    /**
     *
     * @param splitGINIValues - HashMap<String, Double> keyed by attr name,
     * GINIsplit value
     * @return String attribute name having smallest GINIsplit value
     */
    private String determineMinSplitGINIAttr(HashMap<String, Double> splitGINIValues) {
        SimpleEntry minAttr = null;

        for (Map.Entry<String, Double> entry : splitGINIValues.entrySet()) {
            if (minAttr == null || entry.getValue() < (Double) minAttr.getValue()) {
                //System.out.println("New min: " + entry.getKey() + " : " + entry.getValue());
                minAttr = new SimpleEntry(entry.getKey(), entry.getValue());
            }
        }

        if (minAttr == null) {
            return null;
        }

        //System.out.println("Min GINI split is " + minAttr.getKey() + " : " + minAttr.getValue());
        return (String) minAttr.getKey();
    }

    /**
     *
     * @param splitGAINValues - HashMap<String, Double> keyed by attr name,
     * GAINsplit value
     * @return String attribute name having largest GAINsplit value
     */
    private String determineMaxSplitGAINAttr(HashMap<String, Double> splitGAINValues) {
        SimpleEntry maxAttr = null;

        for (Map.Entry<String, Double> entry : splitGAINValues.entrySet()) {
            if (maxAttr == null || entry.getValue() > (Double) maxAttr.getValue()) {
                //System.out.println("New min: " + entry.getKey() + " : " + entry.getValue());
                maxAttr = new SimpleEntry(entry.getKey(), entry.getValue());
            }
        }

        if (maxAttr == null) {
            return null;
        }

        //System.out.println("Max GAIN split is " + maxAttr.getKey() + " : " + maxAttr.getValue());
        return (String) maxAttr.getKey();
    }

    private void printClassDist(HashMap<Object, HashMap<Object, Integer>> classDist) {
        // Consider implementing this
    }

    /**
     *
     * @param attrName
     * @return HashMap<Object, HashMap<String, Integer>> - <attrValue,
     * <class-value, #-occurrences>>
     */
    public HashMap<Object, HashMap<Object, Integer>> findClassDist(String attrName) {
        HashMap<Object, HashMap<Object, Integer>> classDist = new HashMap();
        Integer occurrences;
        //int rowNum = 0;
        // count each row in the data set toward the distribution
        for (HashMap<String, Object> row : this.data.getDataSet()) {
            //System.out.println("Row " + rowNum);
            // If no entry for attribute value, add it. E.g. if A is not a key yet, add it as key with empty HashMap value
            //System.out.println("Checking if key " + row.get(attrName) + "exists: " + classDist.containsKey(row.get(attrName)));
            if (!classDist.containsKey(row.get(attrName))) {
                //System.out.println("Adding new HashMap for " + row.get(attrName));
                classDist.put(row.get(attrName), new HashMap<Object, Integer>());
            }

            // get current count for current row's class value (e.g. if Round has been found once for the given attribute, increment its count)
            if ((occurrences = classDist.get(row.get(attrName)).get(row.get(this.className))) != null) {
                //System.out.println("Incrementing " + row.get(attrName) + ">" + row.get(this.className) + " = " + (occurrences + 1));
                classDist.get(row.get(attrName)).replace(row.get(this.className), occurrences + 1); // increment class value occurrences for the attribute value. e.g. for attribute value A, increment Round by 1
            } else { // no entry yet, add class value as key and empty HashMap as value. E.g. Round, 0
                //System.out.println("Adding new HashMap for " + row.get(attrName) + ">" + row.get(this.className));
                classDist.get(row.get(attrName)).put(row.get(this.className), 1);
            }
            //rowNum ++;
        }
        return classDist;
    }

    /**
     *
     * @param classDist - HashMap<Object, HashMap<String, Integer>> -
     * <attrValue, <class-value, #-occurrences>>
     * @ret
     * urn double GINI split value for the attribute in the classDist
     */
    private double GINISplit(HashMap<Object, HashMap<Object, Integer>> classDist, String attrName) {
        int totalValues = getTotalValuesInClassDist(classDist);
        double splitGINIValue = 0;

        // For each attribute value. E.g. A
        for (Map.Entry<Object, HashMap<Object, Integer>> attr : classDist.entrySet()) {
            int totalForAttr = 0;
            String classValue = null;
            //System.out.println("Counting for " + attr.getKey());
            for (Map.Entry<Object, Integer> entry : classDist.get(attr.getKey()).entrySet()) {
                if (classValue == null) { // Store class value in case the GINI for the attribute value is 0 meaning it is a leaf
                    classValue = (String) entry.getKey();
                }
                //System.out.println("Counting " + entry.getValue() + " for " + entry.getKey());
                totalForAttr += entry.getValue();
            }
            Double curGINI = 1.0;
            //System.out.print("GINI(" + attr.getKey() + ") = 1");
            for (Map.Entry<Object, Integer> entry : classDist.get(attr.getKey()).entrySet()) {
                //System.out.print(" - (" + entry.getValue() + " / " + totalForAttr + ")^2");

                curGINI -= Math.pow((new Double(entry.getValue()) / new Double(totalForAttr)), 2);
            }
            //System.out.println(" = " + curGINI);

            if (doubleIsZero(curGINI)) {
                //System.out.println("GINI = 0. Adding to stagedLeafs(" + attrName + ", " + attr.getKey() + ", " + classValue + ")");
                this.data.stageLeaf(attrName, attr.getKey(), classValue);
            }

            splitGINIValue += (new Double(totalForAttr) / new Double(totalValues)) * curGINI;
            //System.out.println("GINIsplit += (" + totalForAttr + " / " + totalValues + ") * " + curGINI + " = " + ((new Double(totalForAttr) / new Double(totalValues)) * curGINI));
        }

        //System.out.println("GINI split value for attr = " + splitGINIValue);
        return splitGINIValue;
    }

    private double GAINSplit(HashMap<Object, HashMap<Object, Integer>> classDist, String attrName) {
        int totalValues = getTotalValuesInClassDist(classDist);
        double totalEntropy = 0.0;
        double classDistEntropy = 0.0;

        // For each attribute value. E.g. A
        for (Map.Entry<Object, HashMap<Object, Integer>> attr : classDist.entrySet()) {
            int totalForAttr = 0;
            String classValue = null;
            //System.out.println("Counting for " + attr.getKey());
            for (Map.Entry<Object, Integer> entry : classDist.get(attr.getKey()).entrySet()) {
                if (classValue == null) { // Store class value in case the Entropy for the attribute value is 0 meaning it is a leaf
                    classValue = (String) entry.getKey();
                }
                //System.out.println("Counting " + entry.getValue() + " for " + entry.getKey());
                totalForAttr += entry.getValue();
            }
            Double curEntropy = 0.0;
            //System.out.print("Entropy(" + attr.getKey() + ") = ");
            for (Map.Entry<Object, Integer> entry : classDist.get(attr.getKey()).entrySet()) {
                //System.out.print(" - (" + entry.getValue() + " / " + totalForAttr + ") * log2(" + entry.getValue() + " / " + totalForAttr + ")");
                //System.out.println(" = " + (new Double(entry.getValue()) / new Double(totalForAttr)) + " * " + (Math.log10(new Double(entry.getValue()) / new Double(totalForAttr)) / Math.log10(2.)));
                curEntropy -= (new Double(entry.getValue()) / new Double(totalForAttr)) * (Math.log10(new Double(entry.getValue()) / new Double(totalForAttr)) / Math.log10(2.));
            }
            //System.out.println(" = " + curEntropy);

            if (doubleIsZero(curEntropy)) {
                //System.out.println("GAIN = 0. Adding to stagedLeafs(" + attrName + ", " + attr.getKey() + ", " + classValue + ")");
                this.data.stageLeaf(attrName, attr.getKey(), classValue);
            }

            totalEntropy += (new Double(totalForAttr) / new Double(totalValues)) * curEntropy;
            //System.out.println("GINIsplit += (" + totalForAttr + " / " + totalValues + ") * " + curGINI + " = " + ((new Double(totalForAttr) / new Double(totalValues)) * curGINI));

            classDistEntropy -= (new Double(totalForAttr) / new Double(totalValues)) * (Math.log10(new Double(totalForAttr) / new Double(totalValues)) / Math.log10(2.));

        }

        //System.out.println("Total Entropy value for attr = " + totalEntropy);
        //System.out.println("ClassDist Entropy value for attr = " + classDistEntropy);
        double splitGAINValue = classDistEntropy - totalEntropy;
        //System.out.println("SplitGAIN value for attr = " + splitGAINValue);

        return splitGAINValue;
    }

    /**
     * Compares the given double to 0 within a small threshold
     *
     * @param value - double to compare to 0ish
     * @return true if double is within threshold, false otherwise
     */
    private boolean doubleIsZero(double value) {
        double threshold = 0.0001;
        return value >= -threshold && value <= threshold;
    }

    /**
     *
     * @param classDist - HashMap<Object, HashMap<String, Integer>> -
     * <attrValue, <class-value, #-occurrences>>
     * @ret
     * urn integer total number of values for attribute in classDist
     */
    private int getTotalValuesInClassDist(HashMap<Object, HashMap<Object, Integer>> classDist) {
        int total = 0;

        // For each attribute value. E.g. A
        for (Map.Entry<Object, HashMap<Object, Integer>> attr : classDist.entrySet()) {
            for (Map.Entry<Object, Integer> entry : classDist.get(attr.getKey()).entrySet()) {
                total += entry.getValue();
            }
        }
        //System.out.println("Total = " + total);
        return total;
    }

    /**
     * performs pre-order traversal and writes to output file
     *
     * @param curNode
     * @throws UnsupportedEncodingException
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void genOutputFile(Node curNode, Object branchValue) {

        try {
            if (curNode == null) {
                return;
            }

            // Write node value
            System.out.println(curNode.getNodeValue());
            if (curNode.getNodeValue() == null) {
                globalWriter.write("null," + branchValue + "\n");
            } else {
                globalWriter.write(curNode.getNodeValue() + "," + branchValue + "," + curNode.isClassifer() + "\n");
            }

            genOutputFile(curNode.getLeftNode(), curNode.getLeftBranchValue());
            genOutputFile(curNode.getRightNode(), curNode.getRightBranchValue());

        } catch (IOException e) {
            System.err.println("Failed while creating output file for model");
            System.exit(1);
        }

    }
}
