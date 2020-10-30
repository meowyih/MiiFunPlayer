package tw.com.miifun.miifunplayer;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.util.Log;

/**
 * Created by yhorng on 2017/1/7.
 */

public class AssetAudioPlayer {

    final static private String TAG = "AssetAudioPlayer";

    final static public int RESULT_COMPLETE = 1000;
    final static public int RESULT_ERROR = 1001;
    final static public int RESULT_RESTART = 1002;

    public interface AssetAudioPlayerListener {
        // callback when the playback is done
        void onAssetAudioFinish(AssetAudioPlayer mp, int result);
    }

    private Context mContext;
    private MediaPlayer mMediaPlayer;
    private AssetAudioPlayerListener mListener;
    private boolean mLooping;

    public AssetAudioPlayer(Context context) {
        mContext = context;
    }

    public void setListsner( AssetAudioPlayerListener listener ) {
        mListener = listener;
    }

    public void play( final String filename, boolean looping ) {

        // destroy old player
        release();

        mLooping = looping;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AssetFileDescriptor afd;
                    afd = mContext.getAssets().openFd( filename );

                    if (afd == null) {
                        Log.w(TAG, "playEffectSound cannot play sound due to bull afd");
                        if ( mListener != null )
                            mListener.onAssetAudioFinish(AssetAudioPlayer.this, RESULT_ERROR);
                        return;
                    }

                    mMediaPlayer = new MediaPlayer();
                    Log.v(TAG, "create effect mp " + mMediaPlayer);
                    mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {

                            if ( ! mLooping ) {
                                Log.v(TAG, "release effect mp " + mp);
                                if (mListener != null) {
                                    mListener.onAssetAudioFinish(AssetAudioPlayer.this, RESULT_COMPLETE);
                                }
                                release();
                            }
                            else {
                                Log.v(TAG, "looping");
                                mMediaPlayer.seekTo(0);
                                mMediaPlayer.start();
                                if (mListener != null) {
                                    mListener.onAssetAudioFinish(AssetAudioPlayer.this, RESULT_RESTART );
                                }
                            }
                        }
                    });

                    mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();
                }
                catch( Exception e ) {
                    Log.w(TAG, "Error, failed to playback effect sound: " + filename + " " + e.toString());
                    if ( mListener != null )
                        mListener.onAssetAudioFinish( AssetAudioPlayer.this, RESULT_ERROR );
                }
            }
        }).start();
    }

    synchronized public void release() {
        if ( mMediaPlayer != null )
            mMediaPlayer.release();
        mMediaPlayer = null;
    }
}
