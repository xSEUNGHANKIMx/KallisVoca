package com.kallis.satvocabulary;

import android.graphics.Rect;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<ArrayList<VocabModel>> {

    private List<VocabModel> mDataset= new ArrayList<VocabModel>();
    private VocabDao mVocabDao;
    private VocabDBManager mDBManager;
    private Loader<ArrayList<VocabModel>> mLoader;

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

        String[] dataSet = null;
        try {
            dataSet = getAssets().list("demo-pictures");
        } catch (IOException e) {
            e.printStackTrace();
        }
        PhotoAdapter adapter = new PhotoAdapter(dataSet, this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin);
            }
        });
        mRecyclerView.setAdapter(adapter);

        mVocabDao = VocabDao.getInstance(this);
        mDBManager = VocabDBManager.getInstance(this);
        getSupportLoaderManager().initLoader(VocabConfig.VOCABULARY_LOADER_ID, null, this);
        //setData();
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
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void setData() {
        try {
            JSONObject obj = new JSONObject(loadJsonFromAsset());
            JSONArray words = obj.getJSONArray("vocabulary");
            for (int i = 0; i < words.length(); i++){
                JSONObject jsonObject = words.getJSONObject(i);
                String word = jsonObject.getString("word");
                String desc = jsonObject.getString("description");
                String group = jsonObject.getString("grouping");
                mDataset.add(new VocabModel(i, word, desc, group, false));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String loadJsonFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("vocabulary.json");
            int size = is.available();
            byte[] buffer = new byte[size];

            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    @Override
    public Loader<ArrayList<VocabModel>> onCreateLoader(int id, Bundle args) {
        mLoader = mVocabDao.getLoader();
        return mLoader;
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<VocabModel>> loader, ArrayList<VocabModel> data) {
        mDataset.clear();

        if(data.size() > 0) {
            mDataset.addAll(data);

        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<VocabModel>> loader) {
        mDataset.clear();
    }
}
