package tw.com.miifun.miifunplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by yhorn on 2016/3/10.
 */
// indicate an item that can be displayed and move on SurfaceView
public class DrawableItem {

    final static private String appTag = "DrawableItem";

    int mX, mY;
    int mWidth, mHeight;
    int mCanvasWidth, mCanvasHeight;
    Context mContext;

    int mTolerantPixel = 0;

    int mInitX;
    int mInitY;

    int mDestX;
    int mDestY;
    long mCreateTime;
    long mStartTime;
    long mDuration;
    long mCurrentTime;

    int mNextDestX;
    int mNextDestY;
    long mNextMovingTime;
    long mNextDuration;

    boolean mForceDeadIfNotInCanvas = false;

    // debug: recycle counter
    static public int mBitmapRecycleCount = 0;

    int mResId;
    Bitmap mBitmap;
    Matrix mBitmapMatrix;

    EffectSoundPlayer mAudio;

    public DrawableItem(Context context, int x, int y, int width, int height, int canvasWidth, int canvasHeight ) {

        mContext = context;
        mX = x;
        mY = y;
        mWidth = width;
        mHeight = height;
        mCanvasWidth = canvasWidth;
        mCanvasHeight = canvasHeight;

        mTolerantPixel = DrawUtil.dpToPx( DrawUtil.mmToDp( 5 ));

        mInitX = x;
        mInitY = y;

        mDestX = x;
        mDestY = y;
        mStartTime = System.currentTimeMillis();
        mCreateTime = mStartTime;
        mDuration = 0;

        mNextDestX = x;
        mNextDestY = y;
        mNextMovingTime = 0;
        mNextDuration = 0;
    }

    public void playAssetAudio( String filename ) {
        if ( mAudio == null ) {
            mAudio = new EffectSoundPlayer( mContext );
        }

        mAudio.playEffectSound( EffectSoundPlayer.TYPE_ASSET, filename );
    }

    public boolean isHit(int x, int y, int scaleWidth, int scaleHeight ) {
        return (x + mTolerantPixel >= ( mX + mWidth / 2 ) - scaleWidth / 2 &&
                x - mTolerantPixel < ( mX + mWidth / 2 ) + scaleWidth / 2 &&
                y + mTolerantPixel >= ( mY + mHeight / 2 ) - scaleHeight / 2 &&
                y - mTolerantPixel < ( mY + mHeight / 2 ) + scaleHeight / 2 );
    }

    public boolean isHit(int x, int y) {
        return (x + mTolerantPixel >= mX &&
                x - mTolerantPixel < (mX + mWidth) &&
                y + mTolerantPixel >= mY &&
                y - mTolerantPixel< (mY + mHeight));
    }

    public Rect getSrcRect(int bitmapWidth, int bitmapHeight ) {
        return DrawUtil.getSrcRect( mX, mY, mWidth, mHeight, bitmapWidth, bitmapHeight, mCanvasWidth, mCanvasHeight );
    }

    public Rect getDestRect() {
        return new Rect(
                mX >= 0 ? mX : 0,
                mY >= 0 ? mY : 0,
                mX + mWidth <= mCanvasWidth ? mX + mWidth : mCanvasWidth,
                mY + mHeight <= mCanvasHeight ? mY + mHeight : mCanvasHeight
        );
    }

    // MotionEvent handler
    public int onTouchEvent( MotionEvent event ) {
        /*
        int action = event.getAction();
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_OUTSIDE:
                break;
            default:
        }
        */
        return 0;
    }

    public void forceDeadIfNotInCanvas( boolean setting ) {
        mForceDeadIfNotInCanvas = setting;
    }

    // protected abstract method
    void onDrawableItemEvent( DrawableItemEvent event ) { return; }

