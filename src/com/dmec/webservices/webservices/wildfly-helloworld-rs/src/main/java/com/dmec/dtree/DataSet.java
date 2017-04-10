package com.dmec.dtree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DataSet {

    // dataSet is accessible by row #, then column (attribute) string
    private ArrayList<HashMap<String, Object>> dataSet; // array of HashMap<attr-name, attr-value>
    private ArrayList<String> attributeNames = null;
    private HashMap<String, HashMap<Object, Object>> stagedLeafs; // <attr-name, <attr-value, class-value>>
    private String classifyingAttr;

    /**
     * Initialize data set from data file
     *
     * @param dataFilePath
     * @param delimiter
     */
    public DataSet(String dataFilePath, String delimiter) {
        this.dataSet = new ArrayList<HashMap<String, Object>>();
        this.stagedLeafs = new HashMap();
        insertData(dataFilePath, delimiter);
    }

    public String getClassifyingAttr() {
        return this.classifyingAttr;
    }

    /**
     * first line is classifying attribute, second is list of attributes, rest
     * are data
     *
     * @param dataFilePath
     * @param delimiter
     */
    private void insertData(String dataFilePath, String delimiter/*, boolean isTrainingSet*/) {
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(dataFilePath));

            String line;

            //if (isTrainingSet) {
                // get classifying attribute
                if ((line = br.readLine()) != null) {
                    this.classifyingAttr = line;
                    System.out.println("Classifying attr name: " + this.classifyingAttr);
                }
            //}

            // Get attribute names
            if ((line = br.readLine()) != null) {
                this.attributeNames = new ArrayList(Arrays.asList(line.split(delimiter)));
            }
            System.out.println("Attr names: " + this.attributeNames);

            // Get data rows
            while ((line = br.readLine()) != null) {
                ArrayList<String> tempRow = new ArrayList(Arrays.asList(line.split(delimiter)));
                HashMap<String, Object> dataRow = new HashMap();

                for (int i = 0; i < tempRow.size(); i++) {
                    // Insert (attrName, data-value)
                    dataRow.put(this.attributeNames.get(i), tempRow.get(i));
                }

                this.dataSet.add(dataRow);
            }
        } catch (IOException e) {
            System.err.println("Failed while inserting data into dataSet object");
            System.exit(1);
        }
    }

    public void stageLeaf(String attrName, Object attrValue, Object classValue) {
        // instantiate HashMap for the attrName the first time
        if (this.stagedLeafs.get(attrName) == null) {
            this.stagedLeafs.put(attrName, new HashMap());
        }
        this.stagedLeafs.get(attrName).put(attrValue, classValue);
    }

    public void clearStagedLeafs() {
        this.stagedLeafs = new HashMap();
    }

    public HashMap<Object, Object> getStagedLeafValue(String attrName) {
        return this.stagedLeafs.get(attrName);
    }

    /**
     * Inserts given data row into dataSet
     *
     * @param dataRow - row to insert into dataSet
     */
    public void insertRow(HashMap<String, Object> dataRow) {
        dataSet.add(dataRow);

        if (attributeNames == null) {
            setAttrNames(dataRow);
        }
    }

    private void setAttrNames(HashMap<String, Object> dataRow) {
        this.attributeNames = new ArrayList<String>();
        for (Map.Entry<String, Object> entry : dataRow.entrySet()) {
            this.attributeNames.add(entry.getKey());
        }
    }

    public void addAttrName(String attrName) {
        this.attributeNames.add(attrName);
    }

    /**
     * Returns the data set
     *
     * @return the data set
     */
    public ArrayList<HashMap<String, Object>> getDataSet() {
        return dataSet;
    }

    /**
     * Returns the specified row from the data set
     *
     * @param rowNum - row to get
     * @return row from dataSet at specified number
     */
    public HashMap<String, Object> getDataRow(int rowNum) {
        return this.dataSet.get(rowNum);
    }

    public ArrayList<String> getattributeNames() {
        return this.attributeNames;
    }

    public void printDataSet() {
        for (String attrName : this.attributeNames) {
            System.out.print(attrName + "   ");
        }
        System.out.println("\n-------------------------------");

        for (HashMap<String, Object> row : this.dataSet) {
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                System.out.print(entry.getValue() + "   ");
            }
            System.out.println();
        }
        System.out.println();
    }
}