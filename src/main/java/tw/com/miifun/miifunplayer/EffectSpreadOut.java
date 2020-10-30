package tw.com.miifun.miifunplayer;

import android.content.Context;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

/**
 * Created by yhorng on 2016/3/12.
 */
public class EffectSpreadOut extends EffectHorizontalMoveOut {

    final static private int MAX_ITEM_LIST_SIZE = 30;
    final static private int SPEED = 8000; // interval for left most to right most * 2
    final static private int MIN_INTERVAL_BETWEEN_DRAWABLE_ITEM = 100;

    final static public int TYPE_KUMA = 0;
    final static public int TYPE_DONKEY = 1;
    final static public int TYPE_WHITE_CAT = 2;

    int mType = TYPE_KUMA;
    int mCounter = 0;

    int mItemWidth, mItemHeight;
    long mLastAddDrawableItemTime;
    int mNextAngle;

    public EffectSpreadOut(Context context, SurfaceHolder holder, int screenWidth, int screenHeight) {
        super(context, holder, screenWidth, screenHeight, TYPE_DEFAULT );

        mItemWidth = mContext.getResources().getDimensionPixelSize( R.dimen.effect_general_item_size );
        mItemHeight = mContext.getResources().getDimensionPixelSize( R.dimen.effect_general_item_size );
        mLastAddDrawableItemTime = 0;
        mNextAngle = 0;

        mAudio = new EffectSoundPlayer( mContext );
    }

    public EffectSpreadOut(Context context, SurfaceHolder holder, int screenWidth, int screenHeight, int type) {
        super(context, holder, screenWidth, screenHeight, TYPE_DEFAULT );

        mItemWidth = mContext.getResources().getDimensionPixelSize( R.dimen.effect_general_item_size );
        mItemHeight = mContext.getResources().getDimensionPixelSize( R.dimen.effect_general_item_size );
        mLastAddDrawableItemTime = 0;
        mNextAngle = 0;
        mType = type;

        mAudio = new EffectSoundPlayer( mContext );
    }

    @Override
    int handleMotionEvent( MotionEvent event ) {
        // create drawable item if needed
        if ( event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE ) {

            int x = (int)event.getX();
            int y = (int)event.getY();

            x = x - mItemWidth / 2;
            y = y - mItemHeight / 2;

            if ( ! mBtnExit.isHit( (int)event.getX(), (int)event.getY() ) &&
                    mItemList.size() < MAX_ITEM_LIST_SIZE &&
                    mCurrentTime - mLastAddDrawableItemTime > MIN_INTERVAL_BETWEEN_DRAWABLE_ITEM) {
                DrawableItem item = new DrawableItem( mContext, x, y, mItemWidth, mItemHeight, mCanvasWidth, mCanvasHeight );
                setDestinationByAngle( item, mNextAngle );
                mNextAngle = mNextAngle + 45;

                switch ( mType ) {
                    case TYPE_KUMA: item.setBitmap(R.drawable.kuma); break;
                    case TYPE_DONKEY:
                        if ( mCounter % 2 == 0 ) {
                            item.setBitmap(R.drawable.donkey01);
                        }
                        else {
                            item.setBitmap(R.drawable.donkey02);
                        }
                        mCounter ++;
                        break;
                    case TYPE_WHITE_CAT: item.setBitmap(R.drawable.mainicon_512_512); break;
                    default: item.setBitmap(R.drawable.kuma);
                }

                item.setBitmap(R.drawable.kuma);
                item.forceDeadIfNotInCanvas(true);
                mItemList.add(item);
                item.playAssetAudio("raiser_fx_amp.mp3");
                mLastAddDrawableItemTime = mCurrentTime;
            }
        }

        // return if user click the exit button
        return mBtnExit.onTouchEvent( event );
    }

    private void setDestinationByAngle( DrawableItem item, int angle ) {
        int x, y;
        int side = mCanvasWidth * 2;
        x = (int)( side * Math.sin( angle * Math.PI / 180 ));
        y = (int)( side * Math.cos( angle * Math.PI / 180 ));
        item.setDestination( item.mX + x, item.mY + y, mCurrentTime, SPEED );
    }
}
