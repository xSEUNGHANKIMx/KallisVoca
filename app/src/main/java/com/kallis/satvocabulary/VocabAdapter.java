/*
 * Copyright 2015 Worldline.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kallis.satvocabulary;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kallis.satvocabulary.Database.VocabModel;
import com.kallis.satvocabulary.FastScroller.FastScrollRecyclerViewInterface;
import com.kallis.satvocabulary.FolderbleLayout.FoldableLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * TODO: Add a class header comment!
 */
public class VocabAdapter extends RecyclerView.Adapter<VocabAdapter.VocabViewHolder> implements FastScrollRecyclerViewInterface {

    private ArrayList<VocabModel> mDataSet = new ArrayList<VocabModel>();
    private Map<Integer, Boolean> mFoldStates = new HashMap<>();
    private HashMap<String, Integer> mMapIndex = new HashMap<>();
    private Context mContext;

    public VocabAdapter(Context context) {
        mContext = context;
    }

    public void setDataList(ArrayList<VocabModel> data) {
        if(data != null && data.size() > 0) {
            mDataSet.clear();
            mDataSet.addAll(data);

            char[] indexer = VocabConfig.INDEX_SCROLLER_STRING.toCharArray() ;
            for(int i = 0, j = 0; (i < data.size()) && (j < indexer.length); ++i) {
                char c = Character.toUpperCase(mDataSet.get(i).getWord().charAt(0));
                if(c == indexer[j]) {
                    mMapIndex.put(String.valueOf(indexer[j++]), i);
                }
            }
        }

    }

    public void clearDataList() {
        mDataSet.clear();
    }

    @Override
    public VocabViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VocabViewHolder(new FoldableLayout(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(final VocabViewHolder holder, int position) {

        // Bind data
        holder.mTextViewWord.setText(mDataSet.get(position).getWord());
        holder.mTextViewDesc.setText(mDataSet.get(position).getDesc());

        // Bind state
        if (mFoldStates.containsKey(position)) {
            if (mFoldStates.get(position) == Boolean.TRUE) {
                if (!holder.mFoldableLayout.isFolded()) {
                    holder.mFoldableLayout.foldWithoutAnimation();
                }
            } else if (mFoldStates.get(position) == Boolean.FALSE) {
                if (holder.mFoldableLayout.isFolded()) {
                    holder.mFoldableLayout.unfoldWithoutAnimation();
                }
            }
        } else {
            holder.mFoldableLayout.foldWithoutAnimation();
        }

        holder.mButtonFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
            }
        });

        holder.mFoldableLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.mFoldableLayout.isFolded()) {
                    holder.mFoldableLayout.unfoldWithAnimation();
                } else {
                    holder.mFoldableLayout.foldWithAnimation();
                }
            }
        });
        holder.mFoldableLayout.setFoldListener(new FoldableLayout.FoldListener() {
            @Override
            public void onUnFoldStart() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.mFoldableLayout.setElevation(5);
                }
            }

            @Override
            public void onUnFoldEnd() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.mFoldableLayout.setElevation(0);
                }
                mFoldStates.put(holder.getAdapterPosition(), false);
            }

            @Override
            public void onFoldStart() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.mFoldableLayout.setElevation(5);
                }
            }

            @Override
            public void onFoldEnd() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.mFoldableLayout.setElevation(0);
                }
                mFoldStates.put(holder.getAdapterPosition(), true);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    @Override
    public HashMap<String, Integer> getMapIndex() {
        return this.mMapIndex;
    }

    protected static class VocabViewHolder extends RecyclerView.ViewHolder {

        protected FoldableLayout mFoldableLayout;

        @Bind(R.id.textview_cover)
        protected TextView mTextViewWord;

        @Bind(R.id.textview_detail)
        protected TextView mTextViewDesc;

        @Bind(R.id.share_button)
        protected Button mButtonFavorite;

        public VocabViewHolder(FoldableLayout foldableLayout) {
            super(foldableLayout);
            mFoldableLayout = foldableLayout;
            foldableLayout.setupViews(R.layout.list_item_word, R.layout.list_item_desc, R.dimen.card_cover_height, itemView.getContext());
            ButterKnife.bind(this, foldableLayout);
        }
    }
}
