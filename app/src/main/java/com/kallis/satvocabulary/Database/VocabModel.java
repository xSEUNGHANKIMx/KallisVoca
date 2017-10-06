package com.kallis.satvocabulary.Database;

public class VocabModel {
    int mId;
    String mWord;
    String mDesc;
    String mGroupping;
    String mListLabel;
    boolean mIsBookmark;
    boolean mIsFavorite;

    public VocabModel() {
        mId = -1;
        mWord = "";
        mDesc = "";
        mGroupping = "";
        mListLabel = "";
        mIsBookmark = false;
        mIsFavorite = false;
    }

    public VocabModel(int id, String word, String desc, String group, boolean bBookmark, boolean bFavorite) {
        mId = id;
        mWord = word;
        mDesc = desc;
        mGroupping = group;
        mListLabel = String.valueOf(word.charAt(0));
        mIsBookmark = bBookmark;
        mIsFavorite = bFavorite;
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
