package tw.com.miifun.miifunplayer;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

/**
 * Created by yhorng on 2017/1/29.
 */

abstract public class ThreadGame extends Thread {

    final static private String TAG = "ThreadGame";

    final static public int MOTION_EVENT_ON_EXIT = 1000;

    Context mContext;
    int mCanvasWidth;
    int mCanvasHeight;
    SurfaceHolder mSurfaceHolder;
    MotionEvent mMotionEvent;

    boolean mKeepRunning;
    boolean mIsRunning;
    boolean mFinishActivityAtTheEnd;

    long mStartTime;
    long mCurrentTime;

    public ThreadGame(Context context, SurfaceHolder holder, int width, int height ) {
        mContext = context;
        mSurfaceHolder = holder;
        mCanvasWidth = width;
        mCanvasHeight = height;
        mMotionEvent = null;

        mKeepRunning = true;
        mIsRunning = false;
        mFinishActivityAtTheEnd = false;
        mCurrentTime = System.currentTimeMillis();
    }

    public boolean isRunning() {
        return mIsRunning;
    }

    @Override
    public void run() {

        int ret;

        mIsRunning = true;
        mStartTime = System.currentTimeMillis();

        Log.i( TAG, "ThreadGame " + this + " starts." );

        while( mKeepRunning ) {

            // motion event handler
            if ( mMotionEvent != null ) {
                ret = handleMotionEvent(mMotionEvent);
                mMotionEvent = null;

                if ( ret == MOTION_EVENT_ON_EXIT ) {
                    MainActivity activity = ((MainActivity)mContext);
                    activity.onClickBackExitKeyOutsideUiThread();
                }
            }

            // abstract move function
            mCurrentTime = System.currentTimeMillis();
            move();

            // abstract draw function
            draw();
        }

        release(); // abstract release function

        if ( mFinishActivityAtTheEnd ) {
            Log.v(TAG, "end of run with activity finish()");
            ((Activity)mContext).finish();
        }

        Log.i( TAG, "ThreadGame " + this + " finished." );

        mIsRunning = false;
    }

    abstract void move();
    abstract void draw();
    abstract int handleMotionEvent( MotionEvent event );

    // warning! this object is NOT reusable
    public void stopThread() {
        mKeepRunning = false;
    }

    public void stopThread( boolean finishActivity ) {
        if ( finishActivity ) {
            mFinishActivityAtTheEnd = true;
        }

        stopThread();
    }

    abstract void release();

    public boolean onTouchEvent( MotionEvent event ) {
        // actually not a good way to handle it, for example, if the onTouchEvent process speed
        // is faster than the screen refresh, it is possible that some MotionEvent be ignored.
        // i.e. twice onTouchEvent passed in one thread while-loop process.
        // But, hey, if such thing happens, no end-user will tolerant such low frame refreshing rate.
        mMotionEvent = event;
        return true;
    }
}
