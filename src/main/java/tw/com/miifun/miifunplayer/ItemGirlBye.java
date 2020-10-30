package tw.com.miifun.miifunplayer;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by yhorn on 2016/3/12.
 */
public class ItemGirlBye extends DrawableItem {

    final static private String appTag = "ItemGirlBye";

    final static private int CHANGE_IMAGE_INTERVAL = 200;
    final static private int RAISE_INTERVAL = 500;
    final static private int IDLE_INTERVAL  = 1000;
    final static private int DROP_INTERVAL  = 500;

    final static private int STATE_CREATE = 0;
    final static private int STATE_RAISE = 1;
    final static private int STATE_IDLE = 2;
    final static private int STATE_DROP = 3;
    final static private int STATE_DEAD = 4;

    Bitmap mGirl01;
    Bitmap mGirl02;

    int mState;

    public ItemGirlBye(Context context, int canvasWidth, int canvasHeight ) {

        // w, h = context.getResources().getDimensionPixelSize(R.dimen.effect_girl_bye_item_size);

        super( context,
                canvasWidth / 2 -  context.getResources().getDimensionPixelSize(R.dimen.effect_girl_bye_item_size) / 2,
                canvasHeight,
                context.getResources().getDimensionPixelSize(R.dimen.effect_girl_bye_item_size),
                context.getResources().getDimensionPixelSize(R.dimen.effect_girl_bye_item_size),
                canvasWidth,
                canvasHeight );

        mGirl01 = BitmapWarehouse.getBitmap(mContext, R.drawable.girl01);
        mGirl02 = BitmapWarehouse.getBitmap(mContext, R.drawable.girl02);
        mState  = STATE_CREATE;
    }

    @Override
    DrawableItemEvent move( long time ) {
        DrawableItemEvent event = super.move( time );

        long interval = time - mCreateTime;

        if ( mState == STATE_CREATE ) {
            setDestination( mX, mCanvasHeight - mHeight, time, RAISE_INTERVAL );
            mState = STATE_RAISE;
            // Log.v( appTag, "change state to raise, interval " + interval );
        }
        else if ( mState == STATE_RAISE && interval >= RAISE_INTERVAL ) {
            mState = STATE_IDLE;
            // Log.v( appTag, "change state to idle, interval " + interval );
        }
        else if ( mState == STATE_IDLE && interval >= ( RAISE_INTERVAL + IDLE_INTERVAL )) {
            setDestination( mX, mCanvasHeight, time, DROP_INTERVAL );
            mState = STATE_DROP;
            // Log.v( appTag, "change state to drop, interval " + interval );
        }
        else if ( mState == STATE_DROP && interval >= ( RAISE_INTERVAL + IDLE_INTERVAL + DROP_INTERVAL )) {
            // Log.v( appTag, "change state to dead, interval " + interval );
            mState = STATE_DEAD;
            return new DrawableItemEvent( DrawableItemEvent.DEAD, mX, mY );
        }
        else if ( mState == STATE_DEAD ) {
            // Log.v( appTag, "state is dead, interval " + interval );
            return new DrawableItemEvent( DrawableItemEvent.DEAD, mX, mY );
        }

        return event;
    }

    @Override
    public Bitmap getCurrentBitmap() {
        long remain = ( mCurrentTime - mStartTime ) % ( CHANGE_IMAGE_INTERVAL * 2 );
        if ( remain < CHANGE_IMAGE_INTERVAL ) {
            return mGirl01;
        }
        else {
            return mGirl02;
        }
    }

    @Override
    public void destroy() {

        super.destroy();

        if ( mGirl01 != null ) {
            BitmapWarehouse.releaseBitmap(R.drawable.girl01);
            mGirl01 = null;
        }

        if ( mGirl02 != null ) {
            BitmapWarehouse.releaseBitmap(R.drawable.girl02);
            mGirl02 = null;
        }
    }
}
