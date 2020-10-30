package tw.com.miifun.miifunplayer;

import android.content.Context;

/**
 * Created by yhorn on 2016/3/11.
 */
public class ItemZoomIn extends DrawableItem {

    final static private String appTag = "ItemZoomIn";
    final static private int DEFAULT_LIFE_INTERVAL = 3000;

    private int mLifeInterval;
    private int mStartWidth, mStartHeight;
    private int mStartX, mStartY;

    public ItemZoomIn(Context context, int x, int y, int width, int height, int canvasWidth, int canvasHeight ) {
        super( context, x, y, width, height, canvasWidth, canvasHeight );
        mLifeInterval = DEFAULT_LIFE_INTERVAL;
        mStartWidth = mWidth;
        mStartHeight = mHeight;
        mStartX = mX;
        mStartY = mY;
    }

    public void setLifeInterval( long ms ) {
        mLifeInterval = (int) ms;
    }

    @Override
    DrawableItemEvent move( long time ) {

        int interval = (int)( time - mStartTime );

        // end of life
        if ( interval >= mLifeInterval ) {
            return new DrawableItemEvent( DrawableItemEvent.DEAD, mX, mY );
        }
        else {
            mWidth = mStartWidth * ( mLifeInterval - interval ) / mLifeInterval;
            mHeight = mStartHeight * ( mLifeInterval - interval ) / mLifeInterval;
            mStartX = mX + ( mStartWidth - mWidth ) / 2;
            mStartY = mY + ( mStartHeight - mHeight ) / 2;
        }

        return new DrawableItemEvent( DrawableItemEvent.NONE, mX, mY );
    }
}
