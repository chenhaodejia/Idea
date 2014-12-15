package com.pp.idea.mode;

import java.io.Serializable;

public class IdeaPic implements Serializable{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1784932015784L;
	
	private PhotoUpload photo;
	private String text;

	public PhotoUpload getPhoto() {
		return photo;
	}

	public void setPhoto(PhotoUpload photo) {
		this.photo = photo;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
