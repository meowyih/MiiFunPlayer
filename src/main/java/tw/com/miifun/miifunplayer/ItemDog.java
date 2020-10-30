package tw.com.miifun.miifunplayer;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by yhorn on 2016/3/11.
 */
public class ItemDog extends DrawableItem {

    final static private String appTag = "ItemDog";
    final static private int CHANGE_IMAGE_INTERVAL = 200;

    Bitmap mDog1;
    Bitmap mDog2;
    Bitmap mDog3;
    Bitmap mDog4;

    boolean mRightToLeft;

    public static int getWidth( Context context ) {
        return context.getResources().getDimensionPixelSize( R.dimen.effect_general_item_size );
    }

    public static int getHeight( Context context ) {
        return context.getResources().getDimensionPixelSize( R.dimen.effect_general_item_size );
    }

    public ItemDog(Context context, int x, int y, int canvasWidth, int canvasHeight, boolean rightToLeft ) {

        super( context,
                x,
                y,
                getWidth(context),
                getHeight( context ),
                canvasWidth,
                canvasHeight );

        mRightToLeft = rightToLeft;

        if ( mRightToLeft ) {
            mDog1 = BitmapWarehouse.getBitmap(mContext, R.drawable.dog01);
            mDog2 = BitmapWarehouse.getBitmap(mContext, R.drawable.dog02);
            mDog3 = BitmapWarehouse.getBitmap(mContext, R.drawable.dog03);
            mDog4 = BitmapWarehouse.getBitmap(mContext, R.drawable.dog04);
        }
        else {
            mDog1 = BitmapWarehouse.getBitmap(mContext, R.drawable.dog05);
            mDog2 = BitmapWarehouse.getBitmap(mContext, R.drawable.dog06);
            mDog3 = BitmapWarehouse.getBitmap(mContext, R.drawable.dog07);
            mDog4 = BitmapWarehouse.getBitmap(mContext, R.drawable.dog08);
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
        long remain = ( mCurrentTime - mStartTime ) % ( CHANGE_IMAGE_INTERVAL * 4 );
        if ( remain < CHANGE_IMAGE_INTERVAL ) {
            return mDog1;
        }
        else if ( remain < CHANGE_IMAGE_INTERVAL * 2 ) {
            return mDog2;
        }
        else if ( remain < CHANGE_IMAGE_INTERVAL * 3 ) {
            return mDog3;
        }
        else {
            return mDog4;
        }
    }

    @Override
    public void destroy() {

        super.destroy();

        if ( mRightToLeft) {
            if (mDog1 != null) {
                BitmapWarehouse.releaseBitmap(R.drawable.dog01);
                mDog1 = null;
            }

            if (mDog2 != null) {
                BitmapWarehouse.releaseBitmap(R.drawable.dog02);
                mDog2 = null;
            }

            if (mDog3 != null) {
                BitmapWarehouse.releaseBitmap(R.drawable.dog03);
                mDog3 = null;
            }

            if (mDog4 != null) {
                BitmapWarehouse.releaseBitmap(R.drawable.dog04);
                mDog4 = null;
            }
        }
        else {
            if (mDog1 != null) {
                BitmapWarehouse.releaseBitmap(R.drawable.dog05);
                mDog1 = null;
            }

            if (mDog2 != null) {
                BitmapWarehouse.releaseBitmap(R.drawable.dog06);
                mDog2 = null;
            }

            if (mDog3 != null) {
                BitmapWarehouse.releaseBitmap(R.drawable.dog07);
                mDog3 = null;
            }

            if (mDog4 != null) {
                BitmapWarehouse.releaseBitmap(R.drawable.dog08);
                mDog4 = null;
            }
        }
    }
}
