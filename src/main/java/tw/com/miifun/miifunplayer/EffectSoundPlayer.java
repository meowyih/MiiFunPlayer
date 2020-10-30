package tw.com.miifun.miifunplayer;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.util.Log;

/**
 * Created by yhorn on 2016/2/17.
 */
public class EffectSoundPlayer {

    public interface EffectSoundPlayerListener {
        // callback when the playback is done
        void onEffectSoundFinish(EffectSoundPlayer mp, int result);
    }

    final static private String appTag = "EffectSoundPlayer";

    final static public int STOP = 100;
    final static public int CHILDREN_CHEERING = 101;
    final static public int SPLASH_PAGE = 102;
    final static public int CHILDREN_BOOING = 103;
    final static public int CHILDREN_AAAAAH = 104;

    final static public int RESULT_COMPLETE = 1000;
    final static public int RESULT_ERROR = 1001;

    final static private String ASSET_CHILDREN_CHEERING = "Children_Cheering.mp3";
    final static private String ASSET_SPLASH_PAGE = "splash_page_sound.mp3";
    final static private String ASSET_CHILDREN_BOOING = "Children_Boo.mp3";
    final static private String ASSET_CHILDREN_AAAAAH = "Children_Aaaaah.mp3";

    final static public int TYPE_ASSET = 300; // only support asset type for now

    private Context mContext;
    private MediaPlayer mMediaPlayer;
    private EffectSoundPlayerListener mListener;
    private boolean mLooping;

    public EffectSoundPlayer(Context context) {
        mContext = context;
    }

    public void setListsner( EffectSoundPlayerListener listener ) {
        mListener = listener;
    }

    public void playEffectSound( int type, final String filename ) {
        playEffectSound(type, filename, false);
    }

    public void playEffectSound(int type, final String filename, boolean looping ) {

        // Log.v( appTag, "playEffectSound " + type + " " + filename );

        mLooping = looping;

        if ( type == TYPE_ASSET ) {

            destroy();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        AssetFileDescriptor afd;
                        afd = mContext.getAssets().openFd( filename );

                        if (afd == null) {
                            Log.w(appTag, "playEffectSound cannot play sound due to bull afd");
                            if ( mListener != null )
                                mListener.onEffectSoundFinish(EffectSoundPlayer.this, RESULT_ERROR);
                            return;
                        }

                        mMediaPlayer = new MediaPlayer();
                        Log.v(appTag, "create effect mp " + mMediaPlayer);
                        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {

                                if ( ! mLooping ) {
                                    Log.v(appTag, "release effect mp " + mp);
                                    if (mListener != null) {
                                        mListener.onEffectSoundFinish(EffectSoundPlayer.this, RESULT_COMPLETE);
                                    }
                                    destroy();
                                }
                                else {
                                    Log.v(appTag, "looping");
                                    mMediaPlayer.seekTo(0);
                                    mMediaPlayer.start();
                                }
                            }
                        });

                        mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                        mMediaPlayer.prepare();
                        mMediaPlayer.start();
                    }
                    catch( Exception e ) {
                        Log.w(appTag, "Error, failed to playback effect sound: " + filename + " " + e.toString());
                        if ( mListener != null )
                            mListener.onEffectSoundFinish( EffectSoundPlayer.this, RESULT_ERROR );
                    }
                }
            }).start();
        }
        else {
            Log.w(appTag, "playEffectSound unknown type " + type);
        }
    }

    public void playEffectSound(final int name) {

        destroy();
        switch (name) {
            case STOP:
                destroy();
                return;
            case CHILDREN_CHEERING:
                playEffectSound( TYPE_ASSET, ASSET_CHILDREN_CHEERING );
                break;
            case SPLASH_PAGE:
                playEffectSound( TYPE_ASSET, ASSET_SPLASH_PAGE );
                break;
            case CHILDREN_AAAAAH:
                playEffectSound( TYPE_ASSET, ASSET_CHILDREN_AAAAAH );
                break;
            case CHILDREN_BOOING:
                playEffectSound( TYPE_ASSET, ASSET_CHILDREN_BOOING );
                break;
            default:
                Log.w(appTag, "Unknown effect name " + name);
                return;
        }

    }

    synchronized public void destroy() {
        if ( mMediaPlayer != null )
            mMediaPlayer.release();
        mMediaPlayer = null;
    }
}
