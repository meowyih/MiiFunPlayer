package tw.com.miifun.miifunplayer;

/**
 * Created by yhorn on 2016/3/10.
 */
public class DrawableItemEvent {

    final static public int NONE = 0;
    final static public int DEAD = 1;
    final static public int DEAD_EXPLODED = 2;
    final static public int SPEED_UP = 3;
    final static public int SPEED_DOWN = 4;
    final static public int EXIT_APPLICATION = 999;

    int x;
    int y;
    int type;

    public DrawableItemEvent( int eventType, int xValue, int yValue ) {
        type = eventType;
        x = xValue;
        y = yValue;
    }
}
