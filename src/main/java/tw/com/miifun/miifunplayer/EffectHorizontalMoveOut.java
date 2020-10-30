package tw.com.miifun.miifunplayer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import java.util.ArrayList;

/**
 * Created by yhorn on 2016/3/10.
 */
public class EffectHorizontalMoveOut extends ThreadGame {

    final static private String appTag = "EffectMoveToLeft";
    final static private int MAX_ITEM_LIST_SIZE = 30;
    final static private int MOVING_INTERVAL = 4000; // from left most to right most
    final static private int MIN_INTERVAL_BETWEEN_DRAWABLE_ITEM = 100;

    final static public int TYPE_DEFAULT = 0;
    final static public int TYPE_DOG = 1;
    final static public int TYPE_BIRD = 2;

    ArrayList<DrawableItem> mItemList = new ArrayList<>();
    ItemExitButton mBtnExit;
    long mLastAddDrawableItemTime;
    int mType;

    EffectSoundPlayer mAudio;

    public EffectHorizontalMoveOut(Context context, SurfaceHolder holder, int screenWidth, int screenHeight, int type ) {
        super(context, holder, screenWidth, screenHeight);
        mBtnExit = new ItemExitButton( mContext, mCanvasWidth, mCanvasHeight );
        mType = type;
    }

    @Override
    void release() {

        for ( int i = 0; i < mItemList.size(); i ++ ) {
            mItemList.get(i).destroy();
        }
        mItemList.clear();

        if ( mBtnExit != null ) {
            mBtnExit.destroy();
            mBtnExit = null;
        }

        if ( mAudio != null ) {
            mAudio.destroy();
            mAudio = null;
        }
    }

    @Override
    void draw() {

        // consume the click event first
        if ( mMotionEvent != null ) {
            mMotionEvent = null;
        }

        Canvas canvas = mSurfaceHolder.lockCanvas();

        if ( canvas == null ) {
            Log.w(appTag, "mSurfaceHolder.lockCanvas() return null");
            return;
        }

        // clean canvas
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        // draw exit button
        mBtnExit.draw( canvas );

        // draw drawable item
        for ( int i = 0; i < mItemList.size(); i ++ ) {
            mItemList.get(i).draw(canvas);
        }

        // update the screen
        mSurfaceHolder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void move() {
        ArrayList<DrawableItem> trash = new ArrayList<>();
        for ( int i = 0; i < mItemList.size(); i ++ ) {
            DrawableItemEvent event = mItemList.get(i).move( mCurrentTime );
            if ( event.type == DrawableItemEvent.DEAD ) {
                trash.add( mItemList.get(i) );
            }
        }

        for ( int i = 0; i < trash.size(); i ++ ) {
            mItemList.remove( trash.get(i));
            Log.v( appTag, "remove item" );
        }

        trash.clear();
    }


    @Override
    int handleMotionEvent( MotionEvent event ) {
        // create drawable item if needed
        if ( ! mBtnExit.isHit( (int)event.getX(), (int)event.getY() ) &&
                ( event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_MOVE )) {
            int x = (int)event.getX();
            int y = (int)event.getY();

            x = x - ItemDog.getWidth( mContext ) / 2;
            y = y - ItemDog.getHeight(mContext) / 2;

            if ( mItemList.size() < MAX_ITEM_LIST_SIZE &&
                    mCurrentTime - mLastAddDrawableItemTime > MIN_INTERVAL_BETWEEN_DRAWABLE_ITEM) {
                DrawableItem item;

                switch( mType ) {
                    case TYPE_DOG:
                        item = new ItemDog( mContext, x, y, mCanvasWidth, mCanvasHeight, ( x > mCanvasWidth / 2 ) );
                        item.playAssetAudio( "dog_bark_short.mp3" );
                        break;
                    case TYPE_BIRD:
                        item = new ItemBird( mContext, x, y, mCanvasWidth, mCanvasHeight, ( x > mCanvasWidth / 2 ) );
                        item.playAssetAudio( "bird_short.mp3" );
                        break;
                    default:
                        item = new ItemDog( mContext, x, y, mCanvasWidth, mCanvasHeight, ( x > mCanvasWidth / 2 ) );
                        item.playAssetAudio( "dog_bark_short.mp3" );
                }

                if ( x > mCanvasWidth / 2 ) {
                    item.setDestination( 0 - ItemDog.getWidth( mContext ) - 1, y, mCurrentTime, MOVING_INTERVAL * x / mCanvasWidth );
                }
                else {
                    item.setDestination( mCanvasWidth + 1, y, mCurrentTime, MOVING_INTERVAL * ( mCanvasWidth - x ) / mCanvasWidth );
                }

                mItemList.add( item );
                mLastAddDrawableItemTime = mCurrentTime;
            }
        }

        // return if user click the exit button
        return mBtnExit.onTouchEvent( event );
    }
}
