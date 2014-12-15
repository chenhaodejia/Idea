/*
 * Copyright 2013 Chris Banes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pp.idea.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.ImageView;

import com.pp.idea.R;

public class CheckableImageView extends ImageView implements Checkable {

    public CheckableImageView(Context context) {
        super(context);
    }

    public CheckableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private static final int[] CheckedStateSet = {android.R.attr.state_checked};

    private boolean mChecked = false;
    private boolean mEnabled = true;

    public boolean isChecked() {
        return mChecked;
    }
    
    public boolean isMEnabled() {
    	return mEnabled;
    }
    
    public void setMEnabled(boolean enabled){
		mEnabled = enabled;
		if (enabled) {
			if(!isChecked()){
				setImageDrawable(null);
			}else{
				setImageResource(R.drawable.overlay);
			}
		} else {
			setImageResource(R.drawable.overlay_click);
		}
    }

    public void setChecked(boolean b) {
        if (b != mChecked) {
            mChecked = b;
            refreshDrawableState();
        }
        if (isMEnabled()) {
			if(!isChecked()){
				setImageDrawable(null);
			}else{
				setImageResource(R.drawable.overlay);
			}
		} else {
			setImageResource(R.drawable.overlay_click);
		}
    }

    public void toggle() {
        setChecked(!mChecked);
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CheckedStateSet);
        }
        return drawableState;
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        invalidate();
    }

}