package com.yancy.easygallery.lazyGallery;


import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import com.yancy.easygallery.cache.DiskLruImageCache;
import com.yancy.easygallery.utils.BitmapUtils;
import com.yancy.easygallery.utils.Md5;

public class LazyGallery {
	private ExecutorService mThreadPool;
	private static final int THREAD_POOL_SIZE = 10;
	private LruCache<String, Bitmap> imageCache;
	private DiskLruImageCache diskImgCache;
	private static final int DISK_MAX_SIZE = 10 * 1024 * 1024;// SD 32MB
	private static final int IMG_QUALITY = 70;
	public interface ImageCallback{
		public void imageLoaded(Bitmap bitmap);		
	}
	
	public LazyGallery(){
		mThreadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
		int maxMemory = (int) Runtime.getRuntime().maxMemory();  
        // ����ͼƬ�����С�?maxMemory��1/6  
        int cacheSize = maxMemory/10;  
          
        imageCache = new LruCache<String, Bitmap>(cacheSize) {  
            @Override  
            protected int sizeOf(String key, Bitmap bitmap) {  
                return  bitmap.getRowBytes() * bitmap.getHeight();
            }  
        };  
        File cacheDir = new File(  
                Environment.getExternalStorageDirectory().getAbsolutePath() +   
                File.separator +   
                "beautyex");  
        diskImgCache = new DiskLruImageCache("BeautyExAlbum", DISK_MAX_SIZE, CompressFormat.JPEG, IMG_QUALITY);
	}
	
	/**
	 * Clears all instance data and stops running threads
	 */
	private void reset() {
	    ExecutorService oldThreadPool = mThreadPool;
	    mThreadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
	    oldThreadPool.shutdownNow();	   
	}
	
	public Bitmap loadGallery(final Context context, final String tmpStrId , final ImageView imageView, final ImageCallback imageCallback) {
		Bitmap bmp = imageCache.get(tmpStrId);
		if(bmp!=null){
			return bmp; 
		}
		
		final Handler handler = new Handler(){
			@Override
            public void handleMessage(Message message) {
//				if(imageView.isShown())
				if (message.obj != null) {
					Bitmap bmp = (Bitmap) message.obj;
					imageCache.put(tmpStrId,bmp);
	                imageCallback.imageLoaded((Bitmap) message.obj);
				}				
            }
		};
		 
		mThreadPool.submit(new Runnable() {	
			public void run() {
				
				Bitmap bmp = diskImgCache.getBitmap(Md5.encode(tmpStrId));
				if(bmp==null){
					Options options = new BitmapFactory.Options();
					options.inJustDecodeBounds = true;
					BitmapFactory.decodeFile(tmpStrId, options);
					int scale = options.outHeight>options.outWidth?options.outWidth/imageView.getWidth():options.outHeight/imageView.getWidth();
					options.inJustDecodeBounds = false;
					options.inSampleSize = scale;
					bmp = BitmapFactory.decodeFile(tmpStrId, options);
					Bitmap rotateBitmap = BitmapUtils.getRotateBitmap(bmp, tmpStrId);
					if(rotateBitmap!=bmp)bmp.recycle();
					bmp = BitmapUtils.createScaleBitmap(rotateBitmap, imageView.getWidth(), imageView.getWidth());
					if (!diskImgCache.containsKey(Md5.encode(tmpStrId))) {   
						 diskImgCache.put(Md5.encode(tmpStrId), bmp); 
	                }  
				}
                Message message = Message.obtain();  
                message.obj = bmp;
                handler.sendMessage(message);	           
//				}
			}
		});		
		return null;		
	}
	
	public void clearImgCache() {
		imageCache.evictAll();;
	}
}
