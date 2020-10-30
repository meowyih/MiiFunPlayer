package tw.com.miifun.miifunplayer;

import android.content.Context;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

/**
 * Created by yhorn on 2016/3/11.
 */
public class EffectHoldDropping extends EffectHorizontalMoveOut {

    final static private String appTag = "EffectDropping";
    final static private int DROPPING_INTERVAL = 1000;

    ItemKing mItem;
    int mItemWidth, mItemHeight;

    public EffectHoldDropping(Context context, SurfaceHolder holder, int screenWidth, int screenHeight) {
        super(context, holder, screenWidth, screenHeight, TYPE_DEFAULT );

        mItemWidth = mContext.getResources().getDimensionPixelSize( R.dimen.effect_general_item_size );
        mItemHeight = mContext.getResources().getDimensionPixelSize( R.dimen.effect_general_item_size );
    }

    @Override
    int handleMotionEvent( MotionEvent event ) {
        // create drawable item if needed
        int x = (int)event.getX();
        int y = (int)event.getY();

        x = x - mItemWidth / 2;

        if ( event.getAction() == MotionEvent.ACTION_DOWN ) {
            if ( mItem == null ) {
                mItem = new ItemKing( mContext, x, y, mCanvasWidth, mCanvasHeight );
                mItemList.add(mItem);
                mItem.playAssetAudio("Children_Boo.mp3");
            }
        }
        else if ( event.getAction() == MotionEvent.ACTION_MOVE ) {
            if ( mItem != null ) {
                mItem.setDestination( x, y, mCurrentTime, 0 );
            }
        }
        else if ( event.getAction() == MotionEvent.ACTION_UP ) {

            if ( mItem != null ) {
                mItem.setDestination( x, mCanvasHeight + 1, mCurrentTime, DROPPING_INTERVAL );
                mItem.setDroppingFlag( true );
                mItem.playAssetAudio("Children_Aaaaah.mp3" );
                mItem = null; // will be released by parent class in the mItemList release loop
            }
        }

        // return if user click the exit button
        return mBtnExit.onTouchEvent( event );
    }
}
