package com.pp.idea;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.content.Context;

import com.pp.idea.mode.IdeaPic;
import com.pp.idea.mode.PhotoUpload;

import de.greenrobot.event.EventBus;

public class PhotoSelectController {

	public static PhotoSelectController getFromContext(Context context) {
		return MyApplication.getApplication(context)
				.getPhotoSelectController();
	}

	private Context mContext;
	private ArrayList<PhotoUpload> mSelectedPhotoList;//已经选择的
	private ArrayList<PhotoUpload> mNotSeleteList; //不可选中的
	private ArrayList<IdeaPic> mNewAddList;//新添加的，返回的

	PhotoSelectController(Context context) {
		this.mContext = context;
		mSelectedPhotoList = new ArrayList<PhotoUpload>();
		mNotSeleteList = new ArrayList<PhotoUpload>();
		mNewAddList = new ArrayList<IdeaPic>();
	}
	
	public ArrayList<PhotoUpload> getNotSeleteList() {
		return mNotSeleteList;
	}
	
	public ArrayList<IdeaPic> getNewAddList() {
		return mNewAddList;
	}
	
	public IdeaPic getNewAddPhotoAtPosition(int index){
		return mNewAddList.get(index);
	}

	public void addNewAddPhoto(IdeaPic content) {
		mNewAddList.add(content);
	}

	public synchronized void setNotSelect(ArrayList<PhotoUpload> notSeletes){
		mNotSeleteList.addAll(notSeletes);
	}
	
	public synchronized void addNotSelect(PhotoUpload photoUpload){
		mNotSeleteList.add(photoUpload);
	}
	
	public synchronized void removeNotSelect(PhotoUpload photoUpload){
		mNotSeleteList.remove(photoUpload);
	}

	public synchronized void addSelections(List<PhotoUpload> selections) {
		HashSet<PhotoUpload> currentSelectionsSet = new HashSet<PhotoUpload>(
				mSelectedPhotoList);
		boolean listModified = false;

		for (final PhotoUpload selection : selections) {
			if (!currentSelectionsSet.contains(selection)&&!mNotSeleteList.contains(selection)) {

				selection.setUploadState(PhotoUpload.STATE_SELECTED);
				mSelectedPhotoList.add(selection);
				listModified = true;
			}
		}
		
		if (listModified) {
//            postEvent(new PhotoSelectionEvent());
        }

	}

	public synchronized boolean addSelection(final PhotoUpload selection) {
		boolean result = false;

		if (!mSelectedPhotoList.contains(selection)) {
			selection.setUploadState(PhotoUpload.STATE_SELECTED);
			mSelectedPhotoList.add(selection);

			// TODO ? Save to Database
			
//			postEvent(new PhotoSelectionEvent());
			result = true;
		}

		return result;
	}

	public boolean removeSelection(final PhotoUpload selection) {
		boolean removed = false;
		synchronized (this) {
			removed = mSelectedPhotoList.remove(selection);
		}

		if (removed) {
			// TODO ? Delete from Database

			// Reset State (as may still be in cache)
			selection.setUploadState(PhotoUpload.STATE_NONE);
//			postEvent(new PhotoSelectionEvent());
		}

		return removed;
	}

	public synchronized boolean isSelected(PhotoUpload selection) {
		return mSelectedPhotoList.contains(selection);
	}
	
	/**
	 * 是否包含不能被点击的项
	 * @param selection
	 * @return
	 */
	public synchronized boolean isNotSelected(PhotoUpload selection) {
		return mNotSeleteList.contains(selection);
	}
	
	public synchronized ArrayList<PhotoUpload> getSelected(){
		return mSelectedPhotoList;
	}

	public synchronized void clearSelected() {
		if (!mSelectedPhotoList.isEmpty()) {

			// Delete from Database
			// Reset States (as may still be in cache)
			for (PhotoUpload upload : mSelectedPhotoList) {
				upload.setUploadState(PhotoUpload.STATE_NONE);
			}

			// Clear from memory
			mSelectedPhotoList.clear();
//			postEvent(new PhotoSelectionEvent());
		}
	}
	
	public synchronized void clearNewAdd() {
		if(!mNewAddList.isEmpty()){
			mNewAddList.clear();
		}
	}
	
	public synchronized void clearNotSelected(){
		if(!mNotSeleteList.isEmpty()){
			mNotSeleteList.clear();
		}
	}

	public synchronized boolean hasSelections() {
		return !mSelectedPhotoList.isEmpty();
	}
	
	public synchronized boolean hasNotSelections() {
		return !mNotSeleteList.isEmpty();
	}

	private void postEvent(Object event) {
		EventBus.getDefault().post(event);
	}

	public int getSelectedCount() {
		return mSelectedPhotoList.size()+mNotSeleteList.size();
	}
	
	public int getNewSelectedCount(){
		return mSelectedPhotoList.size();
	}
}
