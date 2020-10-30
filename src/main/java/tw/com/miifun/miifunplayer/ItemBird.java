package tw.com.miifun.miifunplayer;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by yhorn on 2016/3/11.
 */
public class ItemBird extends DrawableItem {

    final static private String appTag = "ItemBird";
    final static private int CHANGE_IMAGE_INTERVAL = 200;

    Bitmap mBird01;
    Bitmap mBird02;

    boolean mRightToLeft;

    public static int getWidth( Context context ) {
        return context.getResources().getDimensionPixelSize( R.dimen.effect_general_item_size );
    }

    public static int getHeight( Context context ) {
        return context.getResources().getDimensionPixelSize( R.dimen.effect_general_item_size );
    }

    public ItemBird(Context context, int x, int y, int canvasWidth, int canvasHeight, boolean rightToLeft) {

        super( context,
                x,
                y,
                getWidth(context),
                getHeight( context ),
                canvasWidth,
                canvasHeight );

        mRightToLeft = rightToLeft;

        if ( mRightToLeft ) {
            mBird01 = BitmapWarehouse.getBitmap(mContext, R.drawable.bird01);
            mBird02 = BitmapWarehouse.getBitmap(mContext, R.drawable.bird02);
        }
        else {
            mBird01 = BitmapWarehouse.getBitmap(mContext, R.drawable.bird03);
            mBird02 = BitmapWarehouse.getBitmap(mContext, R.drawable.bird04);
        }
    }

    @Override
    DrawableItemEvent move( long time ) {
        DrawableItemEvent event = super.move( time );

        if ( ! isInCanvas() ) {
            return new DrawableItemEvent( DrawableItemEvent.DEAD, mX, mY );
        }
        else {
            return event;
        }
    }

    @Override
    public Bitmap getCurrentBitmap() {
        long remain = ( mCurrentTime - mStartTime ) % ( CHANGE_IMAGE_INTERVAL * 2 );
        if ( remain < CHANGE_IMAGE_INTERVAL ) {
            return mBird01;
        }
        else {
            return mBird02;
        }
    }

    @Override
    public void destroy() {

        super.destroy();

        if ( mRightToLeft) {
            if (mBird01 != null) {
                BitmapWarehouse.releaseBitmap(R.drawable.bird01);
                mBird01 = null;
            }

            if (mBird02 != null) {
                BitmapWarehouse.releaseBitmap(R.drawable.bird02);
                mBird02 = null;
            }
        }
        else {
            if (mBird01 != null) {
                BitmapWarehouse.releaseBitmap(R.drawable.bird03);
                mBird01 = null;
            }

            if (mBird02 != null) {
                BitmapWarehouse.releaseBitmap(R.drawable.bird04);
                mBird02 = null;
            }
        }
    }
}
