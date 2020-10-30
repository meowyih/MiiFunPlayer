package tw.com.miifun.miifunplayer;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by yhorn on 2017/1/6.
 */

public class SplashPageController implements PageController {

    private static final String TAG = "SplashPageController";

    private RelativeLayout mRlSplashPage;
    private ImageView mIvSplashCat;
    private ImageView mIvSplashTextLogo;
    private Context mContext;
    private PageController.Listener mListener;
    private Config mConfig;
    private SplashThread mThread;
    private AssetAudioPlayer mAudioPlayer;
    Handler mHandler;

    // parentView is the root view contains splash_page.xml layout
    public SplashPageController( Context context, View parentView ) {
        // View in splash_page.xml
        mRlSplashPage = (RelativeLayout) parentView.findViewById(R.id.rl_splash_page);
        mIvSplashCat = (ImageView) parentView.findViewById(R.id.iv_splash_cat);
        mIvSplashTextLogo = (ImageView) parentView.findViewById(R.id.iv_splash_text_logo);
        mContext = context;
        mAudioPlayer = new AssetAudioPlayer( context );
        mHandler = new Handler(mContext.getMainLooper());
    }

    @Override
    public void start( Config config ) {
        mConfig = config;
        mThread = new SplashThread();
        mThread.start();
        mAudioPlayer.play( "splash_page_sound.mp3", false );

        mHandler.post( new Runnable() {
            @Override public void run() {
                mRlSplashPage.setVisibility( View.VISIBLE );
            }
        });
    }

    @Override
    public void setListener( PageController.Listener listener ) {
        mListener = listener;
    }

    private void release() {
        mAudioPlayer.release();
        if ( mListener != null ) {
            mListener.onPageControllerStop( PageController.PAGE_SPLASH, mConfig );
        }
    }

    @Override
    public void stop( boolean saveState ) {
        if ( mThread != null ) {
            // should not happen
            mThread.setStopFlag();
        }
    }

    @Override
    public void hide() {
        mHandler.post( new Runnable() {
            @Override public void run() {
                mRlSplashPage.setVisibility( View.INVISIBLE );
            }
        });
    }


    class SplashThread extends Thread {

        long mStartTime;
        int mCurrentCatResId;
        boolean mRunning = true;

        private void updateImageInUiThread() {
            mHandler.post( new Runnable() {
                @Override public void run() {
                    mIvSplashCat.setImageResource( mCurrentCatResId );
                }
            });
        }

        private void startLogoAnimationInUiThread() {
            mHandler.post( new Runnable() {
                @Override public void run() {
                    mIvSplashTextLogo.setImageResource(R.drawable.logo_miifun);
                    mIvSplashTextLogo.startAnimation(
                            AnimationUtils.loadAnimation(
                                    mContext.getApplicationContext(), R.anim.bounce)
                    );
                }
            });
        }

        public void setStopFlag() {
            mRunning = false;
        }

        @Override
        public void run() {

            mStartTime = System.currentTimeMillis();
            mCurrentCatResId = R.drawable.splash_cat_left;
            updateImageInUiThread();

            Log.i( TAG, "enter thread run" );

            while( mRunning ) {
                long diff = System.currentTimeMillis() - mStartTime;

                if ( diff >= 3000 ) {
                    // on stop
                    SplashPageController.this.stop( false );
                    Log.i( TAG, "stop diff:" + diff + " thread:" + this );
                }
                else if ( diff < 3000 && diff >= 1600 && mCurrentCatResId != R.drawable.splash_cat_middle ) {
                    // show the last cat image and bounce the text logo
                    mCurrentCatResId = R.drawable.splash_cat_middle;
                    updateImageInUiThread();
                    startLogoAnimationInUiThread();
                    Log.i( TAG, "change middle/logo, diff:" + diff + " thread:" + this );
                }
                else if ( diff < 1600 && diff >= 600 && mCurrentCatResId != R.drawable.splash_cat_right ) {
                    mCurrentCatResId = R.drawable.splash_cat_right;
                    updateImageInUiThread();
                    Log.i( TAG, "change right, diff:" + diff + " thread:" + this );
                }
                else if ( diff < 600 && diff >= 400 && mCurrentCatResId != R.drawable.splash_cat_left ) {
                    mCurrentCatResId = R.drawable.splash_cat_left;
                    updateImageInUiThread();
                    Log.i( TAG, "change left, diff:" + diff + " thread:" + this );
                }
                else if ( diff < 400 && diff >= 200 && mCurrentCatResId != R.drawable.splash_cat_right ) {
                    mCurrentCatResId = R.drawable.splash_cat_right;
                    updateImageInUiThread();
                    Log.i( TAG, "change right, diff:" + diff + " thread:" + this );
                }
            }

            // release resource and inform caller the splash page is stopped
            release();

            Log.i( TAG, "leave thread run" );
        }
    }
}
