package com.tsb.tv.fragment.menu;

import com.tsb.tv.R;
import com.tsb.tv.TvManagerHelper;
import com.tsb.tv.menu.MenuItem;
import com.tsb.tv.menu.MenuItem.OnValueChangeListener;

import java.util.List;

public class SoundMenuFragment extends BaseMenuFragment {
    /*
    private static final int[] VALUE_MTS_PRIORITY = {
      TvManager.MTS_PRIORITY_AUTO,
      TvManager.MTS_PRIORITY_MONO,
      TvManager.MTS_PRIORITY_STEREO,
      TvManager.MTS_PRIORITY_SAP,
    };
    
    private static final int[] VALUE_MTS_DUAL = {
        TvManager.MTS_DUAL1,
        TvManager.MTS_DUAL2,
      };*/

	@Override
	public void onCreateMenuItems(List<MenuItem> items) {
		final TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
//		Resources r = getResources();
		
		/*// MTS
		items.add(
			MenuItem.createSpinnerItem(R.string.STRING_S_MTS)
			.setSpinnerOptionsByArray(r.getStringArray(R.array.mts), VALUE_MTS_PRIORITY)
			.setCurrentValue(tm.mTvManager.getMtsPriority())
			.setOnValueChangeListener(new OnValueChangeListener() {
                
                @Override
                public void onValueChanged(MenuItem item, int value) {
                    tm.mTvManager.setMtsPriority(value);
                }
            })
		);*/
		
		/*// Dual
		items.add(
			MenuItem.createSpinnerItem(R.string.STRING_S_DUAL)
			.setSpinnerOptionsByArray(r.getStringArray(R.array.dual), VALUE_MTS_DUAL)
            .setCurrentValue(tm.mTvManager.getMtsDualPriority())
            .setOnValueChangeListener(new OnValueChangeListener() {
                
                @Override
                public void onValueChanged(MenuItem item, int value) {
                    tm.mTvManager.setMtsDualPriority(value);
                }
            })
		);*/
		
		// Balance
		items.add(
			MenuItem.createSeekBarItem(R.string.STRING_S_BALANCE)
			.setBoundary(TvManagerHelper.AUDIO_BALANCING_MIN, TvManagerHelper.AUDIO_BALANCING_MAX)
			.setCurrentProgress(tm.getAudioBalancing())
			.setOnValueChangeListener(new OnValueChangeListener() {
				
				@Override
				public void onValueChanged(MenuItem item, int value) {
					tm.setAudioBalancing(value);
				}
			})
		);
		
		/*// TODO: Advanced Sound Setting
		items.add(
			MenuItem.createTextItem(R.string.STRING_S_AD_SOUND_SETTING)
			.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
				}
			})
			.setEnable(false)
		);*/
		
		/*// Audio Distortion Control
		items.add(
			MenuItem.createSpinnerItem(R.string.STRING_S_AUDIO_DIST_CTRL)
			.addSpinnerOption(R.string.STRING_ON)
			.addSpinnerOption(R.string.STRING_OFF)
			.setCurrentPosition(tm.isAudioDistortionControlEnabled()? 0 : 1)
			.setOnValueChangeListener(new OnValueChangeListener() {
				
				@Override
				public void onValueChanged(MenuItem item, int value) {
					tm.setAudioDistortionControl(value==R.string.STRING_ON);
				}
			})
		);*/
		
		/*// TV Mounting
		items.add(
			MenuItem.createSpinnerItem(R.string.STRING_S_TV_MOUNT)
			.addSpinnerOption(R.string.STRING_STAND, TvManagerHelper.TV_MOUNTING_STAND)
			.addSpinnerOption(R.string.STRING_WALL, TvManagerHelper.TV_MOUNTING_WALL)
			.setCurrentValue(tm.getTvMounting())
			.setOnValueChangeListener(new OnValueChangeListener() {
				
				@Override
				public void onValueChanged(MenuItem item, int value) {
					tm.setTvMounting(value);
				}
			})
		);*/
		
		/*// Dynamic Range Control
		items.add(
			MenuItem.createSpinnerItem(R.string.STRING_S_DYNAMIC_RANGE_CTRL)
			.addSpinnerOption(R.string.STRING_ON)
			.addSpinnerOption(R.string.STRING_OFF)
			.setCurrentValue(tm.isDynamicRangeControlEnabled()? 0 : 1)
			.setOnValueChangeListener(new OnValueChangeListener() {
				
				@Override
				public void onValueChanged(MenuItem item, int value) {
					tm.setDynamicRangeControlEnabled(value == R.string.STRING_ON);
				}
			})
		);*/
		
		/*// Audio Level Offset
		items.add(
			MenuItem.createSeekBarItem(R.string.STRING_S_AUDIO_OFFSET)
			.setBoundary(TvManagerHelper.AUDIO_VOLUME_OFFSET_MIN,
				TvManagerHelper.AUDIO_VOLUME_OFFSET_MAX)
			.setCurrentProgress(tm.getAudioLevelOffset())
			.setOnValueChangeListener(new OnValueChangeListener() {
				
				@Override
				public void onValueChanged(MenuItem item, int value) {
					tm.setAudioLevelOffset(value);
				}
			})
		);*/
	}

	@Override
	public int getTitle() {
		return R.string.STRING_SOUND;
	}

}
