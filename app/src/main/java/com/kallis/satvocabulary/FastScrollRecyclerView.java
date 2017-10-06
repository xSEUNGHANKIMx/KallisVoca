package com.kallis.satvocabulary;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

public class FastScrollRecyclerView extends RecyclerView {
    private Context ctx;

    private boolean setupThings = false;
    public static int indWidth = 25;
    public static int indHeight= 18;
    public float scaledWidth;
    public float scaledHeight;
    public char[] indexer = VocabConfig.INDEX_SCROLLER_STRING.toCharArray();
    public float sx;
    public float sy;
    public char section;
    public boolean showLetter = false;
    private Handler listHandler;

    public FastScrollRecyclerView(Context context) {
        super(context);
        ctx = context;
    }

    public FastScrollRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ctx = context;
    }

    public FastScrollRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ctx = context;
    }

    @Override
    public void onDraw(Canvas c) {
        if(!setupThings)
            setupThings();
        super.onDraw(c);
    }

    private void setupThings() {
        //create az text data
        scaledWidth = indWidth * ctx.getResources().getDisplayMetrics().density;
        scaledHeight = indHeight * ctx.getResources().getDisplayMetrics().density + 15;
        sx = this.getWidth() - this.getPaddingRight() - (float) (1.2 * scaledWidth) + 50;
        sy = (float) ((this.getHeight() - (scaledHeight * indexer.length)) / 2.0) - 50;
        setupThings = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (x < sx - scaledWidth || y < sy || y > (sy + scaledHeight * indexer.length))
                    return super.onTouchEvent(event);
                else {
                    // We touched the index bar
                    float yy = y - this.getPaddingTop() - getPaddingBottom() - sy;
                    int currentPosition = (int) Math.floor(yy / scaledHeight);
                    if(currentPosition<0)currentPosition=0;
                    if(currentPosition>=indexer.length)currentPosition=indexer.length-1;
                    section = indexer[currentPosition];
                    showLetter = true;
                    int positionInData = 0;
                    if( ((FastScrollRecyclerViewInterface)getAdapter()).getMapIndex().containsKey(String.valueOf(section).toUpperCase()) )
                        positionInData = ((FastScrollRecyclerViewInterface)getAdapter()).getMapIndex().get(String.valueOf(section).toUpperCase());
                    this.scrollToPosition(positionInData);
                    FastScrollRecyclerView.this.invalidate();
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {

                if (!showLetter && (x < sx  - scaledWidth || y < sy || y > (sy + scaledHeight*indexer.length)))
                    return super.onTouchEvent(event);
                else {
                    float yy = y - sy;
                    int currentPosition = (int) Math.floor(yy / scaledHeight);
                    if(currentPosition<0)currentPosition=0;
                    if(currentPosition>=indexer.length)currentPosition=indexer.length-1;
                    section = indexer[currentPosition];
                    showLetter = true;
                    int positionInData = 0;
                    if(((FastScrollRecyclerViewInterface)getAdapter()).getMapIndex().containsKey(String.valueOf(section).toUpperCase()) )
                        positionInData = ((FastScrollRecyclerViewInterface)getAdapter()).getMapIndex().get(String.valueOf(section).toUpperCase());
                    this.scrollToPosition(positionInData);
                    FastScrollRecyclerView.this.invalidate();

                }
                break;

            }
            case MotionEvent.ACTION_UP: {
                listHandler = new ListHandler();
                listHandler.sendEmptyMessageDelayed(0, 100);
                if (x < sx - scaledWidth || y < sy || y > (sy + scaledHeight*indexer.length))
                    return super.onTouchEvent(event);
                else
                    return true;
//                break;
            }
        }
        return true;
    }

    private class ListHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            showLetter = false;
            FastScrollRecyclerView.this.invalidate();
        }


    }
}
