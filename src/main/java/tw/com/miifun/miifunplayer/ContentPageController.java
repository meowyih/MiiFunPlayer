package tw.com.miifun.miifunplayer;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by yhorn on 2017/1/7.
 */

public class ContentPageController implements PageController {

    final static private String TAG = "ContentPageController";
    final static private String BGM_FILENAME = "Marimba_Melody.mp3";

    Activity mContext;
    // View in content_main.xml
    RelativeLayout mRlContentMain, mRlOuterContent;
    ScrollView mSvSong;
    LinearLayout mLlSong;
    ImageView mIvContentCatLogo;
    TextView mTvVersion;
    Button mBtnPlayAll;
    Button mBtnExit;
    ImageView mIvMute;
    Handler mHandler;
    AssetAudioPlayer mBgmPlayer;

    TrackManager mTrackManager;
    HashMap<View, TrackManager.Track> mTrackButtonMapping = new HashMap<>();

    private PageController.Listener mListener;
    private Config mConfig;
    private ThreadContentAnimation mThreadAnimation;

    int mNumberOfSongIconInRow;

    public ContentPageController(Activity context,View parent) {

        MainActivity activity = (MainActivity) context;
        mContext = context;

        mRlContentMain = (RelativeLayout) parent.findViewById( R.id.rl_content_main );
        mRlOuterContent = (RelativeLayout) parent.findViewById( R.id.rl_content_outer );
        mHandler = new Handler(context.getMainLooper());

        mSvSong = (ScrollView) parent.findViewById( R.id.sv_content_song );
        mLlSong = (LinearLayout) parent.findViewById( R.id.ll_content_song );

        mBtnExit = (Button) parent.findViewById( R.id.btn_content_exit );
        mBtnExit.setOnClickListener( new buttonOnClickHandler() );

        mTvVersion = (TextView) parent.findViewById( R.id.tv_content_version );
        mTvVersion.setText( "version " + activity.mVersionCode );

        // get the list of the track
        mTrackManager = new TrackManager();

        // create bgm
        mBgmPlayer = new AssetAudioPlayer( mContext );
        mBgmPlayer.setListsner( new BgmPlayerCallback() );

        // thred animation
        mThreadAnimation = new ThreadContentAnimation( mContext, mIvContentCatLogo );
    }

    @Override
    public void start( Config config ) {
        Log.i( TAG, "start(), playbgm" );
        mConfig = config;
        mConfig.cpcTrack = null;
        mHandler.post( new Runnable() {
            @Override public void run() {
                mRlContentMain.setVisibility( View.VISIBLE );
                createViews();

                mThreadAnimation.setStopFlag();
                mThreadAnimation = new ThreadContentAnimation( mContext, mIvContentCatLogo );
                mThreadAnimation.start();
            }
        });

        mBgmPlayer.play( BGM_FILENAME, true );
    }

    // this function should be called in UI thread, and cannot be in onCreate() in Activity
    private void createViews() {
        LinearLayout ll;
        LinearLayout.LayoutParams llparams;
        LinearLayout llImage;
        LinearLayout.LayoutParams llImageParams;
        ArrayList<TrackManager.Track> tracks = mTrackManager.getTracks();

        // remove all views in song list and reset the view to track mapping table
        mLlSong.removeAllViews();
        mTrackButtonMapping.clear();

        // calculate screen size information
        // get real screen width without status/nav bar
        int screenWidthPixel = mContext.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getWidth();

        if ( screenWidthPixel == 0 ) {
            Log.w( TAG, "createViews - screen width is 0, the render is not ready yet." );
            return;
        }
        else {
            Log.i( TAG, "screen width " + screenWidthPixel );
        }

        // get song icon outer size (i.e. include padding) in pixel
        float songIconOuterSizePixel = mContext.getResources().getDimension(R.dimen.content_main_song_icon_outer_size);

        // get logo icon outer size (i.e. include padding) in pixel
        float logoIconOuterSizePixel = mContext.getResources().getDimension(R.dimen.content_main_logo_icon_outer_size);

        // get icon button padding size
        float buttonMarginSizePixel = mContext.getResources().getDimension(R.dimen.content_main_logo_icon_button_margin );

        // calculate number of icons that fit in one line
        mNumberOfSongIconInRow = (int) ((screenWidthPixel) / songIconOuterSizePixel);

        // calculate the real horizontal padding for1 rl_content_main
        int rlContentPaddingHorizontal = (int) ((screenWidthPixel -
                songIconOuterSizePixel * mNumberOfSongIconInRow ) / 2);

        int rlContentVerticalPadding = mRlOuterContent.getPaddingTop();
        mRlOuterContent.setPadding(rlContentPaddingHorizontal, rlContentVerticalPadding,
                rlContentPaddingHorizontal, rlContentVerticalPadding);

        // add cat log
        ll = new LinearLayout( mContext );
        llparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (int) logoIconOuterSizePixel);
        ll.setLayoutParams(llparams);
        ll.setOrientation(LinearLayout.HORIZONTAL);

