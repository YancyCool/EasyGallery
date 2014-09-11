package com.yancy.gallerysmaple;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.yancy.easygallery.ui.GalleryActivity;
import com.yancy.easygallery.utils.BitmapUtils;


public class MainActivity extends Activity {

    private ImageView mChoosePictureBtn;
	private ImageView mShowingImageView;
    private static final int INTENT_REQUEST_CODE_CHOOSE_PICTURE = 0x00;
    private static final String INTENT_KEY_PICTURE_NAME = "picture_name";

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initChoosePictureBtn();
        initShowingView();
    }
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//to handle the data return from Gallery
		if(requestCode==INTENT_REQUEST_CODE_CHOOSE_PICTURE&&resultCode==RESULT_OK) {
			if(data==null) {
				Toast.makeText(this, R.string.choose_picture_failed, Toast.LENGTH_SHORT).show();
			}else {
				String path = data.getStringExtra(INTENT_KEY_PICTURE_NAME);
				mShowingImageView.setImageBitmap(getBitmap(path));
			}
		}
	}

	private Bitmap getBitmap(String path) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		
		int widthRatio = (int) Math.ceil(options.outWidth / (float) mShowingImageView.getWidth());
		int heightRatio = (int) Math.ceil(options.outHeight / (float) mShowingImageView.getHeight());
		if (widthRatio > 1 && widthRatio > 1) {
			if (widthRatio > heightRatio) {
				options.inSampleSize = heightRatio;
			} else {
				options.inSampleSize = widthRatio;
			}
		}
		
		options.inJustDecodeBounds = false;
		Bitmap bmp = BitmapFactory.decodeFile(path, options);
		Bitmap rotateBitmap = BitmapUtils.getRotateBitmap(bmp, path);
		return rotateBitmap;
	}
	
	private void initShowingView() {
		mShowingImageView = (ImageView)findViewById(R.id.view_for_gallery_picture);
	}

	private void initChoosePictureBtn() {
		mChoosePictureBtn = (ImageView)findViewById(R.id.choose_img_btn);
        mChoosePictureBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,GalleryActivity.class);
				startActivityForResult(intent, INTENT_REQUEST_CODE_CHOOSE_PICTURE);
			}
		});
	}

}
