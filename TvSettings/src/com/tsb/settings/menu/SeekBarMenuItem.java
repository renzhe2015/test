package com.tsb.settings.menu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.tsb.settings.R;

public class SeekBarMenuItem extends MenuItem implements OnSeekBarChangeListener {
	
	// Data and status
	private int progress;
	private int max = 100;
	private int min = 0;
	public int seekBarTitle;

	// View
	private SeekBar mSeekBar;
	private TextView mTextView;
	private TextView mTitleView;
	
	SeekBarMenuItem(int title) {
		super(title, ITEM_TYPE_SEEK_BAR);
		seekBarTitle = title;
	}
	public int getCurrentProgress() {
		return progress;
	}
	
	public SeekBarMenuItem setCurrentProgress(int progress) {
		return setCurrentProgress(progress, false);
	}
	
	public SeekBarMenuItem setCurrentProgress(int progress, boolean notify) {
		this.progress = progress;
		if (notify) {
			notifyValueChange(progress);
		}
		updateContent();
		return this;
	}
	
	public SeekBarMenuItem setBoundary(int min, int max) {
		this.min = min;
		this.max = max;
		return this;
	}
	
	@Override
	public void selectNext() {
		increaseProgress();
	}

	@Override
	public void selectPrev() {
		decreaseProgress();
	}

	public int increaseProgress() {
		progress++;
		if (progress > max) {
			progress = max;
		}else
			notifyValueChange(progress);
		updateContent();
		return progress;
	}
	
	public int decreaseProgress() {
		progress--;
		if (progress < min) {
			progress = min;
		}else
			notifyValueChange(progress);
		updateContent();
		return progress;
	}
	
	@Override
	protected void onBindView(LayoutInflater inflater, ViewGroup container, boolean inflate) {
		if (inflate) {
			inflater.inflate(R.layout.layout_item_setting_progress, container);
		}
		mSeekBar = (SeekBar) container.findViewById(R.id.seek);
		mTextView = (TextView) container.findViewById(R.id.textvalue);
		mTitleView = (TextView) container.findViewById(R.id.text_title);
		mTitleView.setText(title);
		updateContent();
	}
	
	@Override
	protected void onUnbindView() {
		mSeekBar = null;
		mTextView = null;
	}
	
	private void updateContent() {
		if (mSeekBar == null) {
			return;
		}
		mSeekBar.setOnSeekBarChangeListener(null);
		mSeekBar.setMax(max - min);
		mSeekBar.setProgress(progress - min);
		mSeekBar.setOnSeekBarChangeListener(this);
		mTextView.setText(String.valueOf(progress));
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		this.progress = progress + min;
		updateContent();
		if (seekBar != mSeekBar) {
			// Not sure why this happen
			// New view for this item has been created, yet hasn't been attached to the list view!?
			// Workaround to update the TextView.
			mTextView.setText(String.valueOf(progress));
			mTextView.setVisibility(View.VISIBLE);
		}
		notifyValueChange(this.progress);
	}
	
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		
	}
	
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		
	}
	@Override
	public void showView() {
		// TODO Auto-generated method stub
		
	}
}
