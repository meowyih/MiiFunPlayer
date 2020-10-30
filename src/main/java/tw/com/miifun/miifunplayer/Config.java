package tw.com.miifun.miifunplayer;

/**
 * Created by yhorn on 2017/1/6.
 */

public class Config {

    // shows "loading video" hint if the preparation time is too long
    final static public long MIN_PREPARATION_TIME_FOR_LOADING_TEXT_IN_MS = 1000;

    // global
    long gSaveTime = 0; // previous save time system time in millisecond
    int gSavePage = 0; // previous save page defined in PageController
    int gSaveTrackOrder = 0; // previous save track name defined in TrackManager
    boolean gOnPausing = false;

    // from ContentPageController
    TrackManager.Track cpcTrack = null;
    int cpcScrollViewPosition = 0;
    boolean cpcPlayAll = false;
    boolean cpcMute = false;

    // from VideoPageController
    int vpcVideoPosition = 0;
    long vpcMediaPreparationTime = 0;

}