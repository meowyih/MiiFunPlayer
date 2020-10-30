package tw.com.miifun.miifunplayer;

import android.content.Context;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

/**
 * Created by yhorn on 2016/3/11.
 */
public class EffectDragZoomOut extends EffectHorizontalMoveOut {

    final static private String appTag = "EffectDragZoomOut";
    final static private int MAX_ITEM_LIST_SIZE = 30;
    final static private int ZOOM_OUT_INTERVAL = 1000;
    final static private int MIN_INTERVAL_BETWEEN_DRAWABLE_ITEM = 100;

    final static public int TYPE_CHICKEN = 1;
    final static public int TYPE_BUTTERFLY = 2;

    int mItemWidth, mItemHeight;
    long mLastAddDrawableItemTime;

    public EffectDragZoomOut(Context context, SurfaceHolder holder, int screenWidth, int screenHeight, int type ) {
        super(context, holder, screenWidth, screenHeight, type );

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
                ItemZoomIn item = new ItemZoomIn( mContext, x, y, mItemWidth, mItemHeight, mCanvasWidth, mCanvasHeight );

                if ( mType == TYPE_CHICKEN )
                    item.setBitmap(R.drawable.chicken01);
                else if ( mType == TYPE_BUTTERFLY )
                    item.setBitmap(R.drawable.butterfly );
                else
                    item.setBitmap( R.drawable.chicken01 );

                item.setLifeInterval(ZOOM_OUT_INTERVAL);
                mItemList.add(item);

                item.playAssetAudio( "downer_fx.mp3" );

                mLastAddDrawableItemTime = mCurrentTime;
            }
        }

        // return if user click the exit button
        return mBtnExit.onTouchEvent( event );
    }
}
