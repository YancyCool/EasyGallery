package com.yancy.easygallery.adapter;


import java.util.ArrayList;
import java.util.LinkedList;

import com.yancy.easygallery.lazyGallery.LazyGallery;
import com.yancy.easygallery.lazyGallery.LazyGallery.ImageCallback;
import com.yancy.easygallery.model.GridImageItem;
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

public class ImgChooserAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context context;
	private LazyGallery lazyGallery;
	private ArrayList<GridImageItem> gridImageItemList;
	
	public ImgChooserAdapter(Context context,ArrayList<GridImageItem> data) {
		lazyGallery = new LazyGallery();
		this.context = context;
		gridImageItemList = data;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return gridImageItemList.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(
					R.layout.item_gridview, null);
			holder.imageview = (SquareImageView) convertView.findViewById(R.id.item_img_gridview);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.imageview.setId(position);
		holder.imageview.setTag(position);
		holder.imageview.setImageBitmap(lazyGallery.loadGallery(context, gridImageItemList.get(position).getArrPath(), holder.imageview, new ImageCallback() {				
			public void imageLoaded(Bitmap bitmap) {
				if(!((Integer)(holder.imageview.getTag())).equals((Integer)position))return;
				holder.imageview.setImageBitmap(bitmap);
				Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
				holder.imageview.setAnimation(animation);
			}
		}));
		return convertView;
	}

	public static class ViewHolder {
		public SquareImageView imageview;
	}
	
	public void clearCache() {
		lazyGallery.clearImgCache();
	}
	
}
