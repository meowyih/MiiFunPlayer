package tw.com.miifun.miifunplayer;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.io.IOException;

/**
 * Created by yhorng on 2017/1/7.
 */

public class VideoPageController implements PageController {

    final static private String TAG = "VideoPageController";

    // View in video.xml
    RelativeLayout mRlVideo;
    SurfaceView mSvVideo;
    SurfaceHolder mSvVideoHolder;
    VideoEffectSurface mSvEffect;

    Config mConfig;

    Context mContext;

    MediaPlayer mMediaPlayer;
    Handler mHandler;

    PageController.Listener mListener = null;

    long mPrepareMediaStartTime;

    public VideoPageController( Context context, View parent ) {
        mContext = context;
        mRlVideo = (RelativeLayout) parent.findViewById( R.id.rl_video );
        mHandler = new Handler(context.getMainLooper());
    }

    private void createView() {

        // remove previous video surface view
        if (mSvVideo != null) {
            mRlVideo.removeView(mSvVideo);
        }

        if ( mSvEffect != null ) {
           mRlVideo.removeView(mSvEffect);
        }

        // create video SurfaceView
        mSvVideo = new SurfaceView( mContext );
        ViewGroup.LayoutParams param = mSvVideo.getLayoutParams();

        if (param == null) {
            param = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            param.width = ViewGroup.LayoutParams.MATCH_PARENT;
            param.height = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        mSvVideo.setLayoutParams(param);
        mSvVideo.setZOrderMediaOverlay(false);

        mRlVideo.addView(mSvVideo);
        mSvVideoHolder = mSvVideo.getHolder();
        mSvVideoHolder.addCallback( new SvHolderCallback() );

        // create effect SurfaceView
        mSvEffect = new VideoEffectSurface( mContext, mConfig );
        param = mSvEffect.getLayoutParams();

        if (param == null) {
            param = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            param.width = ViewGroup.LayoutParams.MATCH_PARENT;
            param.height = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        mSvEffect.setLayoutParams(param);
        mSvEffect.setZOrderMediaOverlay(true);
        mRlVideo.addView(mSvEffect);
    }

    @Override
    public void start( Config config ) {
        mConfig = config;
        mHandler.post( new Runnable() {
            @Override public void run() {
                mRlVideo.setVisibility( View.VISIBLE );
                createView();
            }
        });
    }

    @Override
    public void stop( boolean saveState ) {

        Log.i( TAG, "stop saveState=" + saveState );

        if ( mConfig != null ) {
            if ( saveState && mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mConfig.vpcVideoPosition = mMediaPlayer.getCurrentPosition();
            } else {
                mConfig.vpcVideoPosition = 0;
            }
        }

        if ( mMediaPlayer != null ) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        if ( mSvEffect != null ) {
            mSvEffect.stop();
        }

        if ( mListener != null ) {
            mListener.onPageControllerStop( PageController.PAGE_VIDEO, mConfig );
        }
    }

    @Override
    public void setListener( Listener listener ) {
        mListener = listener;
    }

    @Override
    public void hide() {
        mHandler.post( new Runnable() {
            @Override
            public void run() {
                mRlVideo.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void preparingAssetVideo(TrackManager.Track track) {
        try {
            AssetFileDescriptor descriptor = null;

            if ( track == null ) {
                // no video should be prepared
                return;
            }

            if (track.getStorageType() == TrackManager.STORAGE_TYPE_ASSET) {
                Log.i( TAG, "preparingAssetVideo - Opening Asset " + track.getFilename() );
                descriptor = mContext.getAssets().openFd(track.getFilename());
                // descriptor = mContext.getResources().openRawResourceFd( R.raw.t001 );
            } else if (track.getStorageType() == TrackManager.STORAGE_TYPE_EXPANSION_ZIP) {
                MainActivity activity = (MainActivity)mContext;
                Log.e( TAG, "TODO: rewrite the zip file read for expansion asset" );
                // ZipResourceFile expansionFile = APKExpansionSupport.getAPKExpansionZipFile(
                //         mContext, activity.mExpansionVersionCode, 0);
                // descriptor = expansionFile.getAssetFileDescriptor(track.getFilename());
            }
            long start = descriptor.getStartOffset();
            long end = descriptor.getLength();

            if (mMediaPlayer != null) {
                mMediaPlayer.release();
                mMediaPlayer = null;
            }

            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDisplay(mSvVideoHolder);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(descriptor.getFileDescriptor(), start, end);

            MediaPlayerListener mediaPlayerListener = new MediaPlayerListener();

            mMediaPlayer.setOnPreparedListener(mediaPlayerListener);
            mMediaPlayer.setOnErrorListener(mediaPlayerListener);
            mMediaPlayer.setOnCompletionListener(mediaPlayerListener);
            mMediaPlayer.prepareAsync();
            mPrepareMediaStartTime = System.currentTimeMillis();
        } catch (IOException e) {
            // failed to playback video
            // if (!mIsPlayingAll) {
            //     startConfirmDialog(ConfirmDialog.TYPE_ERROR, R.string.dialog_title_error_track_not_prepared, R.string.dialog_desc_error_track_not_prepared);
            // }
            Log.e(TAG, "failed to prepare media player " + e.toString());
        }
    }

    class MediaPlayerListener implements MediaPlayer.OnPreparedListener,
            MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

        /*
     * MediaPlayer.OnPreparedListener
     */
        @Override
        public void onPrepared(MediaPlayer player) {
            Log.v(TAG, "MediaPlayer.onPrepared");
            mConfig.vpcMediaPreparationTime = System.currentTimeMillis() - mPrepareMediaStartTime;
            mMediaPlayer.seekTo( mConfig.vpcVideoPosition );
            mMediaPlayer.start();

            // start the effect
            mSvEffect.start( mConfig.cpcTrack );
        }

        /*
         * MediaPlayer.OnErrorListener
         */
        @Override
        public boolean onError(MediaPlayer player, int what, int extra) {
            Log.e(TAG, "MediaPlayer.onError " + what + " " + extra);

            // if (!mIsPlayingAll) {
            //    startConfirmDialog(ConfirmDialog.TYPE_ERROR, R.string.dialog_title_error_track_not_playable, R.string.dialog_desc_error_track_not_playable);
            // }

            if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
                Log.e(TAG, "MediaPlayer.onError MEDIA_ERROR_SERVER_DIED back to content layout");
                // changeLayout(LAYOUT_CONTENT_MAIN);
                if ( mListener != null) {
                    mListener.onPageControllerStop(PageController.PAGE_VIDEO, mConfig);
                }
                return true;
            } else if (what != MediaPlayer.MEDIA_ERROR_UNKNOWN) {
                Log.w(TAG, "MediaPlayer.onError un-document 'what' code, don't handle it");
                return false; // un-document error, just skip it
            }

            // 'what' is MediaPlayer.MEDIA_ERROR_UNKNOWN
            switch (extra) {
                case MediaPlayer.MEDIA_ERROR_IO:
                    Log.e(TAG, "MediaPlayer.onError extra code: MEDIA_ERROR_IO");
                    break;
                case MediaPlayer.MEDIA_ERROR_MALFORMED:
                    Log.e(TAG, "MediaPlayer.onError extra code: MEDIA_ERROR_MALFORMED");
                    break;
                case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                    Log.e(TAG, "MediaPlayer.onError exstra code: MEDIA_ERROR_UNSUPPORTED");
                    break;
                case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                    Log.e(TAG, "MediaPlayer.onError extra code: MEDIA_ERROR_TIMED_OUT");
                    break;
                case -2147483648:
                    Log.e(TAG, "MediaPlayer.onError extra code: -2147483648, low-level-system error");
                    break;
                default:
                    Log.w(TAG, "MediaPlayer.onError undocument 'extra' (" + extra +
                            ") code, consumed and ignore it");
                    return true;
            }
            return false;
        }

        /*
         * MediaPlayer.OnCompletionListener
         */
        @Override
        public void onCompletion(MediaPlayer player) {
            if ( mListener != null) {
                mListener.onPageControllerStop(PageController.PAGE_VIDEO, mConfig);
            }
            Log.v(TAG, "MediaPlayer.onCompletion");
        }
    }

    class SvHolderCallback implements SurfaceHolder.Callback {
        /*
         * SurfaceHolder.Callback
         */
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.v(TAG, "surfaceChanged " + format + " " + " width " + width + " height " + height);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {

            // Create MediaPlayer only after the SurfaceView has been created
            Log.v(TAG, "surfaceCreated");
            preparingAssetVideo( mConfig.cpcTrack );
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.v(TAG, "surfaceDestroyed");
        }
    }
}
