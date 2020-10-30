package tw.com.miifun.miifunplayer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;

/**
 * Created by yhorn on 2016/3/10.
 */
public class DrawUtil {

    static public int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    // 160 dp = 25.4mm
    static public int mmToDp( int mm ) {
        return (int)(mm * 160 / 25.4);
    }

    static int getColorFromResource(Context mContext, int resId ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            return mContext.getResources().getColor(resId, null);
        }
        else {
            return mContext.getResources().getColor(resId);
        }
    }

    static public boolean drawBitmap(Canvas canvas, Bitmap bitmap,
                                     int x, int y,
                                     int imageWidth, int imageHeight,
                                     int screenWidth, int screenHeight )
    {
        Rect srcRect, destRect;

        // draw sun
        if ( bitmap != null ) {
            destRect = new Rect(
                    x >= 0 ? x : 0,
                    y >= 0 ? y : 0,
                    x + imageWidth <= screenWidth ? x + imageWidth : screenWidth,
                    y + imageHeight <= screenHeight ? y + imageHeight : screenHeight );

            srcRect = DrawUtil.getSrcRect( x, y, imageWidth, imageHeight, bitmap.getWidth(), bitmap.getHeight(), screenWidth, screenHeight );
            if ( srcRect != null ) {
                canvas.drawBitmap( bitmap, srcRect, destRect, null );
                return true;
            }
        }

        return false;
    }

    static public Rect getSrcRect(int x, int y,
                                  int imageWidth, int imageHeight,
                                  int bitmapWidth, int bitmapHeight,
                                  int screenWidth, int screenHeight ) {

        int transX, transY, transWidth, transHeight;

        // default the src rect is the size of the original bitmap
        transX = 0;
        transY = 0;
        transWidth = bitmapWidth;
        transHeight = bitmapHeight;

        if ( x < 0 ) {
            transWidth = (int)( bitmapWidth * ((double) ( imageWidth - Math.abs( x ))) / ((double) imageWidth ));
            transX = bitmapWidth - transWidth;
        }

        if (( x + imageWidth )> screenWidth ) {
            transWidth = (int)( bitmapWidth * ((double) ( screenWidth - x )) / ((double) imageWidth ));
        }

        if ( y < 0 ) {
            transHeight = (int)( bitmapHeight * ((double) ( imageHeight - Math.abs( y ))) / ((double) imageHeight ));
            transY = bitmapHeight - transHeight;
        }

        if (( y + imageHeight )> screenHeight ) {
            transHeight = (int)( bitmapHeight * ((double) ( screenHeight - y )) / ((double) imageHeight ));
        }

        if ( transX < 0 || transY < 0 || transWidth > bitmapWidth || transHeight > bitmapHeight ) {
            return null;
        }

        return new Rect( transX, transY, transX + transWidth, transY + transHeight );
    }
}

