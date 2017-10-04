package com.kallis.satvocabulary;

public class VocabModel {
    String mWord;
    String mDesc;
    String mGroupping;
    String mListLabel;
    boolean mIsBookmark;

    public VocabModel() {
        mWord = "";
        mDesc = "";
        mGroupping = "";
        mListLabel = "";
        mIsBookmark = false;
    }

    public VocabModel(int id, String word, String desc, String group, boolean bBookmark) {
        mWord = word;
        mDesc = desc;
        mGroupping = group;
        mListLabel = String.valueOf(word.charAt(0));
        mIsBookmark = bBookmark;
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

    public String getGrouping() {
        return mGroupping;
    }

    public void setGrouping(String grouping) {
        this.mGroupping = grouping;
    }

    public String getListLabel() {
        return mListLabel;
    }

    public void setListLabel(String listLabel) {
        this.mListLabel = listLabel;
    }

    public boolean isBookmark() {
        return mIsBookmark;
    }

    public void setBookmark(boolean bookmark) {
        this.mIsBookmark = bookmark;
    }
}
