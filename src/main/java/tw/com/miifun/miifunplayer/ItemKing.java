package tw.com.miifun.miifunplayer;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by yhorn on 2016/3/11.
 */
public class ItemKing extends DrawableItem {

    final static private String appTag = "ItemKing";
    final static private int CHANGE_IMAGE_INTERVAL = 200;

    Bitmap mKing01;
    Bitmap mKing02;

    boolean mIsDropping = false;

    public static int getWidth( Context context ) {
        return context.getResources().getDimensionPixelSize( R.dimen.effect_general_item_size );
    }

    public static int getHeight( Context context ) {
        return context.getResources().getDimensionPixelSize( R.dimen.effect_general_item_size );
    }

    public ItemKing(Context context, int x, int y, int canvasWidth, int canvasHeight ) {

        super( context,
                x,
                y,
                getWidth(context),
                getHeight( context ),
                canvasWidth,
                canvasHeight );

        mKing01 = BitmapWarehouse.getBitmap(mContext, R.drawable.king01);
        mKing02 = BitmapWarehouse.getBitmap(mContext, R.drawable.king02);
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

    public void setDroppingFlag( boolean flag ) {
        mIsDropping = flag;
    }

    @Override
    public Bitmap getCurrentBitmap() {

        if ( mIsDropping ) {
            return mKing02;
        }

        long remain = ( mCurrentTime - mCreateTime ) % ( CHANGE_IMAGE_INTERVAL * 2 );
        if ( remain < CHANGE_IMAGE_INTERVAL ) {
            return mKing01;
        }
        else {
            return mKing02;
        }
    }

    @Override
    public void destroy() {

        super.destroy();

        if (mKing01 != null) {
            BitmapWarehouse.releaseBitmap(R.drawable.king01);
            mKing01 = null;
        }

        if (mKing02 != null) {
            BitmapWarehouse.releaseBitmap(R.drawable.king02);
            mKing02 = null;
        }
    }
}
