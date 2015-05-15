package com.tsb.settings.fragment.menu;

import java.util.List;

import android.app.TvManager;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.tsb.settings.R;
import com.tsb.settings.TvManagerHelper;
import com.tsb.settings.fragment.menu.pic.SoundAdvanceSettingFragment;
import com.tsb.settings.fragment.menu.pic.SoundModeSettingFragment;
import com.tsb.settings.menu.MenuItem;
import com.tsb.settings.menu.OptionMenuItem;
import com.tsb.settings.util.FragmentUtils;

public class SoundMenuFragment extends SecondLevelMenuFragment {
	//标志
	private int effect, scene, smartvolumn, currentSpdif;
	private OptionMenuItem audioEffect = MenuItem
			.createOptionItem(R.string.audio_effect);
	// private OptionMenuItem audioScene =
	// MenuItem.createOptionItem(R.string.audio_scene);
	private OptionMenuItem audioSmartVolume = MenuItem
			.createOptionItem(R.string.audio_smartvolume);
	private OptionMenuItem audioSpdif = MenuItem
			.createOptionItem(R.string.audio_spdif);
	private OptionMenuItem audioAdvance = MenuItem
			.createOptionItem(R.string.audio_advance);
	private OptionMenuItem voicelanguage;
	private int[] mVoicelangs;
	private int curLang = 0;
	private String[] mVoiceLangItems;
	private TvManager tvmm;
	String[] sound_effects, sound_scenes, sound_smartvolumn, sound_spdif;
	private final int[] VALUE_AUDIO_EFFECT = {
			TvManagerHelper.TV_STRING_NATURAL,// 标准
			TvManagerHelper.TV_STRING_MUSIC,// 音乐厅
			TvManagerHelper.TV_STRING_NEWS,// 新闻
			TvManagerHelper.TV_STRING_CINEMA,// 电影院
			TvManagerHelper.TV_STRING_AUTO_USER, // 自定义
			TvManagerHelper.TV_STRING_SURROUND // 虚拟环绕声
	};
	private final int[] VALUE_AUDIO_EFFECT_TOSHIBA = {
			TvManagerHelper.TV_STRING_NATURAL,// 标准
			TvManagerHelper.TV_STRING_MUSIC,// 音乐厅
//			TvManagerHelper.TV_STRING_NEWS,// 新闻
			TvManagerHelper.TV_STRING_CINEMA,// 电影院
			TvManagerHelper.TV_STRING_AUTO_USER, // 自定义
//			TvManagerHelper.TV_STRING_SURROUND, // 虚拟环绕声
			TvManagerHelper.TV_STRING_ROCKET // 火箭炮
	};
	private final int[] VALUE_AUDIO_SENCE = {
			TvManagerHelper.TV_MOUNTING_STAND,// 桌面模式
			TvManagerHelper.TV_MOUNTING_WALL // 壁挂模式
	};
	private final int[] VALUE_AUDIO_SPDIF = {
			TvManagerHelper.AUDIO_DIGITAL_RAW,// 5.1声道--改为自动
			TvManagerHelper.AUDIO_DIGITAL_LPCM_DUAL_CH,// 立体声--改为PCM
	};
	private final int[] VALUE_AUDIO_SMARTVOLUME = {
			-1,//关
			1//开
	};

	private TvManagerHelper tm;
	private String clientType;

