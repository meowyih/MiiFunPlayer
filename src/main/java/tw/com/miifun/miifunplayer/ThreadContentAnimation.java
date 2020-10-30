package tw.com.miifun.miifunplayer;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by yhorn on 2017/2/3.
 */

public class ThreadContentAnimation extends Thread {

    final static private String TAG = "ThreadContentAnimation";

    ImageView mIvCatLogo;
    boolean mIsRunning;
    Context mContext;

    public ThreadContentAnimation( Context context, ImageView ivCatLogo ) {
        mContext = context;
        mIvCatLogo = ivCatLogo;
        mIsRunning = false;
    }

    public void setStopFlag() {
        // Log.i( TAG, "setStopFlag" );
        mIsRunning = false;
    }

    @Override
    public void run() {
        mIsRunning = true;
        long now, curTick = 0, oldTick = 0;
        long start = System.currentTimeMillis();

        if ( mIvCatLogo == null ) {
            Log.e( TAG, "imageview mIvCatLogo/mIvStar is null, stop thread" );
            return;
        }

        Log.i( TAG, "ThreadContentAnimation " + this + " starts" );

        while( mIsRunning ) {
            now = System.currentTimeMillis();
            curTick = ( now - start ) / 1140;

            if ( curTick > oldTick ) {

                oldTick = curTick;

                if ( (curTick + 1 ) % 8 == 0 ) {
                    ((MainActivity)mContext).runOnUiThread( new Runnable() {
                        @Override public void run() {
                            mIvCatLogo.setImageResource(R.drawable.main_cat_laugh);
                        }
                    });
                }
                else if ( ( curTick + 1 ) % 4 == 0 ) {
                    ((MainActivity)mContext).runOnUiThread( new Runnable() {
                        @Override public void run() {
                            mIvCatLogo.setImageResource(R.drawable.main_cat_top);
                        }
                    });
                }
                else if ( curTick % 2 == 0 ) {
                    ((MainActivity)mContext).runOnUiThread( new Runnable() {
                        @Override public void run() {
                            mIvCatLogo.setImageResource(R.drawable.main_cat_left);
                        }
                    });
                }
                else {
                    ((MainActivity)mContext).runOnUiThread( new Runnable() {
                        @Override public void run() {
                            mIvCatLogo.setImageResource( R.drawable.main_cat_right );
                        }
                    });
                }
            }
        }

        Log.i( TAG, "ThreadContentAnimation " + this + " finished." );
    }
}
