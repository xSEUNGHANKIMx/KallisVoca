package com.kallis.satvocabulary;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.net.Uri;

/**
 * Created by Administrator on 2017-10-04.
 */

public abstract class VocabModelLoader<T> extends AsyncTaskLoader<T> {

    final Uri mContentUri;
    final ForceLoadContentObserver mContentObserver;
    private Context mContext = null;

    public VocabModelLoader(Context context, Uri contentUri) {
        super(context);
        mContext = context;
        mContentUri = contentUri;
        mContentObserver = new ForceLoadContentObserver();
    }

    @Override
    protected void onStartLoading() {
        if (mContext != null) {
            getContext().getContentResolver().registerContentObserver(mContentUri, false, mContentObserver);
            if (takeContentChanged()) {
                forceLoad();
            } else {
                T prevResult = takePrevResult();
                if (prevResult != null) {
                    deliverResult(prevResult);
                } else {
                    forceLoad();
                }
            }
        }
    }

    @Override
    protected void onReset() {
        getContext().getContentResolver().unregisterContentObserver(
                mContentObserver);
    }

    protected T takePrevResult() {
        return null;
    }
}