        llImage = new LinearLayout( mContext );
        llImageParams = new LinearLayout.LayoutParams(
                (int) logoIconOuterSizePixel,
                (int) logoIconOuterSizePixel);
        llImage.setLayoutParams(llImageParams);

        mIvContentCatLogo = new ImageView( mContext );
        mIvContentCatLogo.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        mIvContentCatLogo.setImageResource(R.drawable.main_cat_left);

        llImage.addView(mIvContentCatLogo);
        ll.addView(llImage);

        // add playall button
        mBtnPlayAll = new Button( mContext );

        llparams = new LinearLayout.LayoutParams(
                (int) (logoIconOuterSizePixel - buttonMarginSizePixel * 2),
                (int) (logoIconOuterSizePixel - buttonMarginSizePixel * 2));
        llparams.setMargins(
                (int) buttonMarginSizePixel,
                (int) buttonMarginSizePixel,
                (int) buttonMarginSizePixel,
                (int) buttonMarginSizePixel );
        mBtnPlayAll.setLayoutParams( llparams );
        mBtnPlayAll.setBackgroundResource( R.drawable.btn_play_all);
        mBtnPlayAll.setOnClickListener( new buttonOnClickHandler() );
        ll.addView(mBtnPlayAll);

        // add audio button
        mConfig.cpcMute = false;
        mIvMute = new ImageView( mContext );

        llparams = new LinearLayout.LayoutParams(
                (int) (logoIconOuterSizePixel - buttonMarginSizePixel * 2),
                (int) (logoIconOuterSizePixel - buttonMarginSizePixel * 2));
        llparams.setMargins(
                (int) buttonMarginSizePixel,
                (int) buttonMarginSizePixel,
                (int) buttonMarginSizePixel,
                (int) buttonMarginSizePixel );
        mIvMute.setLayoutParams( llparams );
        mIvMute.setBackgroundResource( R.drawable.icon_mute_off );
        mIvMute.setOnClickListener( new buttonOnClickHandler() );
        ll.addView(mIvMute);

        // add the first line into song layout
        mLlSong.addView(ll);

        // add the track icon into layout
        ll = null;
        for (int i = 0; i < tracks.size(); i++) {
            if (i == 0 || i % mNumberOfSongIconInRow == 0) {
                // new line, if the previous line contains something, add to song layout
                if (ll != null)
                    mLlSong.addView(ll);

                // create new linear layout
                ll = new LinearLayout( mContext );
                llparams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        (int) songIconOuterSizePixel);
                ll.setLayoutParams(llparams);
                ll.setOrientation(LinearLayout.HORIZONTAL);
            }

            TrackManager.Track track = tracks.get(i);
            llImage = new LinearLayout( mContext );
            llImageParams = new LinearLayout.LayoutParams(
                    (int) songIconOuterSizePixel,
                    (int) songIconOuterSizePixel);
            llImage.setLayoutParams(llImageParams);

