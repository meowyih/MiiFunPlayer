package tw.com.miifun.miifunplayer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by yhorn on 2017/1/29.
 */

public class VideoEffectSurface extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "VideoEffectSurface";

    Context mContext;
    Config mConfig;
    SurfaceHolder mSurfaceHolder;
    int mCanvasHeight;
    int mCanvasWidth;
    ThreadGame mThread;

    boolean mRequestToFinishActivity;

    public VideoEffectSurface( Context context, Config config ) {
        super(context);
        mContext = context;
        mConfig = config;
        mSurfaceHolder = getHolder();
        mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT); // transparent background
        mSurfaceHolder.addCallback(this);
    }

    /*
     * SurfaceHolder.Callback
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.v( TAG, "surfaceChanged " + format + " " + " width " + width + " height " + height);
        mCanvasWidth = width;
        mCanvasHeight = height;

        if ( mRequestToFinishActivity) {
            Log.v(TAG, "surfaceChanged -no need to create game thread since Activity request to finish" );
            return;
        }

        if ( mThread != null ) {
            Log.w( TAG, "surfaceChanged - warning: existing another game thread, but it is ok if application is going to finish" );
            return;
            // mThread.stopThread();
        }

        if ( mConfig.vpcMediaPreparationTime > 0 &&
                mConfig.vpcMediaPreparationTime < Config.MIN_PREPARATION_TIME_FOR_LOADING_TEXT_IN_MS)
        {
            // no need to show "loading video"
            Log.i( TAG, "previous video loading time is " + mConfig.vpcMediaPreparationTime + "ms, skip loading hint." );
            return;
        }

        // draw loading text on screen if tread not ready yet
        Canvas canvas = mSurfaceHolder.lockCanvas();

        if ( canvas == null ) {
            Log.w(TAG, "mSurfaceHolder.lockCanvas() return null");
            return;
        }

        // clean canvas
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        // draw exit button
        String text = mContext.getResources().getString( R.string.video_loading_text );
        Paint paint = new Paint();
        paint.setTextSize(50);
        paint.setColor(Color.LTGRAY);
        paint.setTextAlign(Paint.Align.CENTER);
        int xPos = (width / 2);
        int yPos = (int) ((height / 2) - ((paint.descent() + paint.ascent()) / 2)) ;
        canvas.drawText(text, xPos, yPos, paint);

        // update the screen
        mSurfaceHolder.unlockCanvasAndPost(canvas);
    }

    private void assignEffectGameThread( TrackManager.Track track ) {
        if ( track == null ) {
            Log.w( TAG, "assignEffectGameThread, track is null" );
            return;
        }

        switch( track.getIconResId() ) {

            case R.drawable.c4:
                mThread = new EffectDragZoomOut(
                        mContext,
                        mSurfaceHolder,
                        mCanvasWidth,
                        mCanvasHeight,
                        EffectDragZoomOut.TYPE_BUTTERFLY );
                break;
            case R.drawable.c5:
                mThread = new EffectHoldDropping(
                        mContext,
                        mSurfaceHolder,
                        mCanvasWidth,
                        mCanvasHeight);
                break;

            case R.drawable.d2:
                mThread = new EffectDragZoomOut(
                        mContext,
                        mSurfaceHolder,
                        mCanvasWidth,
                        mCanvasHeight,
                        EffectDragZoomOut.TYPE_CHICKEN );
                break;

            case R.drawable.d3:
                mThread = new EffectHorizontalMoveOut(
                        mContext,
                        mSurfaceHolder,
                        mCanvasWidth,
                        mCanvasHeight,
                        EffectHorizontalMoveOut.TYPE_DOG );
                break;

            case R.drawable.d4:
                mThread = new EffectSizeSwing(
                        mContext,
                        mSurfaceHolder,
                        mCanvasWidth,
                        mCanvasHeight,
                        EffectSizeSwing.TYPE_SHEEP );
                break;

            case R.drawable.d6:
                mThread = new EffectSpreadOut(
                        mContext,
                        mSurfaceHolder,
                        mCanvasWidth,
                        mCanvasHeight,
                        EffectSpreadOut.TYPE_KUMA );
                break;

            case R.drawable.d9:
                mThread = new EffectDropping(
                        mContext,
                        mSurfaceHolder,
                        mCanvasWidth,
                        mCanvasHeight );
                break;

            case R.drawable.d10:
                mThread = new EffectHorizontalMoveOut(
                        mContext,
                        mSurfaceHolder,
                        mCanvasWidth,
                        mCanvasHeight,
                        EffectHorizontalMoveOut.TYPE_DOG );
                break;

            case R.drawable.f2:
                mThread = new EffectHorizontalMoveOut(
                        mContext,
                        mSurfaceHolder,
                        mCanvasWidth,
                        mCanvasHeight,
                        EffectHorizontalMoveOut.TYPE_BIRD );
                break;
            case R.drawable.f3:
                mThread = new EffectRaiseDrop(
                        mContext,
                        mSurfaceHolder,
                        mCanvasWidth,
                        mCanvasHeight );
                break;
            case R.drawable.f4:
                mThread = new EffectSpreadOut(
                        mContext,
                        mSurfaceHolder,
                        mCanvasWidth,
                        mCanvasHeight,
                        EffectSpreadOut.TYPE_DONKEY);
                break;
            case R.drawable.g2:
                mThread = new EffectSpreadOut(
                        mContext,
                        mSurfaceHolder,
                        mCanvasWidth,
                        mCanvasHeight,
                        EffectSpreadOut.TYPE_WHITE_CAT);
                break;
            case R.drawable.h2:
                mThread = new EffectSizeSwing(
                        mContext,
                        mSurfaceHolder,
                        mCanvasWidth,
                        mCanvasHeight,
                        EffectSizeSwing.TYPE_RABBIT);
                break;
            case R.drawable.i2:
                mThread = new EffectDragZoomOut(
                        mContext,
                        mSurfaceHolder,
                        mCanvasWidth,
                        mCanvasHeight,
                        EffectDragZoomOut.TYPE_BUTTERFLY );
                break;
            default:
                Log.w( TAG, "Unknown track, no effect was applied." );
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // Create MediaPlayer only after the SurfaceView has been created
        Log.v(TAG, "surfaceCreated");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        if ( mThread != null ) {
            mThread.stopThread();
            Log.v(TAG, "surfaceDestroyed - stop game thread");
        }
        else {
            Log.w( TAG, "surfaceDestroyed - no game thread to stop");
        }
        mThread = null;

        Log.v(TAG, "surfaceDestroyed");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                // Log.v( TAG, "ACTION_DOWN" );
                break;
            case MotionEvent.ACTION_MOVE:
                // Log.v( TAG, "ACTION_MOVE" );
                break;
            case MotionEvent.ACTION_UP:
                // Log.v( TAG, "ACTION_UP" );
                break;
            case MotionEvent.ACTION_CANCEL:
                // Log.v( TAG, "ACTION_CANCEL" );
                break;
            case MotionEvent.ACTION_OUTSIDE:
                // Log.v( TAG, "ACTION_OUTSIDE" );
                break;
            default:
        }

        if ( mThread != null )
            return mThread.onTouchEvent( event );
        else
            return false;
    }

    public void start( TrackManager.Track track ) {

        if ( mThread == null ) {
            assignEffectGameThread(track);
        }

        if ( mThread != null && ! mThread.isRunning() ) {
            mThread.start();
        }
    }

    public void stop() {

        if ( mThread != null ) {
            mThread.stopThread();
            mThread = null;
        }
    }

    public void safetyFinishActivity() {

        Log.v(TAG, "safetyFinishActivity");

        mRequestToFinishActivity = true;

        if ( mThread != null ) {
            mThread.stopThread(true);
            mThread = null;
        }
    }
}
