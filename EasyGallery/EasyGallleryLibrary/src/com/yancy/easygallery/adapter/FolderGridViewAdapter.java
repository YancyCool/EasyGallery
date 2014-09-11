package com.yancy.easygallery.adapter;

import java.util.ArrayList;

import com.yancy.easygallery.lazyGallery.LazyGallery;
import com.yancy.easygallery.lazyGallery.LazyGallery.ImageCallback;
import com.yancy.easygallery.model.FolderItem;
import com.yancy.easygallery.widget.SquareImageView;
import com.yancy.easygalllerylibrary.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FolderGridViewAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<FolderItem> mFolder;
	private LayoutInflater mInflater;
	private LazyGallery lazyGallery;
	
	public FolderGridViewAdapter(Context context,ArrayList<FolderItem> folder) {
		mContext = context;
		mFolder = folder;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		lazyGallery = new LazyGallery();
	}

	@Override
	public int getCount() {
		return mFolder.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if(convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(
					R.layout.item_grid_folder, null);
			viewHolder.folderIcon = (SquareImageView) convertView.findViewById(R.id.item_img_gridview_folder);
			viewHolder.folderName = (TextView) convertView.findViewById(R.id.item_gridview_tx);
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.folderName.setText(mFolder.get(position).getFolderName());
		viewHolder.folderIcon.setImageBitmap(lazyGallery.loadGallery(mContext, mFolder.get(position).getFolderIconUrl(), viewHolder.folderIcon, new ImageCallback() {
			public void imageLoaded(Bitmap bitmap) {
				viewHolder.folderIcon.setImageBitmap(bitmap);
				Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in);
				viewHolder.folderIcon.setAnimation(animation);
				viewHolder.folderName.setAnimation(animation);
			}
		}));
		return convertView;
	}
	
	private class ViewHolder {
		SquareImageView folderIcon;
		TextView folderName;
	}	
	
	public void clearViews() {
		lazyGallery.clearImgCache();
	}
}