	@Override
	public void onCreateMenuItems(List<MenuItem> items) {
		tm = TvManagerHelper.getInstance(getActivity());
		clientType = tm.mTvManager.getClientType();
		// if(clientType.equals(TvClientTypeList.ToshibaClient))
		sound_effects = getResources().getStringArray(
				R.array.sound_effects_toshiba);
		// else {
		// sound_effects = getResources().getStringArray(R.array.sound_effects);
		// }
		// sound_scenes = getResources().getStringArray(R.array.sound_scenes);
		sound_smartvolumn = getResources().getStringArray(
				R.array.sound_smartvolumn);
		sound_spdif = getResources().getStringArray(R.array.sound_spdif);

//		audioEffect.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				FragmentUtils.showSubFragment(getBaseFragment(),
//						SoundModeSettingFragment.class);
//			}
//		});
		audioEffect.mClickEqualsRightKey = true;
		items.add(audioEffect);
		// audioScene.mClickEqualsRightKey = true;
		// items.add(
		// audioScene
		// );
		audioSmartVolume.mClickEqualsRightKey = true;
		items.add(audioSmartVolume);
		audioSpdif.mClickEqualsRightKey = true;
		items.add(audioSpdif);
		voicelanguage = MenuItem.createOptionItem(R.string.voice_language);
		voicelanguage.mClickEqualsRightKey = true;
		if (updateVoiceLanguage()) {
			items.add(voicelanguage);
		}
		audioAdvance.needPopWindow(false);
		audioAdvance.setRightKeyEventEqualsOnClick(true);
		audioAdvance.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentUtils.showSubFragment(getBaseFragment(),
						SoundAdvanceSettingFragment.class);
			}
		});
		items.add(audioAdvance);
		initSoundParameters();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initSoundParameters();

	}

	public void initSoundParameters() {
		new Thread() {
			@Override
			public void run() {
				super.run();
				mHandler.sendEmptyMessageDelayed(REFRESH_ITEM, SecondLevelMenuFragment.DATA_INIT_DELAY);
			}
		}.start();
	}

	public static final int REFRESH_ITEM = 1;
	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFRESH_ITEM:
				if (!isVisible()) {
					break;
				}
				Log.i("REFRESH_ITEM", sound_effects[effect]);
				// if(clientType.equals(TvClientTypeList.ToshibaClient))
				audioEffect.setOptionsByArray(sound_effects,
						VALUE_AUDIO_EFFECT_TOSHIBA, sound_effect_listener);
				// else audioEffect.setOptionsByArray(sound_effects,
				// VALUE_AUDIO_EFFECT, sound_effect_listener);
				// audioScene.setOptionsByArray(sound_scenes,
				// VALUE_AUDIO_SENCE,sound_scene_listener);
				audioSmartVolume.setOptionsByArray(sound_smartvolumn,
						VALUE_AUDIO_SMARTVOLUME, sound_smartvolumn_listener);
				audioSpdif.setOptionsByArray(sound_spdif, VALUE_AUDIO_SPDIF,
						sound_spdif_listener);
				audioEffect.setCurrentValue(tm.GetAudioMode());
				// audioScene.needRightArraw(false);
				// audioScene.updateContent(sound_scenes[0]);
				audioSmartVolume
						.setCurrentValue(tm.GetAutoVolume() ? VALUE_AUDIO_SMARTVOLUME[1]
								: VALUE_AUDIO_SMARTVOLUME[0]);
				audioSpdif.setCurrentValue(tm.GetAudioSpdifOutput());
				// audioAdvance.setTextValue(getActivity().getString(R.string.tcl_customer_set));
				if (mItems.contains(voicelanguage)) {
					voicelanguage.setOptionsByArray(mVoiceLangItems,
							mVoicelangs, languageItemClickListener);
					voicelanguage.setCurrentValue(curLang);
				}
				break;

			default:
				break;
			}
		};
	};
	private OnItemClickListener sound_effect_listener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			// if(clientType.equals(TvClientTypeList.ToshibaClient))
			tm.SetAudioMode(VALUE_AUDIO_EFFECT_TOSHIBA[position]);
			// else
			// tm.SetAudioMode(VALUE_AUDIO_EFFECT[position]);
			audioEffect.setTextValue(sound_effects[position]);
			audioEffect.setSelection(position);
			audioEffect.getPopView().dismiss();
		}
	};

	// private OnItemClickListener sound_scene_listener = new
	// OnItemClickListener() {
	//
	// @Override
	// public void onItemClick(AdapterView<?> arg0, View arg1, int position,
	// long arg3) {
	// tm.setTvMounting(VALUE_AUDIO_SENCE[position]);;
	// audioScene.setTextValue(sound_scenes[position]);
	// audioScene.setSelection(position);
	// audioScene.getPopView().dismiss();
	// }
	// };

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		super.onItemClick(parent, view, position, id);
	}

	private OnItemClickListener sound_smartvolumn_listener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			tm.SetAutoVolume(VALUE_AUDIO_SMARTVOLUME[position] == 1);
			audioSmartVolume.setTextValue(sound_smartvolumn[position]);
			audioSmartVolume.setSelection(position);
			audioSmartVolume.getPopView().dismiss();
		}
	};

	private OnItemClickListener sound_spdif_listener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			tm.SetAudioSpdifOutput(VALUE_AUDIO_SPDIF[position]);
			audioSpdif.setTextValue(sound_spdif[position]);
			audioSpdif.setSelection(position);
			audioSpdif.getPopView().dismiss();
		}
	};

	@Override
	public int getTitle() {
		return R.string.string_null;
	}

	private boolean updateVoiceLanguage() {
		if (tm.isSignalDisplayReady()
				&& TvManagerHelper.isDtvSource(TvManagerHelper.getInstance(
						getActivity()).getInputSource())) {
			tvmm = tm.mTvManager;
			String languages[] = tvmm.getCurDtvSoundSelectList().split("\n");
			String tempLanguages = tvmm.getCurrentAudioLang();
			for (int j = 0; j < languages.length; j++) {
				if (tempLanguages.equals(languages[j])) {
					curLang = j;
					break;
				}
			}
			int langNum = tvmm.getCurDtvSoundSelectCount();
			mVoicelangs = new int[langNum];
			mVoiceLangItems = new String[langNum];
			if (langNum > 0) {
				for (int i = 0; i < langNum; i++) {
					mVoicelangs[i] = i;
					mVoiceLangItems[i] = languages[i];
				}
				return true;
			}
		}
		return false;
	}

	private OnItemClickListener languageItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			if (mVoicelangs.length > 0) {
				tvmm.setCurDtvSoundSelectByIndex(position);
				voicelanguage.setCurrentValue(position);
				voicelanguage.getPopView().dismiss();
				voicelanguage.setSelection(position);
			}
		}
	};
}
