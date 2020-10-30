package tw.com.miifun.miifunplayer;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by yhorn on 2016/2/13.
 */
public class TrackManager {

    final static public int STORAGE_TYPE_UNSET = 0;
    final static public int STORAGE_TYPE_ASSET = 1;
    final static public int STORAGE_TYPE_EXPANSION_ZIP = 2;

    final static public int STATUS_AVAILABLE = 1;
    final static public int STATUS_AVAILABLE_AFTER_PURCHASE = 2;

    // structure to store track information
    public class Track {
        private int mIconResId;
        private int mIconNotAvailableResId;
        private int mStorageType;
        private String mFilename;
        private int mStatus;

        public Track() {
            mIconResId = 0;
            mStatus = STATUS_AVAILABLE;
            mStorageType = STORAGE_TYPE_UNSET;
            mFilename = "";
        }

        public Track( int iconResId, int iconNotAvailableResId, int storageType, String filename ) {
            mIconResId = iconResId;
            mIconNotAvailableResId = iconNotAvailableResId;
            mStorageType = storageType;
            mFilename = filename;
            mStatus = STATUS_AVAILABLE;
        }

        public Track( int iconResId, int iconNotAvailableResId, int storageType, String filename, int status ) {
            mIconResId = iconResId;
            mIconNotAvailableResId = iconNotAvailableResId;
            mStorageType = storageType;
            mFilename = filename;
            mStatus = status;
        }

        public int getIconResId() { return mIconResId; }
        public int getIconNotAvailableResId() { return mIconNotAvailableResId; }
        public int getStorageType() { return mStorageType; }
        public String getFilename() { return mFilename; }
        public int getStatus() { return mStatus; }
        public void setStatus( int status ) { mStatus = status; }
    }

    private ArrayList<Track> mTracks = new ArrayList<Track>();

    public ArrayList<Track> getTracks() { return mTracks; }

    public int getSize() { return mTracks.size(); }

    public Track getTrack( int idx ) {
        if ( idx < mTracks.size() && idx >= 0 )
            return mTracks.get(idx);
        else
            return null;
    }

    public int getNextTrackPosition( int idx ) {

        for ( int i = idx + 1; i < mTracks.size(); i ++ ) {
            if ( mTracks.get(i).getStatus() == STATUS_AVAILABLE )
                return i;
        }

        // cannot find any available track after idx
        for ( int i = 0; i < idx; i ++ ) {
            if ( mTracks.get(i).getStatus() == STATUS_AVAILABLE )
                return i;
        }

        return -1;
    }

    public int getBackTrackPosition( int idx ) {

        for ( int i = idx - 1; i >= 0; i -- ) {
            if ( mTracks.get(i).getStatus() == STATUS_AVAILABLE )
                return i;
        }

        // cannot find any available track after idx
        for ( int i = mTracks.size() - 1; i > idx; i -- ) {
            if ( mTracks.get(i).getStatus() == STATUS_AVAILABLE )
                return i;
        }

        return -1;
    }

    // constructor
    public TrackManager() {

        Track[] tracks = new Track[] {
                new Track( R.drawable.c4, R.drawable.c4u, STORAGE_TYPE_ASSET, "c4.mp4" ),
                new Track( R.drawable.c5, R.drawable.c5u, STORAGE_TYPE_ASSET, "c5.mp4" ),
                new Track( R.drawable.d2, R.drawable.d2u, STORAGE_TYPE_ASSET, "d2.mp4" ),
                new Track( R.drawable.d3, R.drawable.d3u, STORAGE_TYPE_ASSET, "d3.mp4" ),
                new Track( R.drawable.d4, R.drawable.d4u, STORAGE_TYPE_ASSET, "d4.mp4" ),
                new Track( R.drawable.d6, R.drawable.d6u, STORAGE_TYPE_ASSET, "d6.mp4" ),
                new Track( R.drawable.d9, R.drawable.d9u, STORAGE_TYPE_ASSET, "d9.mp4" ),
                new Track( R.drawable.d10, R.drawable.d10u, STORAGE_TYPE_ASSET, "d10.mp4" ),
                new Track( R.drawable.f2, R.drawable.f2u, STORAGE_TYPE_ASSET, "f2.mp4" ),
                new Track( R.drawable.f3, R.drawable.f3u, STORAGE_TYPE_ASSET, "f3.mp4" ),
                new Track( R.drawable.f4, R.drawable.f4u, STORAGE_TYPE_ASSET, "f4.mp4" ),
                new Track( R.drawable.g2, R.drawable.g2u, STORAGE_TYPE_ASSET, "g2.mp4" ),
                new Track( R.drawable.h2, R.drawable.h2u, STORAGE_TYPE_ASSET, "h2.mp4" ),
                new Track( R.drawable.i2, R.drawable.i2u, STORAGE_TYPE_ASSET, "i2.mp4" )
        };



        mTracks = new ArrayList<>(Arrays.asList(tracks));
    }

    public int getOrder( Track track ) {
        for ( int i = 0; i < mTracks.size(); i ++ ) {
            TrackManager.Track localTrack = mTracks.get(i);

            // int iconResId, int iconNotAvailableResId, int storageType, String filename
            if (localTrack.mIconResId == track.mIconResId &&
                    localTrack.mIconNotAvailableResId == track.mIconNotAvailableResId &&
                    localTrack.mStorageType == track.mStorageType &&
                    localTrack.getFilename().compareTo( track.getFilename() ) == 0 ) {
                return i;
            }
        }

        return -1;
    }
}
