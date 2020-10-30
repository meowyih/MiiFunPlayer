package tw.com.miifun.miifunplayer;

import android.content.Context;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

/**
 * Created by yhorn on 2016/3/12.
 */
public class EffectSizeSwing extends EffectHorizontalMoveOut {

    final static private String appTag = "EffectSizeSwing";

    final static public int TYPE_DUCK = 1;
    final static public int TYPE_RABBIT = 2;
    final static public int TYPE_SHEEP = 3;

    int mItemWidth, mItemHeight;
    ItemSizeSwing mItem;

    public EffectSizeSwing(Context context, SurfaceHolder holder, int screenWidth, int screenHeight, int type ) {
        super(context, holder, screenWidth, screenHeight, type );

        mItemWidth = mContext.getResources().getDimensionPixelSize( R.dimen.effect_general_item_size );
        mItemHeight = mContext.getResources().getDimensionPixelSize( R.dimen.effect_general_item_size );

        mAudio = new EffectSoundPlayer( mContext );
    }

    @Override
    int handleMotionEvent( MotionEvent event ) {
        // create drawable item if needed
        if ( event.getAction() == MotionEvent.ACTION_DOWN && mItem == null ) {
            mItem = new ItemSizeSwing( mContext, (int)event.getX(), (int)event.getY(), mCanvasWidth, mCanvasHeight );

            if ( mType == TYPE_RABBIT )
                mItem.setBitmap( R.drawable.rabbit );
            else if ( mType == TYPE_DUCK )
                mItem.setBitmap(R.drawable.duck );
            else if ( mType == TYPE_SHEEP )
                mItem.setBitmap( R.drawable.sheep );
            else
                mItem.setBitmap(R.drawable.duck);

            if ( ! mBtnExit.isHit( (int)event.getX(), (int)event.getY() ) )
                mItem.playAssetAudio( "raiser_fx_amp.mp3" );

            mItemList.add( mItem );
        }
        else if ( event.getAction() == MotionEvent.ACTION_UP && mItem != null ) {
            mItemList.clear();
            mItem.destroy();
            mItem = null;

            mAudio.playEffectSound( EffectSoundPlayer.STOP );
        }

        return mBtnExit.onTouchEvent( event);
    }
}