            ImageView iv = new ImageView( mContext );
            iv.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));

            iv.setPadding(
                    (int) mContext.getResources().getDimension(R.dimen.content_main_song_icon_padding),
                    (int) mContext.getResources().getDimension(R.dimen.content_main_song_icon_padding),
                    (int) mContext.getResources().getDimension(R.dimen.content_main_song_icon_padding),
                    (int) mContext.getResources().getDimension(R.dimen.content_main_song_icon_padding));

            iv.setImageResource(track.getIconResId());
            iv.setClickable(true);

            mTrackButtonMapping.put(iv, track);
            iv.setOnClickListener( new buttonOnClickHandler() );

            llImage.addView(iv);
            ll.addView(llImage);
        }

        // add last one line
        if (ll != null)
            mLlSong.addView(ll);

        // set the scroll view position
        mSvSong.setVerticalScrollbarPosition( mConfig.cpcScrollViewPosition );
    }

    @Override
    public void stop( boolean saveState ) {
        Log.i( TAG, "stop saveState=" + saveState );
        if ( mSvSong != null && mConfig != null && saveState ) {
            mConfig.cpcScrollViewPosition = mSvSong.getVerticalScrollbarPosition();
        }

        mThreadAnimation.setStopFlag();
        mBgmPlayer.release();
    }

    @Override
    public void setListener( Listener listener ) {
        mListener = listener;
    }

    private void release() {
        mThreadAnimation.setStopFlag();
        mConfig.cpcScrollViewPosition = mSvSong.getVerticalScrollbarPosition();
        mBgmPlayer.release();
        if ( mListener != null ) {
            mListener.onPageControllerStop( PageController.PAGE_CONTENT_MAIN, mConfig );
        }
    }

    @Override
    public void hide() {
        mHandler.post( new Runnable() {
            @Override
            public void run() {
                mRlContentMain.setVisibility( View.INVISIBLE );
            }
        });
    }

    private class buttonOnClickHandler implements View.OnClickListener {

        @Override
        public void onClick( View view ) {
            Log.i( TAG, "onClick " + view );

            if ( mTrackButtonMapping.containsKey( view )) {
                // click the track icon
                mConfig.cpcPlayAll = false;
                TrackManager.Track track = mTrackButtonMapping.get(view);
                mConfig.cpcTrack = track;
                mConfig.gSaveTrackOrder = mTrackManager.getOrder( track );
                release();
            }
            else if ( view == mBtnPlayAll ) {
                mConfig.cpcPlayAll = true;
                mConfig.cpcTrack = mTrackManager.getTrack(0);
                mConfig.gSaveTrackOrder = 0;
                release();
            }
            else if ( view == mIvMute ) {
                if ( mConfig.cpcMute ) {
                    mConfig.cpcMute = false;
                    mIvMute.setImageResource( R.drawable.icon_mute_off );
                    mBgmPlayer.release();
                    mBgmPlayer.play( BGM_FILENAME, true );
                    mThreadAnimation.setStopFlag();
                    mThreadAnimation = new ThreadContentAnimation( mContext, mIvContentCatLogo );
                    mThreadAnimation.start();
                }
                else {
                    mConfig.cpcMute = true;
                    mIvMute.setImageResource( R.drawable.icon_mute_on );
                    mBgmPlayer.release();
                }
            }
            else if ( view == mBtnExit ) {
                // ContentPageController.this.stop( true );
                // show confirm dialog
                ((MainActivity)mContext).startConfirmDialog(ConfirmDialog.TYPE_EXIT, R.string.dialog_title_exit, R.string.dialog_desc_exit);
            }
        }
    }

    class BgmPlayerCallback implements AssetAudioPlayer.AssetAudioPlayerListener {

        @Override
        public void onAssetAudioFinish(AssetAudioPlayer mp, int result) {
            if ( result == AssetAudioPlayer.RESULT_RESTART ) {
                Log.i( TAG, "onAssetAudioFinish RESULT_RESTART, restart the animation " );
                mThreadAnimation.setStopFlag();
                mThreadAnimation = new ThreadContentAnimation( mContext, mIvContentCatLogo );
                mThreadAnimation.start();
            }
        }
    }
}
