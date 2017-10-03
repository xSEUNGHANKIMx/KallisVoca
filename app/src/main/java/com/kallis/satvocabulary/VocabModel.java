package com.kallis.satvocabulary;

public class VocabModel {
    int mId;
    String mWord;
    String mDesc;
    String mGroup;
    String mListLabel;

    public VocabModel() {
        mId = -1;
        mWord = "";
        mDesc = "";
        mGroup = "";
        mListLabel = "";
    }

    public VocabModel(int id, String word, String desc, String group) {
        mId = id;
        mWord = word;
        mDesc = desc;
        mGroup = group;
        mListLabel = String.valueOf(word.charAt(0));
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public String getWord() {
        return mWord;
    }

    public void setWord(String word) {
        this.mWord = word;
    }

    public String getDesc() {
        return mDesc;
    }

    public void setDesc(String desc) {
        this.mDesc = desc;
    }

    public String getGroup() {
        return mGroup;
    }

    public void setGroup(String group) {
        this.mGroup = group;
    }

    public String getListLabel() {
        return mListLabel;
    }

    public void setListLabel(String listLabel) {
        this.mListLabel = listLabel;
    }
}
