package com.tsb.settings.menu;

import com.tsb.settings.util.LogUtil;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListAdapter;
import android.widget.ListView;

/**重写ListView，使得非触屏模式下INVALID_POSITION有作用*/
public class DeenListView extends ListView{
	private static final String TAG = "DeenListView";
	private boolean enableInvalidPosition;

	public DeenListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public void setSelection(int position, boolean invalidPosition) {
        enableInvalidPosition = invalidPosition;
        setSelectionFromTop(position, 0);
    }
	
	public void enableInvalidPosition(boolean invalidPosition) {
		LogUtil.logD(TAG,"enableInvalidPosition(),invalidPosition=" + invalidPosition, true);
		enableInvalidPosition = invalidPosition;
	}
	
	public boolean isInvalidPosition() {
		return enableInvalidPosition;
	}
	
	
	int lookForSelectablePosition(int position, boolean lookDown) {
        final ListAdapter adapter = super.getAdapter();
        if (adapter == null || isInTouchMode() || enableInvalidPosition) {
            return INVALID_POSITION;
        }

        final int count = adapter.getCount();
//        if (!mAreAllItemsSelectable) {
            if (lookDown) {
                position = Math.max(0, position);
                while (position < count && !adapter.isEnabled(position)) {
                    position++;
                }
            } else {
                position = Math.min(position, count - 1);
                while (position >= 0 && !adapter.isEnabled(position)) {
                    position--;
                }
            }
//        }

        if (position < 0 || position >= count) {
            return INVALID_POSITION;
        }

        return position;
    }
}
