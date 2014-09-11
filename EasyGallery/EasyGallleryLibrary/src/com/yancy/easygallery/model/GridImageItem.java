package com.yancy.easygallery.model;

public class GridImageItem {
	
	private boolean isChecked;
	private int id;
	private int dataColumnIndex;
	private boolean thumbnailsselection = false;
	private String arrPath;	
	
	public boolean isThumbnailsselection() {
		return thumbnailsselection;
	}
	public void setThumbnailsselection(boolean thumbnailsselection) {
		this.thumbnailsselection = thumbnailsselection;
	}	
	public String getArrPath() {
		return arrPath;
	}
	public boolean isChecked() {
		return isChecked;
	}
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	public void setArrPath(String arrPath) {
		this.arrPath = arrPath;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getDataColumnIndex() {
		return dataColumnIndex;
	}
	public void setDataColumnIndex(int dataColumnIndex) {
		this.dataColumnIndex = dataColumnIndex;
	}
}
