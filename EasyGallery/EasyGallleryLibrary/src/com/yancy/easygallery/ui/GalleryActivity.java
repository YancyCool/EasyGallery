package com.yancy.easygallery.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.yancy.easygallery.adapter.ImgChooserAdapter;
import com.yancy.easygallery.model.GridImageItem;
import com.yancy.easygalllerylibrary.R;

public class GalleryActivity extends Activity{

	private static final String INTENT_KEY_FOLDER_NAME = "folder_name";
	private static final String INTENT_KEY_PICTURE_NAME = "picture_name";
	
	protected static final int REQUEST_CODE_CHOOSE_FOLDER = 0x01;
	private ProgressDialog mProgressDialog;
	private GridView mGridView;
	private ImgChooserAdapter mGalleryImgAdapter;
	private ArrayList<GridImageItem> mlistData;
	private View mFolderBtn;
	private TextView mTitleView;
	private ImageView mBackBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);
		initViews();
		new LoadImageGalleryTask(this,"").execute();
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
		if(resultCode == RESULT_OK&&data!=null) {
			mTitleView.setText(data.getStringExtra(INTENT_KEY_FOLDER_NAME));
			new LoadImageGalleryTask(this,data.getStringExtra(INTENT_KEY_FOLDER_NAME)).execute();
		}
	}
	
	private void initViews() {
		initProgressDialog();
		initGridView();
		initFolderBtn();
		initTitleText();
		initBackBtn();
	}
	
	private void initBackBtn() {
		mBackBtn = (ImageView)findViewById(R.id.gallery_back);
		mBackBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void initTitleText() {
		mTitleView = (TextView)findViewById(R.id.gallery_title);
	}

	private void initFolderBtn() {
		mFolderBtn = findViewById(R.id.gallery_choose_folder);
		mFolderBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(GalleryActivity.this,GalleryFolderActivity.class);
				startActivityForResult(intent,REQUEST_CODE_CHOOSE_FOLDER);
			}
		});
	}

	private void initGridView() {
		mGridView = (GridView)findViewById(R.id.gallry_gridview);
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				intent.putExtra(INTENT_KEY_PICTURE_NAME, mlistData.get(position).getArrPath());
				setResult(RESULT_OK, intent);
				finish();
			}
			
		});
	}

	private void initProgressDialog() {
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage("Loading... Please wait...");
		mProgressDialog.setCancelable(false);
		mProgressDialog.setIndeterminate(true);		
	}

	private class LoadImageGalleryTask extends AsyncTask<Void, Void, Void>{		
		private Context context;
		private Cursor imagecursor = null;
		private int image_column_index;
		private String folderName;
		
		public LoadImageGalleryTask(Context context,String folderName){
			this.context = context;
			this.folderName = folderName;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog.show();
			mlistData = new ArrayList<GridImageItem>();
			CursorLoader loader;
			String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
			String orderBy = MediaStore.Images.Media._ID;	
			
			if(folderName.equals("")) {
				loader = new CursorLoader(context, 
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, 
						columns,null, null, orderBy+" DESC");
			}else {
				loader = new CursorLoader(context, 
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, 
						columns,MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " =?", 
						new String[]{folderName}, orderBy+" DESC");
			}
			imagecursor = loader.loadInBackground();			
		}

		@Override
		protected Void doInBackground(Void... params) {	
			if(imagecursor==null)return null;
			image_column_index = imagecursor.getColumnIndex(MediaStore.Images.Media._ID);
			int count = imagecursor.getCount();
			for (int i = 0; i <count; i++) {
				imagecursor.moveToPosition(i);
				int id = imagecursor.getInt(image_column_index);
				int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);				
				String arrPath = imagecursor.getString(dataColumnIndex);
				
				GridImageItem item = new GridImageItem();
				item.setId(id);
				item.setDataColumnIndex(dataColumnIndex);
				item.setArrPath(arrPath);				
				mlistData.add(item);
			}												
			return null;
		}	
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if(imagecursor==null)return ;
			mGalleryImgAdapter = new ImgChooserAdapter(GalleryActivity.this, mlistData);
			mGridView.setAdapter(mGalleryImgAdapter);
			imagecursor.close();
			mProgressDialog.dismiss();
		}
	}
	
}