    DrawableItemEvent move( long time ) {

        mCurrentTime = time;

        // check if next moving should start
        if ( mNextMovingTime != 0 && time >= mNextMovingTime ) {
            mInitX = mX;
            mInitY = mY;
            mDestX = mNextDestX;
            mDestY = mNextDestY;
            mDuration = mNextDuration;
            mStartTime = mNextMovingTime;
            mNextMovingTime = 0;
        }

        // check if move is necessary
        if ( mDestX != mInitX || mDestY != mInitY ) {
            // need to move position
            if ( time >= mStartTime + mDuration ) {
                // too late, just move to the destination
                // Log.v( appTag, "too late, skip to dest " + mDestX + " " + mDestY );
                mX = mDestX;
                mY = mDestY;
                mInitX = mX;
                mInitY = mY;
            }
            else {
                // move!
                // Log.v( appTag, "move x:" + mX + " destx:" + mDestX + " initx:" + mInitX + " duration:" + mDuration + " interval:" + (time - mStartTime) );
                mX = mInitX + (int)(( mDestX - mInitX ) * ( time - mStartTime ) / mDuration );
                // Log.v( appTag, "move y:" + mY + " desty:" + mDestY + " inity:" + mInitY + " duration:" + mDuration + " interval:" + (time - mStartTime) );
                mY = mInitY + (int)(( mDestY - mInitY ) * ( time - mStartTime ) / mDuration);
            }
        }

        // Log.v( appTag, "no need move, mDestX " + mDestX + " = mInit X" + mInitX );

        if ( mForceDeadIfNotInCanvas && ! isInCanvas() ) {
            return new DrawableItemEvent( DrawableItemEvent.DEAD, mX, mY );
        }

        return new DrawableItemEvent( DrawableItemEvent.NONE, mX, mY );
    }

    public void setDestination( int x, int y, long startTime, long movingInterval ) {

        // Log.v(appTag, "setDestination x:" + x + " y:" + y + " start:" + startTime + " interval:" + movingInterval );

        mNextDestX = x;
        mNextDestY = y;
        mNextMovingTime = startTime;
        mNextDuration = movingInterval;
    }

    public void stopMoving() {
        mDestX = mX;
        mDestY = mY;
        mInitX = mX;
        mInitY = mY;
        mDuration = 0;
        mNextMovingTime = 0;
        mNextDuration = 0;
    }

    public void setBitmap( int resid ) {
        if ( mBitmap != null && mResId != 0 ) {
            BitmapWarehouse.releaseBitmap( mResId );
            mBitmapRecycleCount --;
            mBitmap = null;
        }

        mBitmap = BitmapWarehouse.getBitmap( mContext, resid );

        if ( mBitmap != null ) {
            mResId = resid;
        }
        else {
            mResId = 0;
            Log.w(appTag, "error, BitmapWarehouse.getBitmap return null");
        }
    }

    // public abstract method
    public boolean draw( Canvas canvas ) {

        Rect srcRect = null;
        Rect destRect;
        Bitmap bp;

        bp = getCurrentBitmap();

        if ( canvas == null ) {
            Log.w(appTag, "error, holder is null");
            return false;
        }

        if ( mX > mCanvasWidth || mX + mWidth < 0 || mY > mCanvasHeight || mY + mHeight < 0 ) {
            // Log.v(appTag, "ignore draw, outside the screen");
            return false;
        }

        if ( mX < 0 || mX + mWidth > mCanvasWidth ||  mY < 0 || mY + mHeight > mCanvasHeight ) {
            srcRect = getSrcRect( bp.getWidth(), bp.getHeight() );

            if ( srcRect == null ) {
                return false;
            }
        }

         destRect = getDestRect();

        // Log.v( appTag, "drawBitmap bp " + bp + " x:" + mX + " y:" + mY + " w:" + mWidth + " h:" + mHeight );
        if ( mBitmapMatrix == null ) {
            canvas.drawBitmap(bp, srcRect, destRect, null);
        }
        else {
            canvas.drawBitmap(bp, mBitmapMatrix, null);
        }

        return true;
    }

    public boolean isInCanvas() {
        if ( mX > mCanvasWidth || mX + mWidth < 0 || mY > mCanvasHeight || mY + mHeight < 0 )
            return false;
        else
            return true;
    }

    public Bitmap getCurrentBitmap() {
        return mBitmap;
    }

    public void destroy() {

        if ( mAudio != null ) {
            mAudio.destroy();
            mAudio = null;
        }

        // Log.v( appTag, "destroy" );
        if ( mBitmap != null && mResId != 0 ) {
            BitmapWarehouse.releaseBitmap( mResId );
            mResId = 0;
            mBitmapRecycleCount--;
        }
    }
}

