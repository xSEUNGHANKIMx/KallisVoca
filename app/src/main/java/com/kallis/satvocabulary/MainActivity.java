package com.kallis.satvocabulary;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.kallis.satvocabulary.Database.VocabDBManager;
import com.kallis.satvocabulary.Database.VocabDao;
import com.kallis.satvocabulary.Database.VocabModel;
import com.kallis.satvocabulary.FastScroller.FastScrollRecyclerViewItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<ArrayList<VocabModel>> {

    private VocabDao mVocabDao;
    private VocabDBManager mDBManager;
    private Loader<ArrayList<VocabModel>> mLoader;
    private ArrayList<VocabModel> mDataSet = new ArrayList<>();
    private VocabAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ContentObserver mDBObserver;
    private ImageView mIndicator;
    private AnimationDrawable mAnim;
    protected String mSearchInputString = "";

    @Bind(R.id.recycler_view)
    protected RecyclerView mRecyclerView;

    @Bind(R.id.toolbar)
    protected Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);


        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin);
            }
        });

        mAdapter = new VocabAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new FastScrollRecyclerViewItemDecoration(this) {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin);
            }
        });
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mVocabDao = VocabDao.getInstance(this);
        mDBManager = VocabDBManager.getInstance(this);
        getSupportLoaderManager().initLoader(VocabConfig.VOCABULARY_LOADER_ID, null, this);
        mDBObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                if (mLoader != null) {
                    mLoader.startLoading();
                }
            }
        };

        mIndicator = (ImageView) findViewById(R.id.initial_loading_anim);
        mAnim = (AnimationDrawable) mIndicator.getDrawable();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

            if(mAnim.isRunning()) {
                mAnim.stop();
                mAnim.start();
            }
    }

    @Override
    public void onResume(){
        super.onResume();
        getContentResolver().registerContentObserver(VocabDao.CONTENT_URI, true, mDBObserver);

        if (mLoader != null) {
            mLoader.startLoading();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        getContentResolver().unregisterContentObserver(mDBObserver);

        if (mLoader != null) {
            mLoader.stopLoading();
        }
    }

    private HashMap<String, Integer> calculateIndexesForName(ArrayList<String> items){
        HashMap<String, Integer> mapIndex = new LinkedHashMap<String, Integer>();
        for (int i = 0; i < items.size(); i++){
            String name = items.get(i);
            String index = name.substring(0,1);
            index = index.toUpperCase();

            if (!mapIndex.containsKey(index)) {
                mapIndex.put(index, i);
            }
        }
        return mapIndex;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                setFilterText(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    void setFilterText(String text) {
        new VocabListFilterTask().execute(text.toLowerCase());
    }

    class VocabListFilterTask extends AsyncTask<String, Void, ArrayList<VocabModel>> {

        @Override
        protected void onPreExecute() {
            mRecyclerView.setAdapter(null);
            super.onPreExecute();
        }

        @Override
        protected ArrayList<VocabModel> doInBackground(String... params) {
            mSearchInputString = params[0];

            if (!TextUtils.isEmpty(mSearchInputString)) {
                ArrayList<VocabModel> filteredList = new ArrayList<VocabModel>();

                for (VocabModel model : mDataSet) {
                    if (model != null) {
                        int machedCount = 0;

                        if (findWordMachedString(model)) {
                            machedCount++;
                        } else {
                            model.setSearchMatchedStart(-1);
                            model.setSearchMatchedEnd(-1);
                        }

                        if (machedCount > 0) {
                            filteredList.add(model);
                        }
                    }
                }

                return filteredList;
            } else {
                return mDataSet;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<VocabModel> result) {

            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setDataList(result);
            mAdapter.notifyDataSetChanged();

            if (!result.isEmpty()) {
                if (!TextUtils.isEmpty(mSearchInputString)) {
                    mRecyclerView.scrollToPosition(0);
                }
            }
        }

        private boolean findWordMachedString(VocabModel model) {
            if (model == null) {
                return false;
            }

            String word = model.getWord();
            boolean bIsMached = false;

            if (word.length() >= mSearchInputString.length()) {
                int start = -1, end = -1, offset = 0;
                if (word.indexOf(mSearchInputString) >= 0) {
                    start = word.indexOf(mSearchInputString);
                    end = start + mSearchInputString.length();

                    model.setSearchMatchedStart(start);
                    model.setSearchMatchedEnd(end);
                    bIsMached = true;
                }
            }

            return bIsMached;
        }
    }

    @Override
    public Loader<ArrayList<VocabModel>> onCreateLoader(int id, Bundle args) {
        mLoader = mVocabDao.getLoader();
        return mLoader;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(TextUtils.isEmpty(mSearchInputString)) {
            int firstVisiblePosition = 0;
            LinearLayoutManager layoutmanager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            firstVisiblePosition = layoutmanager.findFirstVisibleItemPosition();

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(VocabConfig.PREFERENCE_LAST_SCROLLED_INDEX, firstVisiblePosition);
            editor.apply();
        }
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<VocabModel>> loader, ArrayList<VocabModel> data) {

        if(data.size() > 0) {
            if(mAnim.isRunning()) {
                mIndicator.setVisibility(View.GONE);
                mAnim.stop();
            }

            if(mRecyclerView.getVisibility() != View.VISIBLE) {
                mRecyclerView.setVisibility(View.VISIBLE);
            }

            boolean bInitialLoading = mDataSet.size() == 0 ? true : false;

            mDataSet.clear();
            mDataSet.addAll(data);

            if(TextUtils.isEmpty(mSearchInputString)) {
                mAdapter.setDataList(data);
                mAdapter.notifyDataSetChanged();

                if(bInitialLoading) {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                    int firstVisiblePosition = preferences.getInt(VocabConfig.PREFERENCE_LAST_SCROLLED_INDEX, 0);
                    mRecyclerView.scrollToPosition(firstVisiblePosition);
                }
            } else {
                setFilterText(mSearchInputString);
            }
        } else {
            mIndicator.setVisibility(View.VISIBLE);
            mAnim.start();
            mVocabDao.migrateDataToSQLiteDB();
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<VocabModel>> loader) {
        mAdapter.clearDataList();
    }

    private ArrayList<String> getWordList(ArrayList<VocabModel> list) {
        ArrayList<String> wordlist = new ArrayList<String>();

        for(VocabModel model : list) {
            wordlist.add(model.getWord());
        }

        return wordlist;
    }
}
