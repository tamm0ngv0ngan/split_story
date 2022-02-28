package com.company;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class Chapter implements Cloneable {
    private int chapterNumber;
    private String chapterName;
    private boolean isMultiple;
    private int numOfLines;
    private StringBuffer data;
    private Chapter nextChapter;
    private static final String RESULT_DIRECTORY = "/Users/ducnguyen/test/result/";
    private static final String[] checkLastChar = {"?", ":", ".", "!"};

    public Chapter() {
        this.chapterNumber = 0;
        this.chapterName = "";
        this.numOfLines = 0;
        this.nextChapter = null;
        this.data = new StringBuffer("");
    }

    public Chapter(int chapterNumber, String chapterName, boolean isMultiple, int numOfLines, StringBuffer data) {
        this.chapterNumber = chapterNumber;
        this.chapterName = chapterName;
        this.isMultiple = isMultiple;
        this.numOfLines = numOfLines;
        this.data = new StringBuffer(data.toString());
    }

    void resetData() {
        this.chapterNumber = 0;
        this.chapterName = "";
        this.numOfLines = 0;
        this.data = new StringBuffer("");
    }

    @Override
    public Chapter clone() {
        return new Chapter(this.chapterNumber, this.chapterName, this.isMultiple, this.numOfLines, this.data);
    }

    public void mergeChapter(Chapter another) {
        Chapter tmpChapter = this;
        while (tmpChapter.nextChapter != null) {
            if (tmpChapter.isMultiple) {
                tmpChapter = tmpChapter.nextChapter;
            } else {
                System.out.println("Multiple of chapter " +
                        tmpChapter.chapterNumber + ": " + tmpChapter.chapterName + " failed!");
                return;
            }
        }
        tmpChapter.nextChapter = another;
    }

    public void setChapterNumber(int chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public int getNumOfLines() {
        return numOfLines;
    }

    public void increaseNumOfLines() {
        this.numOfLines += 1;
    }


    public void appendData(String appendedData) {
        if (appendedData.length() < 20) {
            this.data.append(appendedData).append("\n");
            return;
        }
        String lastCharacter = appendedData.substring(appendedData.length() - 1);
        if (Arrays.asList(Chapter.checkLastChar).contains(lastCharacter)) {
            this.data.append(appendedData).append("\n");
            return;
        }
        this.data.append(appendedData).append(" ");
    }


    public void setMultiple(boolean multiple) {
        isMultiple = multiple;
    }

    public void generateFileChapter() {
        String fileName = this.chapterNumber + "." + this.chapterName;
        String filePath = Chapter.RESULT_DIRECTORY + fileName + ".txt";
        try {
            File chapter = new File(filePath);
            if (chapter.createNewFile()) {
                FileWriter fw = new FileWriter(filePath);
                fw.write(this.data.toString());
                fw.close();
                System.out.println("Create file: " + fileName + " success!");
            } else {
                System.out.println("Create file: " + fileName + " failed!");
            }
        } catch (IOException e) {
            System.out.println("Create file: " + fileName + " failed!");
            e.printStackTrace();
        }
    }
}
