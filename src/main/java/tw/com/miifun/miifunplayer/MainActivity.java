package tw.com.miifun.miifunplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.FragmentManager;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

/* LifeCycle
 * (1) When each page stop by themselves, they use Config object to pass information
 * (2) When application goes pause(), we stop all pages. Each page should store
 *     their own data into Config.
 * (3) When application goes onSaveInstanceState() after pause(), we just store Config into
 *     savedInstanceState.
 * (4) do not change layout page after savedInstanceState
 */
public class MainActivity extends AppCompatActivity implements PageController.Listener,
        ConfirmDialog.ConfirmDialogListener{

    final static private String TAG = "MainActivity";

    final static private int ID_PERMISSION_REQUEST_READ_STORAGE = 100;
    final static private int ID_PERMISSION_REQUEST_WRITE_STORAGE = 101;

    protected Config mConfig = new Config();
    protected SplashPageController mSplashPage;
    // protected DownloadPageController mDownloadPage;
    protected ContentPageController mContentPage;
    protected VideoPageController mVideoPage;

    protected ConfirmDialog mConfirmDialog;
    protected int mVersionCode = 0;
    protected int mExpansionVersionCode = 10605;
    protected int mExpansionFileSize = 96328685;

    private boolean savedInstanceStateDone = false;
    private TrackManager mTracks = new TrackManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        View rootView;

        super.onCreate(savedInstanceState);
        Log.i( TAG, "onCreate" );

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            mVersionCode = pInfo.versionCode;
        }
        catch ( Exception e ) {
            Log.e( TAG, "Fatal error: failed to get version code" );
        }

        // read saveInstanceState into mConfig
        if ( savedInstanceState != null ) {
            mConfig.gSaveTime = savedInstanceState.getLong( "gSaveTime" );
            mConfig.gSavePage = savedInstanceState.getInt( "gSavePage" );
            mConfig.gSaveTrackOrder = savedInstanceState.getInt( "gSaveTrackOrder" );
            mConfig.cpcScrollViewPosition = savedInstanceState.getInt( "cpcScrollViewPosition" );

            if ( mConfig.gSaveTrackOrder >= 0 ) {
                mConfig.cpcTrack = new TrackManager().getTrack( mConfig.gSaveTrackOrder );
            }

            mConfig.cpcMute = savedInstanceState.getBoolean( "cpcMute" );
            mConfig.cpcPlayAll = savedInstanceState.getBoolean( "cpcPlayAll" );

            mConfig.vpcVideoPosition = savedInstanceState.getInt( "vpcVideoPosition" );
            mConfig.vpcMediaPreparationTime = savedInstanceState.getLong( "vpcMediaPreparationTime" );

            Log.i( TAG, "savedInstanceState gSavePage:" + mConfig.gSavePage
                    + " gSaveTime:" + mConfig.gSaveTime
                    + " gSaveTrackOrder:" + mConfig.gSaveTrackOrder
                    + " cpcScrollViewPosition:" + mConfig.cpcScrollViewPosition
                    + " cpcPlayAll:" + mConfig.cpcPlayAll
                    + " cpcMute:" + mConfig.cpcMute
                    + " vpcVideoPosition" + mConfig.vpcVideoPosition );
        }

        // set to full screen and load the main layout
        onCreateSetFullScreen();
        setContentView(R.layout.activity_main);

        rootView = findViewById( R.id.activity_main );

        // create page controller
        mSplashPage = new SplashPageController( this, rootView );
        mSplashPage.setListener( this );

        mContentPage = new ContentPageController( this, rootView );
        mContentPage.setListener( this );

        mVideoPage = new VideoPageController( this, rootView );
        mVideoPage.setListener( this );

        // mDownloadPage = new DownloadPageController( this, rootView );
        // mDownloadPage.setListener( this );
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        long systemTime = System.currentTimeMillis();
        int page = mConfig.gSavePage;

        savedInstanceStateDone = true;

        if ( page == PageController.PAGE_DOWNLOAD ) {
            page = PageController.PAGE_NONE;
        }

        Log.i( TAG, "onSaveInstanceState gSavePage:" + page + " gSaveTime:" + systemTime );

        // global date, when we save state and the last page is which one
        savedInstanceState.putLong( "gSaveTime", systemTime );
        savedInstanceState.putInt( "gSavePage", page );
        savedInstanceState.putInt( "gSaveTrackOrder", mConfig.gSaveTrackOrder );

        // content page
        savedInstanceState.putInt( "cpcScrollViewPosition", mConfig.cpcScrollViewPosition );
        savedInstanceState.putBoolean( "cpcPlayAll", mConfig.cpcPlayAll );
        savedInstanceState.putBoolean( "cpcMute", mConfig.cpcMute );

        // video page
        if ( mConfig.cpcTrack != null ) {
            savedInstanceState.putString( "gTrackName", mConfig.cpcTrack.getFilename() );
        }

        savedInstanceState.putInt( "vpcVideoPosition", mConfig.vpcVideoPosition );
        savedInstanceState.putLong( "vpcMediaPreparationTime", mConfig.vpcMediaPreparationTime );

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onStart() {
        Log.i( TAG, "onStart" );
        savedInstanceStateDone = false;
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.i( TAG, "onResume, mConfig.gSavePage is " + mConfig.gSavePage +
                " " + PageController.LAYOUT_NAME[mConfig.gSavePage] );

        mConfig.gOnPausing = false;

        // Android seems to call onSaveInstanceState pause before the permission request and
        // resume it without onStart, we add this line to avoid that situation
        savedInstanceStateDone = false;

        // change layout to splash page
        if ( mConfig.gSavePage == PageController.PAGE_NONE ) {
            changeLayout(PageController.PAGE_SPLASH);
        }
        else {
            changeLayout(mConfig.gSavePage);
        }

        super.onResume();
    }

    @Override
    public void onPause() {
        Log.i( TAG, "onPause, current page " + PageController.LAYOUT_NAME[mConfig.gSavePage] );
        mConfig.gOnPausing = true;

        // stop all pages, each page will store data in shared Config
        if ( mConfig.gSavePage == PageController.PAGE_SPLASH ) {
            mSplashPage.stop(true);
        }
        else if ( mConfig.gSavePage == PageController.PAGE_DOWNLOAD ) {
            // mDownloadPage.stop(true);
        }
        else if ( mConfig.gSavePage == PageController.PAGE_CONTENT_MAIN ) {
            mContentPage.stop(true);
        }
        else if ( mConfig.gSavePage == PageController.PAGE_VIDEO ) {
            mVideoPage.stop(true);
        }

        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i( TAG, "onStop" );
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy");
        mContentPage.stop( false );
        mSplashPage.stop( false );
        mVideoPage.stop( false );
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.v(TAG, "onKeyDown KeyEvent.KEYCODE_BACK");
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onClickBackExitKeyOutsideUiThread() {
        if ( mConfig.gSavePage == PageController.PAGE_VIDEO ) {
            mConfig.cpcPlayAll = false; // not play all anymore
            mVideoPage.stop(false);
        }
        else if (mConfig.gSavePage == PageController.PAGE_CONTENT_MAIN ) {
            finish();
        }
    }

    private void onCreateSetFullScreen() {
        // keep the landscape orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // hide the nav bar and make it full screen
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        // This work only for android 4.4+
        getWindow().getDecorView().setSystemUiVisibility(flags);

        // Code below is to handle presses of Volume up or Volume down.
        // Without this, after pressing volume buttons, the navigation bar will
        // show up and won't hide
        final View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    decorView.setSystemUiVisibility(flags);
                }
            }
        });
    }

    private synchronized void changeLayout(int layoutId) {

        Log.i( TAG, "changeLayout " + PageController.LAYOUT_NAME[layoutId] );

        // hide all pages
        mSplashPage.hide();
        // mDownloadPage.hide();
        mContentPage.hide();
        mVideoPage.hide();

        if ( layoutId == PageController.PAGE_SPLASH ) {
            mConfig.gSavePage = PageController.PAGE_SPLASH;
            mSplashPage.start( mConfig );
        }
        else if ( layoutId == PageController.PAGE_DOWNLOAD ) {
            mConfig.gSavePage = PageController.PAGE_DOWNLOAD;
            // mDownloadPage.start( mConfig );
        }
        else if ( layoutId == PageController.PAGE_CONTENT_MAIN ) {
            mConfig.gSavePage = PageController.PAGE_CONTENT_MAIN;
            mContentPage.start( mConfig );
        }
        else if ( layoutId == PageController.PAGE_VIDEO ) {
            mConfig.gSavePage = PageController.PAGE_VIDEO;
            mVideoPage.start( mConfig );
        }
        else {
            Log.w( TAG, "changeLayout did nothing" );
        }
    }

    /*
     * Page Controller Callback
     */
    @Override
    public void onPageControllerStop( int id, Config config ) {
        Log.i( TAG, "onPageControllerStop id=" + id + " " + PageController.LAYOUT_NAME[id] );

        if ( mConfig.gOnPausing || savedInstanceStateDone ) {
            Log.i( TAG, "onPageControllerStop - no need to change layout due to on pausing, gOnPause:"
                    + mConfig.gOnPausing + " saveInstanceStateDone:" + savedInstanceStateDone );
            return;
        }

        if ( id == PageController.PAGE_SPLASH ) {

            // goto download page
            changeLayout( PageController.PAGE_CONTENT_MAIN );
        }
        else if ( id == PageController.PAGE_CONTENT_MAIN ) {
            if ( mConfig.cpcTrack == null ) {
                Log.e(TAG, "fatal error, no track from cpc");
            }
            else {
                Log.i( TAG, "cpc returns track " + mConfig.cpcTrack.getFilename() );
                changeLayout( PageController.PAGE_VIDEO );
            }
        }
        else if ( id == PageController.PAGE_VIDEO ) {

            if ( mConfig.cpcPlayAll ) {
                int nextTrackIdx = mTracks.getNextTrackPosition( mConfig.gSaveTrackOrder );
                mConfig.gSaveTrackOrder = nextTrackIdx;
                mConfig.cpcTrack = mTracks.getTrack( nextTrackIdx );

                if ( mConfig.cpcTrack == null ) {
                    Log.e( TAG, "mConfig.cpcTrack is null, position is " + nextTrackIdx );
                }

                mConfig.vpcVideoPosition = 0;
                changeLayout( PageController.PAGE_VIDEO );
            }
            else {
                changeLayout( PageController.PAGE_CONTENT_MAIN );
            }
        }

    }

    /*
     * Storage Permission
     */
    public void requestVideoPlaybackPermission() {

        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            Log.i( TAG, "shouldShowRequestPermissionRationale, show TYPE_EXPLAIN_STORAGE_PERMISSION dialog" );
            startConfirmDialog( ConfirmDialog.TYPE_EXPLAIN_WRITE_STORAGE_PERMISSION,
                    R.string.dialog_title_explain_write_permission_to_playback,
                    R.string.dialog_desc_explain_write_permission_to_playback );

        } else {

            // No explanation needed, we can request the permission.

            Log.i( TAG, "not shouldShowRequestPermissionRationale, request read permission" );
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    ID_PERMISSION_REQUEST_READ_STORAGE);
        }
    }

    public void requestExpansionDownloadPermission() {

        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            Log.i( TAG, "shouldShowRequestPermissionRationale, show TYPE_EXPLAIN_WRITE_STORAGE_PERMISSION dialog" );
            startConfirmDialog( ConfirmDialog.TYPE_EXPLAIN_WRITE_STORAGE_PERMISSION,
                    R.string.dialog_title_explain_write_permission_to_download,
                    R.string.dialog_desc_explain_write_permission_to_download );
        }
        else {

            // No explanation needed, we can request the permission.
            Log.i( TAG, "not shouldShowRequestPermissionRationale, request write permission" );
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    ID_PERMISSION_REQUEST_WRITE_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case ID_PERMISSION_REQUEST_READ_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.i( TAG, "onRequestPermissionsResult - read permission granted" );
                    changeLayout( PageController.PAGE_VIDEO );

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.i( TAG, "onRequestPermissionsResult - warning: read permission not granted" );
                    startConfirmDialog( ConfirmDialog.TYPE_NO_READ_STORAGE_PERMISSION,
                            R.string.dialog_title_no_write_permission_to_playback,
                            R.string.dialog_desc_no_write_permission_to_playback );
                }
                return;
            }

            case ID_PERMISSION_REQUEST_WRITE_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.i( TAG, "onRequestPermissionsResult - write permission granted" );
                    changeLayout( PageController.PAGE_DOWNLOAD );

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.i( TAG, "onRequestPermissionsResult - warning: write permission not granted" );
                    startConfirmDialog( ConfirmDialog.TYPE_NO_WRITE_STORAGE_PERMISSION,
                            R.string.dialog_title_no_write_permission_to_download,
                            R.string.dialog_desc_no_write_permission_to_download );
                }
                return;
            }
        }
    }

    /*
     * Confirm Dialog
     */
    public void startConfirmDialog(int dialogType, int titleResId, int descResId) {

        if ( savedInstanceStateDone ) {
            Log.w( TAG, "startConfirmDialog - warning: savedInstanceStateDone, skip launching dialog" );
            return;
        }


        if (mConfirmDialog == null) {
            // it is annoying to show all error dialog for each track
            final FragmentManager fm = getFragmentManager();
            mConfirmDialog = new ConfirmDialog();
            Bundle bundle = new Bundle();
            bundle.putInt(ConfirmDialog.PARAM_DIALOG_TYPE, dialogType);
            bundle.putString(ConfirmDialog.PARAM_DIALOG_TITLE, getResources().getString(titleResId));
            bundle.putString(ConfirmDialog.PARAM_DIALOG_DESC, getResources().getString(descResId));

            mConfirmDialog.setArguments(bundle);
            mConfirmDialog.show(fm, "startConfirmDialog");
        } else {
            Log.w(TAG, "warning, cannot display dialog since it exists " + mConfirmDialog);
        }
    }

    /*
     * ConfirmDialog.ConfirmDialogListener
     */
    @Override
    public void onFinishConfirmDialog(int type, int result) {
        Log.v(TAG, "onFinishConfirmDialog " + type + " " + result);
        if (type == ConfirmDialog.TYPE_EXIT) {
            if (result == ConfirmDialog.POSITIVE) {
                Log.i( TAG, "finish for ConfirmDialog.TYPE_EXIT/POSITIVE" );
                finish();
            }
        }
        else if ( type == ConfirmDialog.TYPE_EXPLAIN_READ_STORAGE_PERMISSION) {
            // after explain, request the read permission again
            Log.i( TAG, "request read permission for ConfirmDialog.TYPE_EXPLAIN_READ_STORAGE_PERMISSION" );
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    ID_PERMISSION_REQUEST_READ_STORAGE);
        }
        else if ( type == ConfirmDialog.TYPE_EXPLAIN_WRITE_STORAGE_PERMISSION ) {
            // after explain, request the write permission again
            Log.i( TAG, "request read permission for ConfirmDialog.TYPE_EXPLAIN_WRITE_STORAGE_PERMISSION" );
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    ID_PERMISSION_REQUEST_WRITE_STORAGE);
        }
        else if ( type == ConfirmDialog.TYPE_NO_READ_STORAGE_PERMISSION ) {
            Log.i( TAG, "finish for ConfirmDialog.TYPE_NO_READ_STORAGE_PERMISSION" );
            finish();
        }
        else if ( type == ConfirmDialog.TYPE_NO_WRITE_STORAGE_PERMISSION ) {
            Log.i( TAG, "finish for ConfirmDialog.TYPE_NO_READ_STORAGE_PERMISSION" );
            finish();
        }

        mConfirmDialog = null;
    }
}