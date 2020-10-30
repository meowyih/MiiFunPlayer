package tw.com.miifun.miifunplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yhorn on 2016/3/10.
 */
public class BitmapWarehouse {

    final static private String appTag = "BitmapWarehouse";

    static boolean mIsPause = false;
    static long mMemoryUsage = 0;

    // resource id vs bitmap object
    static HashMap<Integer, BitmapData> mHashMap = new HashMap<>();

    synchronized static Bitmap getBitmap(Context context, int resId ) {

        if ( mIsPause ) {
            // Log.e(appTag, "getBitmap error: warehouse is in pause state");
            return null;
        }

        if ( mHashMap.containsKey( resId ) ) {
            BitmapData data = mHashMap.get( resId );
            data.link ++;
            // Log.v(appTag, "getBitmap, bitmap " + resId + " exist, return directly. Link = " + data.link );
            return data.bitmap;
        }
        else {
            BitmapData data = new BitmapData();
            data.bitmap = BitmapFactory.decodeResource(context.getResources(), resId);

            if ( data.bitmap != null ) {
                data.link = 1;
                mHashMap.put(resId, data);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ) {
                    mMemoryUsage += data.bitmap.getAllocationByteCount();
                }
                else {
                    mMemoryUsage += data.bitmap.getByteCount();
                }
                // Log.v(appTag, "getBitmap, bitmap " + resId + " does not exist. create it. total " + mHashMap.size() + " images " + mMemoryUsage + " bytes");
                return data.bitmap;
            }
            else {
                Log.e(appTag, "getBitmap, bitmap does not exist. failed to create bitmap");
                return null;
            }
        }
    }

    synchronized static void releaseBitmap( int resId ) {
        if ( mHashMap.containsKey( resId )) {
            BitmapData data = mHashMap.get( resId );
            data.link --;

            if ( data.link == 0 ) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ) {
                    mMemoryUsage -= data.bitmap.getAllocationByteCount();
                }
                else {
                    mMemoryUsage -= data.bitmap.getByteCount();
                }

                data.bitmap.recycle();
                data.bitmap = null;
                mHashMap.remove( resId );

                // Log.v(appTag, "releaseBitmap, link is 0, remove it from map" );
            }
            else {
                // Log.v( appTag, "releaseBitmap reduce link to " + data.link );
            }
        }
        else {
            // Log.e( appTag, "releaseBitmap - id " + resId + " does not exist." );
        }
    }

    // temperary release the memory
    synchronized static void pause() {

        int link = 0;
        int recycleCount = 0;

        mIsPause = true;

        for(Map.Entry<Integer, BitmapData> entry : mHashMap.entrySet()) {
            // Integer key = entry.getKey();
            BitmapData value = entry.getValue();

            if ( value.bitmap != null ) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ) {
                    mMemoryUsage -= value.bitmap.getAllocationByteCount();
                }
                else {
                    mMemoryUsage -= value.bitmap.getByteCount();
                }

                link += value.link;
                value.bitmap.recycle();
                value.bitmap = null;
                recycleCount ++;
            }
        }

        // Log.i( appTag, "pause, release " + recycleCount + " bitmap, memory usage " + mMemoryUsage + ", total link " + link );
    }

    // back from paused state
    synchronized static void resume( Context context ) {

        int count = 0;

        for(Map.Entry<Integer, BitmapData> entry : mHashMap.entrySet()) {
            Integer key = entry.getKey();
            BitmapData value = entry.getValue();

            if ( value.bitmap == null ) {
                value.bitmap = BitmapFactory.decodeResource( context.getResources(), key );
                count ++;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ) {
                    mMemoryUsage += value.bitmap.getAllocationByteCount();
                }
                else {
                    mMemoryUsage += value.bitmap.getByteCount();
                }
            }
        }

        mIsPause = false;

        // Log.i( appTag, "resume, load " + count + " images" );
    }


    synchronized static void release() {

        int link = 0;
        int recycleCount = 0;

        for(Map.Entry<Integer, BitmapData> entry : mHashMap.entrySet()) {
            // Integer key = entry.getKey();
            BitmapData value = entry.getValue();

            if ( value.bitmap != null ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ) {
                    mMemoryUsage += value.bitmap.getAllocationByteCount();
                }
                else {
                    mMemoryUsage += value.bitmap.getByteCount();
                }
                link += value.link;
                value.bitmap.recycle();
                recycleCount ++;
            }
        }

        mHashMap.clear();

        // Log.i( appTag, "release " + recycleCount + " bitmap, mMemoryUsage " + mMemoryUsage + " bytes, total link " + link );
    }
}
