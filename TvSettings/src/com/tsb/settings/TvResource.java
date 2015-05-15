package com.tsb.settings;

import android.app.TvManager;
import android.content.Context;

import java.util.Locale;

public class TvResource {
	
	public static final String getLanguageName(Context context, String languageCode) {
		return new Locale(languageCode).getDisplayName();
	}
	
	public static final int getAudioTrackIcon(int mode) {
		switch(mode) {
		case TvManager.ATV_SOUND_MODE_AUTO:
		case TvManager.ATV_SOUND_MODE_DUAL:
			return R.drawable.ic_info_audio_track_dual_left;
		case TvManager.ATV_SOUND_MODE_SAP_STEREO:
		case TvManager.ATV_SOUND_MODE_STEREO:
			return R.drawable.ic_info_audio_track_stereo;
		default:
		case TvManager.ATV_SOUND_MODE_SAP_MONO:
		case TvManager.ATV_SOUND_MODE_MONO:
			return R.drawable.ic_info_audio_track_mono;
		}
	}
  	public static final int getAudioTrackText(int mode) {
		switch(mode) {
		case TvManager.ATV_SOUND_MODE_AUTO:
		case TvManager.ATV_SOUND_MODE_DUAL:
			return R.string.left_chan;
		case TvManager.ATV_SOUND_MODE_SAP_STEREO:
		case TvManager.ATV_SOUND_MODE_STEREO:
			return R.string.stereo;
		default:
		case TvManager.ATV_SOUND_MODE_SAP_MONO:
		case TvManager.ATV_SOUND_MODE_MONO:
			return R.string.right_chan;
		}
	}
  	
  	public static final int getDtvTrackText(int mode) {
		switch(mode) {
		case TvManagerHelper.TV_STRING_CHANNEL_LEFT:
			return R.string.left_chan;
		case TvManagerHelper.TV_STRING_CHANNEL_RIGHT:
			return R.string.right_chan;
		default:
		case TvManagerHelper.TV_STRING_STEREO:
			return R.string.stereo;
		}
	}
  	
  	
	public static final String getInputSourceLabel(Context context, int sourceType) {
		int stringId = 0;
		switch (sourceType) {
		case TvManager.SOURCE_ATV1:
			stringId = R.string.ATV;
			break;
		case TvManager.SOURCE_DTV1:
			stringId = R.string.DTV;
			break;
		case TvManager.SOURCE_IDTV1:
			stringId = R.string.IDTV;
			break;
		case TvManager.SOURCE_AV1:
			stringId = R.string.AV1;
			break;
		case TvManager.SOURCE_AV2:
			stringId = R.string.AV2;
			break;
		case TvManager.SOURCE_AV3:
			stringId = R.string.AV3;
			break;
		case TvManager.SOURCE_SV1:
			stringId = R.string.SV1;
			break;
		case TvManager.SOURCE_SV2:
			stringId = R.string.SV2;
			break;
		case TvManager.SOURCE_YPP1:
			stringId = R.string.YPP1;
			break;
		case TvManager.SOURCE_VGA1:
			stringId = R.string.PC;
			break;
		case TvManager.SOURCE_HDMI1:
			stringId = R.string.HDMI1;
			break;
		case TvManager.SOURCE_HDMI2:
			stringId = R.string.HDMI2;
			break;
		case TvManager.SOURCE_HDMI3:
			stringId = R.string.HDMI3;
			break;
		case TvManager.SOURCE_BROWSER:
			stringId = R.string.BROWSER;
			break;
		case TvManager.SOURCE_PLAYBACK:
		default:
			return "";
		}
		return context.getString(stringId);
	}

	public static int getAudioFormatIcon(int audioFormat) {
		switch(audioFormat) {
		default:
			return R.drawable.ic_info_video_hd;
		}
	}

	public static int getVideoResolutionIcon(int resolution) {
		switch(resolution) {
		case TvManagerHelper.RESOLUTION_SD:
			return R.string.resolution_sd;
		case TvManagerHelper.RESOLUTION_HD:
		case TvManagerHelper.RESOLUTION_UNKNOWN:
		default:
			return R.string.resolution_hd;
		}
	}

	public static int getVideoAspectIcon(int aspect) {
		switch(aspect) {
		case TvManagerHelper.RESOLUTION_UNKNOWN:
		default:
			return R.drawable.ic_info_video_hd;
		}
	}

	public static int getMultiAudioTrackIcon(int audioCount) {
		switch(audioCount) {
		default:
			return R.drawable.ic_info_video_hd;
		}
	}

	public static int getParentRatingIcon(int parentRating) {
		switch(parentRating) {
		default:
			return R.drawable.ic_info_video_hd;
		}
	}

	public static int getGreneIcon(int grene) {
		switch(grene) {
		default:
			return R.drawable.ic_info_video_hd;
		}
	}

	public static int getSignalStrengthIcon(int signalStrength) {
		switch(signalStrength) {
		default:
			return R.drawable.ic_info_video_hd;
		}
	}
}
