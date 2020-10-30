package tw.com.miifun.miifunplayer;

import android.content.Context;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

/**
 * Created by yhorn on 2016/3/11.
 */
public class EffectDropping extends EffectHorizontalMoveOut {

    final static private String appTag = "EffectDropping";
    final static private int MAX_ITEM_LIST_SIZE = 30;
    final static private int DROPPING_INTERVAL = 2000;
    final static private int MIN_INTERVAL_BETWEEN_DRAWABLE_ITEM = 200;

    int mTotalItemCount = 0;
    int mItemWidth, mItemHeight;
    long mLastAddDrawableItemTime;

    public EffectDropping(Context context, SurfaceHolder holder, int screenWidth, int screenHeight) {
        super(context, holder, screenWidth, screenHeight, TYPE_DEFAULT );

        mItemWidth = mContext.getResources().getDimensionPixelSize( R.dimen.effect_general_item_size );
        mItemHeight = mContext.getResources().getDimensionPixelSize( R.dimen.effect_general_item_size );
        mLastAddDrawableItemTime = 0;
    }

    @Override
    int handleMotionEvent( MotionEvent event ) {
        // create drawable item if needed
        if ( ! mBtnExit.isHit( (int)event.getX(), (int)event.getY() ) &&
                event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE ) {

            int x = (int)event.getX();
            int y = (int)event.getY();

            x = x - mItemWidth / 2;
            y = y - mItemHeight / 2;

            if ( mItemList.size() < MAX_ITEM_LIST_SIZE &&
                    mCurrentTime - mLastAddDrawableItemTime > MIN_INTERVAL_BETWEEN_DRAWABLE_ITEM) {
                DrawableItem item = new DrawableItem( mContext, x, y, mItemWidth, mItemHeight, mCanvasWidth, mCanvasHeight );
                item.setDestination(x, mCanvasHeight + mItemHeight, mCurrentTime, DROPPING_INTERVAL);
                if ( mTotalItemCount++ % 2 == 0 )
                    item.setBitmap( R.drawable.donkey01 );
                else
                    item.setBitmap( R.drawable.donkey02 );
                item.forceDeadIfNotInCanvas( true );
                mItemList.add(item);
                item.playAssetAudio( "Cartoon_boing_amp.mp3" );
                mLastAddDrawableItemTime = mCurrentTime;
            }
        }

        // return if user click the exit button
        return mBtnExit.onTouchEvent( event );
    }
}
