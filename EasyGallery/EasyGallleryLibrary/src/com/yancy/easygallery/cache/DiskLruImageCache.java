package com.yancy.easygallery.cache;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class DiskLruImageCache {

    private DiskLruCache2 mDiskCache;
    private CompressFormat mCompressFormat = CompressFormat.JPEG;
    private int mCompressQuality = 70;
    private static final int APP_VERSION = 1;
    private static final int VALUE_COUNT = 1;
    public static final int IO_BUFFER_SIZE = 8 * 1024;
    private static final String TAG = "DiskLruImageCache";

    public DiskLruImageCache(String uniqueName, int diskCacheSize,
        CompressFormat compressFormat, int quality ) {
        try {
                final File diskCacheDir = getDiskCacheDir(uniqueName );
                mDiskCache = DiskLruCache2.open( diskCacheDir, APP_VERSION, VALUE_COUNT, diskCacheSize );
                mCompressFormat = compressFormat;
                mCompressQuality = quality;
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    private boolean writeBitmapToFile( Bitmap bitmap, DiskLruCache2.Editor editor )
        throws IOException, FileNotFoundException {
        OutputStream out = null;
        try {
            out = new BufferedOutputStream( editor.newOutputStream( 0 ),IO_BUFFER_SIZE );
            return bitmap.compress( mCompressFormat, mCompressQuality, out );
        } finally {
            if ( out != null ) {
                out.close();
            }
        }
    }

    private File getDiskCacheDir(String uniqueName) {

    // Check if media is mounted or storage is built-in, if so, try and use external cache dir
    // otherwise use internal cache dir
    	 File cacheDir = new File(  
                 Environment.getExternalStorageDirectory().getAbsolutePath() +   
                 File.separator +   
                 uniqueName);  

        return cacheDir;
    }

    public void put( String key, Bitmap data ) {

        DiskLruCache2.Editor editor = null;
        try {
            editor = mDiskCache.edit( key );
            if ( editor == null ) {
                return;
            }

            if( writeBitmapToFile( data, editor ) ) {               
                mDiskCache.flush();
                editor.commit();
            } else {
                editor.abort();
            }   
        } catch (IOException e) {
            try {
                if ( editor != null ) {
                    editor.abort();
                }
            } catch (IOException ignored) {
            }           
        }

    }

    public Bitmap getBitmap( String key ) {

        Bitmap bitmap = null;
        DiskLruCache2.Snapshot snapshot = null;
        try {

            snapshot = mDiskCache.get( key );
            if ( snapshot == null ) {
                return null;
            }
            final InputStream in = snapshot.getInputStream( 0 );
            if ( in != null ) {
                final BufferedInputStream buffIn = 
                new BufferedInputStream( in,IO_BUFFER_SIZE );
                bitmap = BitmapFactory.decodeStream( buffIn );              
            }   
        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
            if ( snapshot != null ) {
                snapshot.close();
            }
        }
        return bitmap;

    }

    public boolean containsKey( String key ) {

        boolean contained = false;
        DiskLruCache2.Snapshot snapshot = null;
        try {
            snapshot = mDiskCache.get( key );
            contained = snapshot != null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if ( snapshot != null ) {
                snapshot.close();
            }
        }

        return contained;

    }

    public void clearCache() {
        try {
            mDiskCache.delete();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public File getCacheFolder() {
        return mDiskCache.getDirectory();
    }

}
