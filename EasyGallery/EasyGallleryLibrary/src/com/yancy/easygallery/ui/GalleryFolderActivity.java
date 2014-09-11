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

import com.yancy.easygallery.adapter.FolderGridViewAdapter;
import com.yancy.easygallery.model.FolderItem;
import com.yancy.easygalllerylibrary.R;

public class GalleryFolderActivity extends Activity{
	
	private ProgressDialog mProgressDialog;
	private GridView mGridView;
	private FolderGridViewAdapter mGalleryFolderAdapter;
	private ArrayList<FolderItem> mlistData;
	private ImageView mBackBtn;
	private static final String INTENT_KEY_FOLDER_NAME = "folder_name";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery_folder);
		initViews();
		new LoadFolderGalleryTask(this).execute();
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
	
	private void initViews() {
		initProgressDialog();
		initGridView();
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
	
	private void initGridView() {
		mGridView = (GridView)findViewById(R.id.gallry_gridview);
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String folderName = mlistData.get(position).getFolderName();
				Intent intent = new Intent();
				intent.putExtra(INTENT_KEY_FOLDER_NAME, folderName);
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
	
	private class LoadFolderGalleryTask extends AsyncTask<Void, Void, Void>{		
		private Cursor imagecursor = null;
		private Context mContext;
		
		public LoadFolderGalleryTask(Context context){
			mContext = context;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mlistData = new ArrayList<FolderItem>();
			String[] columns = { MediaStore.Images.Media.BUCKET_DISPLAY_NAME,"max("+MediaStore.Images.Media._ID+")as maxid",
					MediaStore.Images.Media.DATA+" as maxid_url"};
			CursorLoader loader = new CursorLoader(mContext, 
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, 
					"1=1) group by("+MediaStore.Images.Media.BUCKET_DISPLAY_NAME, 
					null,
					MediaStore.Images.Media._ID+" desc");
			imagecursor = loader.loadInBackground();	
		}

		@Override
		protected Void doInBackground(Void... params) {	
			int image_column_index = imagecursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
			int icon_id_url = imagecursor.getColumnIndex("maxid_url");
			int count = imagecursor.getCount();
			
			for (int i = 0; i <count; i++) {
				FolderItem folderItem = new FolderItem();
				imagecursor.moveToPosition(i);
				folderItem.setFolderName(imagecursor.getString(image_column_index));
				folderItem.setFolderIconUrl(imagecursor.getString(icon_id_url));
				mlistData.add(folderItem);
			}
			return null;
		}	
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if(imagecursor==null)return ;
			mGalleryFolderAdapter = new FolderGridViewAdapter(GalleryFolderActivity.this, mlistData);
			mGridView.setAdapter(mGalleryFolderAdapter);
			imagecursor.close();
			mProgressDialog.dismiss();
		}
	}
	
}
