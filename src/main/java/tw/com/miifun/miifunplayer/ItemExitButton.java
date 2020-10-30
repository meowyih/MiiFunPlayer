package tw.com.miifun.miifunplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by yhorn on 2016/3/10.
 */
public class ItemExitButton extends DrawableItem {

    final static private String appTag = "ItemExitButton";

    Bitmap mIconNormal;
    Bitmap mIconPressed;

    boolean mIsPressed;

    public ItemExitButton(Context context, int canvasWidth, int canvasHeight ) {

        super(context,
                0 + context.getResources().getDimensionPixelSize(R.dimen.common_icon_exit_margin_left),
                canvasHeight -
                        context.getResources().getDimensionPixelSize(R.dimen.common_icon_exit_height) -
                        context.getResources().getDimensionPixelSize(R.dimen.common_icon_exit_margin_bottom),
                context.getResources().getDimensionPixelSize(R.dimen.common_icon_exit_width),
                context.getResources().getDimensionPixelSize(R.dimen.common_icon_exit_height),
                canvasWidth,
                canvasHeight);

        /*
        int w = context.getResources().getDimensionPixelSize(R.dimen.common_icon_exit_width);
        int h = context.getResources().getDimensionPixelSize(R.dimen.common_icon_exit_height);
        int marginLeft = context.getResources().getDimensionPixelSize( R.dimen.common_icon_exit_margin_left );
        int marginBottom = context.getResources().getDimensionPixelSize( R.dimen.common_icon_exit_margin_bottom );
        int x = 0 + marginLeft;
        int y = canvasHeight - h - marginBottom;
        */

        mIconNormal = BitmapWarehouse.getBitmap( mContext, R.drawable.icon_exit );
        mIconPressed = BitmapWarehouse.getBitmap( mContext, R.drawable.icon_exit_pressed );
        mIsPressed = false;
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
    public boolean draw( Canvas canvas ) {
        Rect srcRect = null;
        Rect destRect;
        Bitmap icon;

        if ( ! mIsPressed )
            icon = mIconNormal;
        else
            icon = mIconPressed;


        if ( canvas == null ) {
            Log.w(appTag, "error, holder is null");
            return false;
        }

        if ( mX > mCanvasWidth || mX + mWidth < 0 || mY > mCanvasHeight || mY + mHeight < 0 ) {
            // Log.v(appTag, "ignore draw, outside the screen");
            return false;
        }

        if ( mX < 0 || mX + mWidth > mCanvasWidth ||  mY < 0 || mY + mHeight > mCanvasHeight ) {
            srcRect = getSrcRect( icon.getWidth(), icon.getHeight() );

            if ( srcRect == null ) {
                return false;
            }
        }

        destRect = getDestRect();

        // Log.v( appTag, "drawBitmap bp " + bp + " x:" + mX + " y:" + mY + " w:" + mWidth + " h:" + mHeight );
        if ( mBitmapMatrix == null ) {
            canvas.drawBitmap(icon, srcRect, destRect, null);
        }
        else {
            canvas.drawBitmap(icon, mBitmapMatrix, null);
        }

        return true;
    }

    @Override
    public void destroy() {

        if ( mIconNormal != null ) {
            BitmapWarehouse.releaseBitmap(R.drawable.icon_exit);
            mIconNormal = null;
        }

        if ( mIconPressed != null ) {
            BitmapWarehouse.releaseBitmap(R.drawable.icon_exit_pressed);
            mIconNormal = null;
        }
    }

    @Override
    public int onTouchEvent( MotionEvent event ) {

        mIsPressed = false;

        if ( isHit( (int)event.getX(), (int)event.getY() )) {
            if ( event.getAction() == MotionEvent.ACTION_DOWN ) {
                mIsPressed = true;
            }
            else if ( event.getAction() == MotionEvent.ACTION_UP ) {
                return ThreadGame.MOTION_EVENT_ON_EXIT;
            }
        }

        return 0;
    }
}
