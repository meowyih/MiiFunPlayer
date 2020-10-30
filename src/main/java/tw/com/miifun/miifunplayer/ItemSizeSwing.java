package tw.com.miifun.miifunplayer;

import android.content.Context;

/**
 * Created by yhorn on 2016/3/12.
 */
public class ItemSizeSwing extends DrawableItem {
    final static private String appTag = "ItemSizeSwing";

    final static private int SWING_INTERVAL = 1000;
    private int mCenterX, mCenterY;
    private int mMaxWidth, mMaxHeight;
    private int mMinWidth, mMinHeight;

    public ItemSizeSwing(Context context, int x, int y, int canvasWidth, int canvasHeight ) {
        super( context,
                x -  context.getResources().getDimensionPixelSize(R.dimen.effect_general_item_size) / 2,
                y -  context.getResources().getDimensionPixelSize(R.dimen.effect_general_item_size) / 2,
                context.getResources().getDimensionPixelSize(R.dimen.effect_general_item_size),
                context.getResources().getDimensionPixelSize(R.dimen.effect_general_item_size),
                canvasWidth,
                canvasHeight );

        mCenterX = x;
        mCenterY = y;

        mMaxWidth = mWidth;
        mMaxHeight = mHeight;
        mMinWidth = mWidth / 2;
        mMinHeight = mHeight / 2;
    }

    @Override
    DrawableItemEvent move( long time ) {
        DrawableItemEvent event = super.move(time);

        int interval = (int)(( time - mCreateTime ) % SWING_INTERVAL);

        if ( interval < SWING_INTERVAL / 2 ) {
            // decreasing size
            int w = mMinWidth + ( mMaxWidth - mMinWidth ) * ( SWING_INTERVAL / 2 - interval ) / ( SWING_INTERVAL / 2 );
            int h = mMinHeight + ( mMaxHeight - mMinHeight ) * ( SWING_INTERVAL / 2 - interval ) / ( SWING_INTERVAL / 2 );
            int x = mCenterX - w / 2;
            int y = mCenterY - h / 2;
            mX = x;
            mY = y;
            mWidth = w;
            mHeight = h;
        }
        else {
            // increasing size
            interval = interval - SWING_INTERVAL / 2;
            int w = mMaxWidth - ( mMaxWidth - mMinWidth ) * ( SWING_INTERVAL / 2 - interval ) / ( SWING_INTERVAL / 2 );
            int h = mMaxHeight - ( mMaxHeight - mMinHeight ) * ( SWING_INTERVAL / 2 - interval ) / ( SWING_INTERVAL / 2 );
            int x = mCenterX - w / 2;
            int y = mCenterY - h / 2;
            mX = x;
            mY = y;
            mWidth = w;
            mHeight = h;
        }

        return event;
    }
}
