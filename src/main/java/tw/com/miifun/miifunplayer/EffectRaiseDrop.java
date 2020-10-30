package tw.com.miifun.miifunplayer;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

/**
 * Created by yhorn on 2016/3/11.
 */
public class EffectRaiseDrop extends EffectHorizontalMoveOut {

    final static private String appTag = "EffectRaiseDrop";

    ItemGirlBye mGirl;

    public EffectRaiseDrop(Context context, SurfaceHolder holder, int screenWidth, int screenHeight) {
        super(context, holder, screenWidth, screenHeight, TYPE_DEFAULT );
        mGirl= null;
    }

    @Override
    int handleMotionEvent( MotionEvent event ) {
        // create drawable item if needed
        if ( ! mBtnExit.isHit( (int)event.getX(), (int)event.getY() ) &&
                event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE ) {
            if ( mGirl == null ) {
                mGirl = new ItemGirlBye( mContext, mCanvasWidth, mCanvasHeight );
                mGirl.playAssetAudio( "byebye.mp3" );
                mItemList.add( mGirl );
            }
        }

        // return if user click the exit button
        return mBtnExit.onTouchEvent( event );
    }

    @Override
    public void move() {

        if ( mGirl != null ) {
            DrawableItemEvent event = mGirl.move(mCurrentTime);
            if ( event.type == DrawableItemEvent.DEAD ) {
                Log.v(appTag, "DrawableItemEvent.DEAD" );
                mItemList.remove( mGirl );
                mGirl.destroy();
                mGirl = null;
            }
        }
    }

    @Override
    void release() {

        super.release();

        if ( mGirl != null ) {
            mItemList.remove( mGirl );
            mGirl.destroy();
            mGirl = null;
        }
    }
}
