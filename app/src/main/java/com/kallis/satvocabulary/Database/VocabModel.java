package com.kallis.satvocabulary.Database;

public class VocabModel {
    String mWord;
    String mDesc;
    String mGroupping;
    String mListLabel;
    boolean mIsBookmark;
    boolean mIsFavorite;

    public VocabModel() {
        mWord = "";
        mDesc = "";
        mGroupping = "";
        mListLabel = "";
        mIsBookmark = false;
        mIsFavorite = false;
    }

    public VocabModel(int id, String word, String desc, String group, boolean bBookmark, boolean bFavorite) {
        mWord = word;
        mDesc = desc;
        mGroupping = group;
        mListLabel = String.valueOf(word.charAt(0));
        mIsBookmark = bBookmark;
        mIsFavorite = bFavorite;
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

    public boolean isFavorite() {
        return mIsFavorite;
    }

    public void setFavorite(boolean favorite) {
        this.mIsFavorite = favorite;
    }
}
