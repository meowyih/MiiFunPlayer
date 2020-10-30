package tw.com.miifun.miifunplayer;

/**
 * Created by yhorn on 2017/1/6.
 */

public interface PageController {

    // layout ID
    final static public int PAGE_NONE = 0;
    final static public int PAGE_CONTENT_MAIN = 1;
    final static public int PAGE_DOWNLOAD= 2;
    final static public int PAGE_VIDEO = 3;
    final static public int PAGE_SPLASH = 4;

    final static public String[] LAYOUT_NAME = {
            "UNKNOWN",
            "LAYOUT_CONTENT_MAIN",
            "LAYOUT_DOWNLOAD",
            "LAYOUT_VIDEO",
            "LAYOUT_SPLASH"
    };

    interface Listener {
        void onPageControllerStop(int id, Config config);
    }

    void start(Config config);
    void stop(boolean saveState);
    void setListener(Listener listener);
    void hide();
}

