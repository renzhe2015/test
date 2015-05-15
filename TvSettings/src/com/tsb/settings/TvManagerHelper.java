package com.tsb.settings;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.app.TvManager;
import android.app.tv.ChannelFilter;
import android.app.tv.ChannelInfo;
import android.app.tv.EpgData;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

public class TvManagerHelper {

	private static final String TAG = "TvManagerHelper";
	private static final boolean VERBOSE = true;
	private static final boolean BENCHMARK = true;
	private static final boolean GINGA = false;
	
	public static final boolean DEBUG = false;
	
	public static final int RESOLUTION_UNKNOWN = -1;
	public static final int RESOLUTION_HD = 0;
	public static final int RESOLUTION_SD = 1;
	
	// Aspect Ratio
	public final static int ASPECT_RATIO_UNKNOWN = 0;
	public final static int ASPECT_RATIO_PS_4_3 = 1;
	public final static int ASPECT_RATIO_LB_4_3 = 2;
	public final static int ASPECT_RATIO_16_9 =  TvManager.Wide_16_9;
	public final static int ASPECT_RATIO_16_10 = TvManager.Wide_16_10;
	public final static int SCALER_RATIO_AUTO = 5;
	public final static int SCALER_RATIO_4_3 = 6;
	public final static int SCALER_RATIO_16_9 = 7;
	public final static int SCALER_RATIO_14_9 = 8;
	public final static int SCALER_RATIO_LETTERBOX = 9;
	public final static int SCALER_RATIO_PANORAMA = 10;
	public final static int SCALER_RATIO_FIT = 11;
	public final static int SCALER_RATIO_POINTTOPOINT = 12;
	public final static int SCALER_RATIO_BBY_AUTO = 13;
	public final static int SCALER_RATIO_BBY_NORMAL = 14;
	public final static int SCALER_RATIO_BBY_ZOOM = 15;
	public final static int SCALER_RATIO_BBY_WIDE_1 = 16;
	public final static int SCALER_RATIO_BBY_WIDE_2 = 17;
	public final static int SCALER_RATIO_BBY_CINEMA = 18;
	public final static int SCALER_RATIO_CUSTOM = 19;
	public final static int SCALER_RATIO_PERSON = 20;
	public final static int SCALER_RATIO_CAPTION = 21;
	public final static int SCALER_RATIO_MOVIE = 22;
	public final static int SCALER_RATIO_ZOOM = 23;
	public final static int SCALER_RATIO_100 = 24;
	public final static int SCALER_RATIO_SOURCE = 25;
	public final static int SCALER_RATIO_ZOOM_14_9 = 26;
	public final static int SCALER_RATIO_NATIVE = 27;
	public final static int SCALER_RATIO_DISABLE = 28;
	public final static int ASPECT_RATIO_MAX = 29;
	
	public static final int PARENT_RATING_UNKNOWN = -1;
	
	public static final int GNERE_UNKNOWN = -1;
	
	// Sound Settings
	public static final int AUDIO_BALANCING_MAX = 50;
	public static final int AUDIO_BALANCING_MIN = -50;
	
	public static final int TV_MOUNTING_STAND = 0;
	public static final int TV_MOUNTING_WALL = 1;
	
	public static final int AUDIO_VOLUME_OFFSET_MAX = 50;
	public static final int AUDIO_VOLUME_OFFSET_MIN = -50;
	
	// Menu -> Preferences -> Teletext
	public static final int TELETEXT_AUTO = 0;
	public static final int TELETEXT_LIST = 1;
	
	// Menu -> Setup -> Location
	public static final int TV_LOCATION_HOME = 0;
	public static final int TV_LOCATION_STORE = 1;
	
	// Menu -> Setup -> Country
	public static final int COUNTRY_BAHRAIN = 0;
	public static final int COUNTRY_INDIA = 1;
	public static final int COUNTRY_INDONESIA = 2;
	public static final int COUNTRY_ISRAEL = 3;
	public static final int COUNTRY_KUWAIT = 4;
	public static final int COUNTRY_MALAYSIA = 5;
	public static final int COUNTRY_PHILIPPINES = 6;
	public static final int COUNTRY_QATAR = 7;
	public static final int COUNTRY_SAUDI_ARABIA = 8;
	public static final int COUNTRY_SINGAPORE = 9;
	public static final int COUNTRY_THAILAND = 10;
	public static final int COUNTRY_UNITED_ARAB_EMIRATES = 11;
	public static final int COUNTRY_VIETNAM = 12;
	
	// Menu -> PVR -> Time Shift Size
	public static final int PVR_TIME_SHIFT_SIZE_512M = 512;
	public static final int PVR_TIME_SHIFT_SIZE_1G = 1024;
	public static final int PVR_TIME_SHIFT_SIZE_2G = 2048;
	public static final int PVR_TIME_SHIFT_SIZE_4G = 4096;
	
	// TODO: Audio Format
	public static final int AUDIO_FORMAT_HE_AAC = 0;
	public static final int AUDIO_FORMAT_DD = 1;
	public static final int AUDIO_FORMAT_DD_PLUS = 2;
	
	private static final ChannelInformation CHANNEL_INFO_EMPTY = new ChannelInformation();
	private static final ChannelSchedule CHANNEL_SCHEDULE_EMPTY = new ChannelSchedule();
	
	// DTV manual tuning
//	public static final int TUNING_DTV_BANDWIDTH_8MHZ = TvManager.TUNING_BANDWIDTH_8MHZ;
//	public static final int TUNING_DTV_BANDWIDTH_7MHZ = TvManager.TUNING_BANDWIDTH_7MHZ;
//	public static final int TUNING_DTV_BANDWIDTH_7MHZ_8MHZ = TvManager.TUNING_BANDWIDTH_7MHZ_8MHZ;
	
	//图像模式
	public final static int PICTURE_MODE_USER = TvManager.PICTURE_MODE_USER;
	public final static int PICTURE_MODE_STANDARD = TvManager.PICTURE_MODE_MOVIE;
	public final static int PICTURE_MODE_BRIGHT = TvManager.PICTURE_MODE_MAX;
	public final static int PICTURE_MODE_GENTLE = TvManager.PICTURE_MODE_GENTLE;
	
	//声音模式
	public static final int TV_STRING_MUSIC = 2; // 音乐厅
	public static final int TV_STRING_AUTO_USER = 5;// 自定义
	public static final int TV_STRING_NEWS = 3;// 新闻
	public static final int TV_STRING_CINEMA = 4; // 电影院
	public static final int TV_STRING_NATURAL = 1; // 标准
	public static final int TV_STRING_SURROUND = 6;// 虚拟环绕声
	public static final int TV_STRING_ROCKET = 7;// 火箭炮
	
	//数字音频输出
	public static final int AUDIO_DIGITAL_RAW=1;
	public static final int AUDIO_DIGITAL_LPCM_DUAL_CH=2;
	//声道
	public static final int TV_STRING_STEREO = 0; //立体声
	public static final int TV_STRING_CHANNEL_LEFT = 1;//左声道
	public static final int TV_STRING_CHANNEL_RIGHT= 2;//右声道
	
	public static class ChannelInformation {
		public final int tvSystemType;
		public final int sourceType;
		public final int channelNumber;
		public final int channelIndex;
		public String channelName;
		public final boolean skipped;
		public final boolean locked;
		public final int frequency;
//		public int signalStrength;
		public final boolean isFavorite;
		public final ChannelInfo channelInfo;
		
		// Tags
		public boolean isNew = false;
		
		ChannelInformation(ChannelInfo info) {
			tvSystemType = info.m_SystemType;
			sourceType = info.m_ServiceType;//?
			channelName = info.m_ChName;
			channelNumber = info.m_1PartChNum;
			channelIndex = info.m_Channelndex;
			channelInfo = info;
			skipped = info.m_Skipped;
			locked = info.m_Locked;
			frequency = info.m_Frequency;
			isNew = info.m_IsNewChannel;
			isFavorite=info.m_Favorite;
			
//			if (TextUtils.isEmpty(channelName)) {
//				channelName = String.format("Channel %d", channelNumber);
//			}
		}
		
		private ChannelInformation() {
			tvSystemType = ChannelInfo.RT_TV_SYSTEM_UNKNOWN;
			sourceType = 0;
			channelName = "";
			channelNumber = 0;
			channelIndex = 0;
			channelInfo = null;
			skipped = false;
			locked = false;
			frequency = 0;
			isFavorite=false;
		}

		@Override
		public String toString() {
			return String.format(Locale.US, "%03d %s", channelNumber, channelName);
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof ChannelInformation) {
				ChannelInformation i = (ChannelInformation) o;
				return i.sourceType == sourceType && i.frequency == frequency
						&& i.channelIndex == channelIndex;
			}
			return false;
		}
		
		
	}
	
	public static class ChannelSchedule extends ChannelInformation {

		private final TvProgram[] mPrograms;
		private Date mBaseDate;
		
		ChannelSchedule(ChannelInfo info, EpgData[] epgs) {
			super(info);
			if (epgs != null && epgs.length > 0){
				int length = epgs.length;
				mPrograms = new TvProgram[length];
				for (int i = 0; i < length; i++) {
					mPrograms[i] = new TvProgram(epgs[i]);
				}
			} else {
				mPrograms = new TvProgram[0];
			}
		}
		
		private ChannelSchedule() {
			super();
			mPrograms = new TvProgram[0];
		}

		public List<TvProgram> getProgramList() {
			return Arrays.asList(mPrograms);
		}
		
		public TvProgram getCurrentProgram(long now) {
			for (int i = 0; i < mPrograms.length; i++) {
				TvProgram p = mPrograms[i];
				if (p.isPlaying(now)) {
					return p;
				}
			}
			return null;
		}
		
		/**
		 * Get the date of this schedule.
		 * The time will be set to the begin of that day.
		 * @return The date of this schedule or {@code null} if no data available.
		 */
		public Date getBaseTime() {
			if (mBaseDate == null && mPrograms != null && mPrograms.length > 0) {
				TvProgram p = mPrograms[0];
				Calendar c = Calendar.getInstance();
				if (p.isCrossDay()) {
					c.setTime(p.endTime);
				} else {
					c.setTime(p.startTime);
				}
				c.set(Calendar.HOUR_OF_DAY, 0);
				c.set(Calendar.MINUTE, 0);
				c.set(Calendar.SECOND, 0);
				c.set(Calendar.MILLISECOND, 0);
				mBaseDate = c.getTime();
			}
			return mBaseDate;
		}
		
		public TvProgram findLatestProgram(long now) {
			return optProgramAt(findLatestProgramPosition(now));
		}

		public int findLatestProgramPosition(long now) {
			if (DEBUG) {
				now %= 86400000L;// Only compare hour/minute fields for now
			}
			for (int i = 0; i < mPrograms.length; i++) {
				TvProgram p = mPrograms[i];
				long start = p.startTime.getTime();
				long end = p.endTime.getTime();
				if (DEBUG) {
					start %= 86400000L;
					end %= 86400000L;
				}
				if (i == 0 && now < end) {
					return 0;
				}
				
				if (start <= now && end >= now) {
					return i;
				}
			}
			
			return mPrograms.length - 1;
		}

		public TvProgram optProgramAt(int position) {
			if (position >= 0 && position < mPrograms.length) {
				return mPrograms[position];
			}
			return null;
		}

		public int getProgramCount() {
			return mPrograms.length;
		}
	}
	
	public static class TvProgram {
		
		public Date startTime;
		public Date endTime;
		public String title;
		public String summary;
		public String description;
		
		public boolean hasTeletext;
		public boolean hasSubtitle;
		public int audioCount;
		public int parentRating = PARENT_RATING_UNKNOWN;
		public int genre = GNERE_UNKNOWN;
		
		public final EpgData epg;
		public boolean encrypted;
		
		TvProgram(EpgData epg) {
			if (epg != null) {
				startTime = translateTvTime(epg.m_startTime);
				endTime = translateTvTime(epg.m_endTime);
				title = epg.m_eventTitle;
				summary = epg.m_eventShortDesc;
				description = epg.m_eventExtendedDesc;
				
				hasTeletext = epg.m_hasTeletext;
				hasSubtitle = epg.m_hasSubtitle;
				audioCount = epg.m_audioCount;
				parentRating = epg.m_rating;
				genre = epg.m_genre;
			}
			this.epg = epg;
			if (VERBOSE) {
				Log.v(TAG, "TvProgram: " + toString());
			}
		}
		
		@Override
		public String toString() {
			return String.format("%s: %s ~ %s", title, startTime.toString(), endTime.toString());
		}
		
		public boolean isCrossDay() {
			long s = startTime.getTime() / 86400000L;
			long e = endTime.getTime() / 86400000L;
			return s != e;
		}

		public boolean hasAudioDescription() {
			return true;
		}
		
		public boolean hasMultiAudioTrack() {
			return audioCount > 0;
		}
		
		public boolean isExpired(long now) {
			long end = endTime.getTime();
			return now > end;
		}
		
		public boolean isPlaying(long now) {
			return now >= startTime.getTime() && now <= endTime.getTime();
		}
	}
	
	public static class AudioInformation{
		public int format;// DD, DD+, HE-ACC
		public int track;// Mono, Dual, Stereo
		public String language;
		public boolean hardOfHearing;
		public boolean ad;// Audio Description?
	}
	
	public static class VideoInformation{
		public int resolution = RESOLUTION_UNKNOWN;
		public int aspectRatio = ASPECT_RATIO_UNKNOWN;
		public String reso;
	}
	
	public static class AtvScanEntry {
		public boolean skipped;
		public int index;
		public String name;
	}
	
	public static Date translateTvTime(long time) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time * 1000);
		return c.getTime();
	}
	
	/////////////////////////////////////
	private static final String KEY_PREF_3DMODE = "3d_mode";
	private static TvManagerHelper sInstance;
	
	public static TvManagerHelper getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new TvManagerHelper(context);
		}
		return sInstance;
	}
	
	public final TvManager mTvManager;
	private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
	private final Handler mMainHandler = new Handler(Looper.getMainLooper());
	private final SharedPreferences mPreference;
	
	private TvManagerHelper(Context context) {
		context = context.getApplicationContext();
		mTvManager = (TvManager) context.getSystemService(Context.TV_SERVICE);
		mPreference = context.getSharedPreferences("tv_manager", Context.MODE_PRIVATE);
	}
	
	// System API
	public long currentTvTimeMillis() {
		return mTvManager.getDtvTimeMillis();
//		return new Date().getTime();
	}
	
	//
	public int getInputSource() {
		return mTvManager.getCurLiveSource();
	}
	
	public int getCurFrequency(){
		return mTvManager.getCurFrequency();
	}
	
	/**
	 * Setup input source.
	 * It will execute asynchronously for the TvManager blocks too long.
	 * @param tm
	 * @param src
	 */
	public void setInputSource(final int src) {
		new Thread() {
			@Override
			public void run() {
				mTvManager.setSource(src);
			}
		}.start();
	}
	
	private SoftReference<List<Integer>> mCacheSourceList;
	public List<Integer> getInputSourceList() {
		List<Integer> list = null;
		if (mCacheSourceList != null) {
			list = mCacheSourceList.get();
			if (list != null) {
				return list;
			}
		}
		
		list = new ArrayList<Integer>();
		// Get source lists
		String s = mTvManager.getSourceList();
		String[] ids = s.split("\n");
		
		// Parse to integer
		for (int i = 0; i < ids.length; i++) {
			try {
				int id = Integer.parseInt(ids[i]);
				// Ignore source 24
				if (id == TvManager.SOURCE_PLAYBACK) {
					continue;
				}
				list.add(id);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	public void switchToNextSoruce() {
		int currentSource = getInputSource();
		List<Integer> list = getInputSourceList();
		if (list.size() <= 0) {
			return;
		}
		
		int idx = list.indexOf(currentSource) + 1;
		if (idx >= list.size()) {
			idx = 0;
		}
		setInputSource(list.get(idx));
	}

	public void switchToPrevSource() {
		int currentSource = getInputSource();
		List<Integer> list = getInputSourceList();
		if (list.size() <= 0) {
			return;
		}
		
		int idx = list.indexOf(currentSource) - 1;
		if (idx < 0) {
			idx = list.size() - 1;
		}
		setInputSource(list.get(idx));
	}
	
	// Channel Control
	private Future<?> mTaskChannel;
	
	public void switchToNextChannel() {
		if (hasChannel(getInputSource())) {
			mTvManager.playNextChannel();
		}
	}

	public void switchToPrevChannel() {
		if (hasChannel(getInputSource())) {
			mTvManager.playPrevChannel();
		}
	}
	
	public void switchToHistoryChannel() {
		if (hasChannel(getInputSource())) {
			mTvManager.playHistoryChannel();
		}
	}
	
	public void setCurrentChannel(final int channelNumber) {
		setCurrentChannel(channelNumber, null, Looper.getMainLooper());
	}
	
	public void setCurrentChannel(final int channelNumber, final Runnable callback) {
		setCurrentChannel(channelNumber, callback, Looper.getMainLooper());
	}
	
	public void setCurrentChannel(final int channelNumber, final Runnable callback, final Looper looper) {
		long time;
		if (BENCHMARK) {
			time = SystemClock.uptimeMillis();
		}
		if (!hasChannel(getInputSource())) {
			return;
		}
		if (mTaskChannel != null) {
			mTaskChannel.cancel(false);
		}
		Runnable r = new Runnable() {
			@Override
			public void run() {
				mTvManager.playChannelByNum(channelNumber);
				if (callback != null) {
					Handler h = new Handler(looper);
					h.post(callback);
				}
			}
		};
		mTaskChannel = mExecutor.submit(r);
		if (BENCHMARK) {
			time = SystemClock.uptimeMillis() - time;
			Log.d(TAG, "setCurrentChannel: " + time);
		}
	}
	
	private Runnable mPostSetCurrentChannel;
	public void postSetCurrentChannel(long delay, final int channelNumber, final Runnable callback, final Looper looper) {
		if (mPostSetCurrentChannel != null) {
			mMainHandler.removeCallbacks(mPostSetCurrentChannel);
		}
		mPostSetCurrentChannel = new Runnable() {
			
			@Override
			public void run() {
				mPostSetCurrentChannel = null;
				setCurrentChannel(channelNumber, callback, looper);
			}
		};
		mMainHandler.postDelayed(mPostSetCurrentChannel, delay);
	}
	
	public void setCurrentChannelByIndex(final int channelIndex, final Runnable callback,
			final Looper looper) {
		if (!hasChannel(getInputSource())) {
			return;
		}
		if (mTaskChannel != null) {
			mTaskChannel.cancel(false);
		}
		Runnable r = new Runnable() {
			@Override
			public void run() {
				if (VERBOSE) {
					Log.v(TAG, String.format("setCurrentChannelByIndex: channel=%d", channelIndex));
				}
//				if (isAtvTableScanEnabled()) {
//					int freq = getAtvFrequencyAt(channelIndex);
//					mTvManager.playChannelByChnumFreq(channelIndex, freq, "");
//				} else {
					mTvManager.playChannelByIndex(channelIndex-1);
//					mTvManager.setCurrentChannel(channelNumber, mRunUpdateChannel);
//				}
				if (callback != null) {
					Handler h = new Handler(looper);
					h.post(callback);
				}
			}
		};
		mTaskChannel = mExecutor.submit(r);
	}
	
	@Deprecated
	public static ChannelInfo[] _getCurrentChannelInformations(TvManager tm) {
		final int channelCount = tm.getChannelCount();
		if (channelCount <= 0) {
			return new ChannelInfo[0];
		}
		
		ChannelInfo[] infos = new ChannelInfo[channelCount];
		for (int i = 0; i < channelCount; i++) {
			infos[i] = tm.getChannelInfoByIndex(i);
		}
		return infos;
	}
	
	// Channel Informations & EPG
	/**
	 * Get the channel schedules of current input source.
	 * @param tm
	 * @param dayOffset
	 * @param dayCount
	 * @return
	 */
	public List<ChannelSchedule> getChannelSchedules(int dayOffset) {
		List<ChannelSchedule> list = new ArrayList<ChannelSchedule>();
		int source = getInputSource();
		if (!hasChannel(source)) {
			return list;
		}
		
		final int channelCount = mTvManager.getChannelCount();
		// Get Channel informations
		if (channelCount > 0) {
			// Get/Setup filter
			ChannelFilter filter = mTvManager.getDefaultFilter();
			filter.atv = true;
			
			int start = 0;
			while(start < channelCount) {
				// This returns an array with maximum length 25.
				ChannelInfo[] ci = mTvManager.getChInfoArray(filter, start, channelCount - start);
				if (ci != null) {
					if (ci.length == 0) {
						break;
					}
					for (int i = 0; i < ci.length; i++) {
						list.add(getChannelSchduleByChannelInfo(ci[i], dayOffset));
					}
					start += ci.length;
				} else {
					Log.w(TAG, "Null response from getChInfoArray");
					break;
				}
			}
		}
		return list;
	}
	
	public List<ChannelInformation> getChannelList() {
		List<ChannelInformation> list = new ArrayList<ChannelInformation>();
		if (!hasChannel(getInputSource())) {
			return list;
		}
		
		final int channelCount = mTvManager.getChannelCount();
		if (channelCount > 0) {
			ChannelFilter filter = mTvManager.getDefaultFilter();
			getChannelInfoArray(list, channelCount, filter);
		}
		return list;
	}
	
	public List<ChannelInformation> getDtvChannelList() {
		List<ChannelInformation> list = new ArrayList<ChannelInformation>();
		if (!hasChannel(getInputSource())) {
			return list;
		}
		
		final int channelCount = mTvManager.getChannelCount();
		if (channelCount > 0) {
			ChannelFilter filter = mTvManager.getDefaultFilter();
			filter.atv = true;// Remove atv
			getChannelInfoArray(list, channelCount, filter);
		}
		return list;
	}
	
	public List<ChannelInformation> getAtvChannelList() {
		List<ChannelInformation> list = new ArrayList<ChannelInformation>();
		if (!hasChannel(getInputSource())) {
			return list;
		}
		
		final int channelCount = mTvManager.getChannelCount();
		if (channelCount > 0) {
			ChannelFilter filter = mTvManager.getDefaultFilter();
			filter.dtv = true;// Remove dtv
			filter.audioDtv = true;
			filter.avDtv = true;
			getChannelInfoArray(list, channelCount, filter);
		}
		return list;
	}
    
	public List<ChannelInformation> getAtvFavChannelList() {
		List<ChannelInformation> list = new ArrayList<ChannelInformation>();
		if (!hasChannel(getInputSource())) {
			return list;
		}
		
		final int channelCount = mTvManager.getChannelCount();
		if (channelCount > 0) {
			ChannelFilter filter = mTvManager.getDefaultFilter();
			filter.dtv = true;// Remove dtv
			filter.audioDtv = true;
			filter.avDtv = true;			
			filter.userFavorite = false;
			getChannelInfoArray(list, channelCount, filter);
		}
		return list;
	}

	

		public List<ChannelInformation> getDtvFavChannelList() {
			List<ChannelInformation> list = new ArrayList<ChannelInformation>();
			if (!hasChannel(getInputSource())) {
				return list;
			}
			
			final int channelCount = mTvManager.getChannelCount();
			if (channelCount > 0) {
				ChannelFilter filter = mTvManager.getDefaultFilter();
				filter.atv = true;// Remove atv
				filter.userFavorite = false;
				getChannelInfoArray(list, channelCount, filter);
			}
			return list;
		}        
		public void getEpgData(int iDayOffset, int iDayCount)
		{
					
		    mTvManager.getEpgData(iDayOffset,iDayCount);
		}

		public int getEpgListEpgCount()
		{					
			return	mTvManager.getEpgListEpgCount();
		}
     
		public EpgData[] getEpgDataList(int iStartIdx, int iContentLen)
	 	{
	 			 
		  return mTvManager.getEpgDataList(iStartIdx, iContentLen);
	 	}

	 	public EpgData[] getEpgDailyListByChIdx(int iChIdx, int iDayOffset)
	 	{
	 			 
		  return mTvManager.getEpgDailyListByChIdx(iChIdx, iDayOffset);
	 	}

	 	public int getEpgDailyListCountByChIdx(int iChIdx, int iDayOffset)
	 	{
	 			 
		  return mTvManager.getEpgDailyListCountByChIdx(iChIdx, iDayOffset);

	 	}

	    public void getEpgDataByChIdx(int u16ChIdx, int iDayOffset, int iDayCount)
	 	{
	 			 
		      mTvManager.getEpgDataByChIdx(u16ChIdx, iDayOffset, iDayCount);

	 	}

	    public  String  getEpgDataList_old(int iStartIdx, int iContentLen)
	 	{
		     return mTvManager.getEpgDataList_old(iStartIdx, iContentLen);

	 	}
 		public List<ChannelInformation> getFavChannelList() {
		List<ChannelInformation> list = new ArrayList<ChannelInformation>();
		if (!hasChannel(getInputSource())) {
			return list;
		}
		
		final int channelCount = mTvManager.getChannelCount();
		if (channelCount > 0) {
			ChannelFilter filter = mTvManager.getDefaultFilter();
		    filter.userFavorite = false;
			getChannelInfoArray(list, channelCount, filter);
		}
		return list;
	}
	private void getChannelInfoArray(List<ChannelInformation> list, int channelCount, ChannelFilter filter) {
		int start = 0;
		while(start < channelCount) {
			// This returns an array with maximum length 25.
			ChannelInfo[] ci = mTvManager.getChInfoArray(filter, start, channelCount - start);
			if (ci != null) {
				if (ci.length == 0) {
					break;
				}
				for (int i = 0; i < ci.length; i++) {
					list.add(new ChannelInformation(ci[i]));
				}
				start += ci.length;
			} else {
				Log.w(TAG, "Null response from getChInfoArray");
				break;
			}
		}
	}
	
	public List<ChannelInfo> _getChannelList() {
		List<ChannelInfo> list = new ArrayList<ChannelInfo>();
		if (!hasChannel(getInputSource())) {
			return list;
		}
		
		final int channelCount = mTvManager.getChannelCount();
		// Get Channel infos
		if (channelCount > 0) {
			ChannelFilter filter = mTvManager.getDefaultFilter();
			ChannelInfo[] ci = mTvManager.getChInfoArray(filter, 0, channelCount);
			if (ci != null) {
				for (int i = 0; i < ci.length; i++) {
					list.add(ci[0]);
				}
			}
		}
		return list;
	}
	
	public int getCurrentChannelIndex() {
		if (hasChannel(getInputSource())) {
			return mTvManager.getCurChannelIndex();
		}
		return -1;
	}

   public ChannelInfo getChanneIInfoByIndex(int index) {
		
		return mTvManager.getChannelInfoByIndex(index);
	}
	
	public ChannelInformation getCurrentChannelInformation() {
		long time;
		if (BENCHMARK) {
			time = SystemClock.uptimeMillis();
		}
		if (hasChannel(getInputSource())) {
			ChannelInfo ci = null;
			ChannelFilter filter = mTvManager.getDefaultFilter();
			ChannelInfo[] array = mTvManager.getChInfoArray(filter, getCurrentChannelIndex(), 1);
			if (array != null && array.length > 0) {
				ci = array[0];
				if (BENCHMARK) {
					time = SystemClock.uptimeMillis() - time;
					Log.d(TAG, "getCurrentChannelInformation,time= " + time);
				}
				ChannelInformation info = new ChannelInformation(ci);
				if (VERBOSE) {
					Log.d(TAG, "getCurrentChannelInformation,channel information: " + info.toString());
				}
				return info;
			}
		}
		return CHANNEL_INFO_EMPTY;
	}
	

	public ChannelInformation getChannelAt(int channelIdx) {
		if (hasChannel(getInputSource())) {
			ChannelInfo ci = null;
			ChannelFilter filter = mTvManager.getDefaultFilter();
			ChannelInfo[] array = mTvManager.getChInfoArray(filter, channelIdx, 1);
			if (array != null && array.length > 0) {
				ci = array[0];
				return new ChannelInformation(ci);
			}
		}
		return CHANNEL_INFO_EMPTY;
	}
	
	public int getChannelCount() {
		if (hasChannel(getInputSource())) {
			return mTvManager.getChannelCount();
		}
		return 0;
	}
	
	public ChannelSchedule getCurrentChannelSchedule() {
		if (hasProgramInformation()) {
			ChannelInfo ci = null;
			ChannelFilter filter = mTvManager.getDefaultFilter();
			ChannelInfo[] array = mTvManager.getChInfoArray(filter, mTvManager.getCurChannelIndex(), 1);
			if (array != null && array.length > 0) {
				ci = array[0];
				return getChannelSchduleByChannelInfo(ci, 0);
			}
		}
		return CHANNEL_SCHEDULE_EMPTY;
	//	return null;
	}
	
	public ChannelSchedule getChannelSchduleByChannelInfo(ChannelInfo ci, int dayOffset) {
		EpgData[] epgs = mTvManager.getEpgDailyListByChIdx(ci.m_Channelndex, dayOffset);
		if (VERBOSE) {
			Log.v(TAG,
				String.format("API: getEpgDailyListByChIdx(%d,%d): length = %d", 
					ci.m_Channelndex, dayOffset, epgs.length));
		}
		return new ChannelSchedule(ci, epgs);
	}

    public char getSignalStrength()
    {
       return mTvManager.getSignalStrength();
    }
	public AudioInformation getCurrentAudioInformations() {
		AudioInformation info = new AudioInformation();
		info.format = 0;//TODO: ??
		info.track = mTvManager.getAudioMode();
		info.language = mTvManager.getCurrentAudioLang();
		info.hardOfHearing = true;//??
		info.ad = true;//
		return info;
	}
	
	public VideoInformation getCurrentVideoInformations() {
		VideoInformation info = new VideoInformation();
		//info.reso = mTvManager.getResolutionInfo();
		//info.aspectRatio = mTvManager.getAspectRatio(getInputSource());
		return info;
	}

	public static boolean hasAudioInformation(int source) {
		switch(source) {
		case TvManager.SOURCE_DTV1:
		case TvManager.SOURCE_DTV2:
		case TvManager.SOURCE_IDTV1:
			return true;
		default:
			return false;
		}
	}
	
	/**
	 * Check whether current input source has EPG data or not.
	 * @return has EPG data or not
	 */
	public boolean hasProgramInformation() {
		int source = getInputSource();
		switch(source) {
		case TvManager.SOURCE_DTV1:
		case TvManager.SOURCE_DTV2:
			return true;
		case TvManager.SOURCE_IDTV1:
			int sub = mTvManager.getIDTVSubSource();
			if (sub == TvManager.TV_SUB_SERVICE_DTV) {
				return true;
			}
			return false;
		default:
			return false;
		}
	}

	public static boolean hasChannel(int source) {
		switch(source) {
		case TvManager.SOURCE_ATV1:
		case TvManager.SOURCE_ATV2:
		case TvManager.SOURCE_DTV1:
		case TvManager.SOURCE_DTV2:
		case TvManager.SOURCE_IDTV1:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isNotTVInputsource(int source) {
		switch(source) {
		case TvManager.SOURCE_ATV1:
		case TvManager.SOURCE_ATV2:
		case TvManager.SOURCE_DTV1:
		case TvManager.SOURCE_DTV2:
		case TvManager.SOURCE_IDTV1:
			return false;
		default:
			return true;
		}
	}
	
	public static boolean isAtvSource(int source) {
		return source == TvManager.SOURCE_ATV1 || source == TvManager.SOURCE_ATV2;
	}
	
	public static boolean isAV1Source(int source) {
		return source == TvManager.SOURCE_AV1;
	}
	
	public static boolean isAV2Source(int source) {
		return source == TvManager.SOURCE_AV2;
	}
	
	public static boolean isAV3Source(int source) {
		return source == TvManager.SOURCE_AV3;
	}
	
	public static boolean isYpbprSource(int source) {
		return source == TvManager.SOURCE_YPP1 || source == TvManager.SOURCE_YPP2;
	}
	public static boolean isDtvSource(int source) {
		return source == TvManager.SOURCE_DTV1 || source == TvManager.SOURCE_DTV2;
	}
	
	public static boolean isIdtvSource(int source) {
		return source == TvManager.SOURCE_IDTV1;
	}
	
	public static boolean isTvSource(int source) {
		return isAtvSource(source) || isDtvSource(source) || isIdtvSource(source);
	}
	
	public static boolean isPCSource(int source) {
		return source == TvManager.SOURCE_VGA1 || source == TvManager.SOURCE_VGA2;
	}

	// ===================== Picture Settings =============================
	public void set3dColorManagement(boolean enable) {
		mTvManager.set3dColourManagement(enable);
	}
	
	public boolean is3dColorManagementEnabled() {
		return mTvManager.get3dColourManagement();
	}

	public void setBacklightControl(boolean active) {
		mTvManager.setBacklight(active ? 1 : 0);
	}
	
	public boolean isBacklightActive() {
		return mTvManager.getBacklight() == 1;
	}

	public void SetPictureMode(int value_mode) {  // 图效 //标准/明亮/柔和/自定义
		mTvManager.setPictureMode(value_mode);
	}

	public int GetPictureMode() {// 图效 //标准/明亮/柔和/自定义
		return mTvManager.getPictureMode();
	}
	public void SetBrightness(int value_bright) { // 亮度
		mTvManager.setBrightness(value_bright);
	}

	public int GetBrightness() {// 亮度
		return mTvManager.getBrightness();
	}
	public void SetContrast(int iValue_contrast) { // 对比度
		mTvManager.setContrast(iValue_contrast);
	}

	public int GetContrast() {// 对比度
		return mTvManager.getContrast();
	}
	
	
	public void SetSaturation(int iValue_r) { // 彩色
		mTvManager.setSaturation(iValue_r);
	}

	public int GetSaturation() {// 彩色
		return mTvManager.getSaturation();
	}
	

	public void SetSharpness(boolean iApply, int iValue) { // 锐度
		mTvManager.setSharpness(iApply,iValue);
	}

	public int GetSharpness() {// 锐度
		return mTvManager.getSharpness();
	}

	public void SetHue(int iValue_hue) { // 色调
		mTvManager.setHue(iValue_hue);
	}

	public int GetHue() {// 色调
		return mTvManager.getHue();
	}
	public void setColorTempMode(int level_color) { // 色温
		mTvManager.setColorTempMode(level_color);
	}

	public int GetColorTempMode() {// 色温
		return mTvManager.getColorTempMode();
	}
	
	public void SetDCR(int iDCR) { // 自然光
		mTvManager.setDCR(iDCR);
	}

	public int GetDCR() {// 自然光
		return mTvManager.getDCR();
	}
	public void setDCC(boolean iDccOn, boolean iIsApply){ // 动态对比度
		mTvManager.setDCC(iDccOn,iIsApply);
	}

	public boolean GetDCC() {// 动态对比度
		return mTvManager.getDCC();
	}
	
	public void SetBacklight(int iValue_back) { // 背光
		mTvManager.setBacklight(iValue_back);
	}

	public int GetBacklight() {// 背光
		return mTvManager.getBacklight();
		
	}
	
	// ===================== Sound Settings ====================================
	public void setAudioBalancing(int value) {  // 平衡
		mTvManager.setBalance(value);
	}

	public int getAudioBalancing() {// 平衡
		return mTvManager.getBalance();
	}
	
	public void setAudioDistortionControl(boolean b) {
		mTvManager.setAudioDistortionControl(b);
	}

	public boolean isAudioDistortionControlEnabled() {
		return mTvManager.getAudioDistortionControl();
	}
	
	public void setTvMounting(int value) {  //声音场景
		mTvManager.SetTvMountSetting(value);
	}
	
	public int getTvMounting() { //声音场景
		return mTvManager.GetTvMountSetting();
	}
	
	public void SetAudioSpdifOutput(int value) { //数字音频输出
		mTvManager.setAudioSpdifOutput(value);
	}

	public int GetAudioSpdifOutput() {//数字音频输出
		return mTvManager.getAudioSpdifOutput();
	}
	
	public void SetAudioMode(int audio_mode) { //声音模式
		mTvManager.setAudioMode(audio_mode);
	}
	
	public int GetAudioMode() { //声音模式
		return mTvManager.getAudioMode();
	}
	
	public void Setbass(int value_bass) { //低音
		mTvManager.setBass(value_bass);
	}

	public int Getbass() { //低音
		return mTvManager.getBass();
	}
	public boolean isDynamicRangeControlEnabled() {
		return mTvManager.getDynamicRangeControl();
	}

	
	public int GetAudioChannelSwap() { //声道选择
		return mTvManager.getAudioChannelSwap();
	}
	public void SetAudioChannelSwap(int value_chanel) { //声道选择
		mTvManager.setAudioChannelSwap(value_chanel);
	}
	
	public void SetTreble(int value_treble) { //高音
		mTvManager.setTreble(value_treble);
	}
	public int GetTreble() { //高音
		return mTvManager.getTreble();
	}
	public String  getResolutionInfo()
	  {
   
        return mTvManager.getResolutionInfo();

	  }
	  public int  getAvColorStd()
	  {
   
        return mTvManager.getAvColorStd();

	  }


	public boolean SetAutoVolume(boolean bEnable) { //自动音量
		return mTvManager.setAutoVolume(bEnable);
	}
	public boolean GetAutoVolume() { //自动音量
		return mTvManager.getAutoVolume();
	}
	
	public void SetAspectRatio(int value_ratio) { //比例
		mTvManager.setAspectRatio(value_ratio);
	}
	public int GetAspectRatio() { //比例
		return mTvManager.getAspectRatio(/*getInputSource()*/);
	}
	
	public void setDynamicRangeControlEnabled(boolean enable) {
		mTvManager.setDynamicRangeControl(enable);
	}

	public int getAudioLevelOffset() {
		return mTvManager.getAudioVolumeOffset();
	}
	
	public void setAudioLevelOffset(int offset) {
		mTvManager.setAudioVolumeOffset(offset);  
	}
	// Preferences
	public int getTeletextMode() {
		return mTvManager.getTeleTextMode();
	}

	public void setTeletextMode(int value) {
		mTvManager.setTeleTextMode(value);
	}

	public void setTeletextLanguage(int lang) {
		mTvManager.setTeleTextLanguage(lang);
	}

	public int getTeletextLanguage() {
		return mTvManager.getTeleTextLanguage();
	}

	public boolean isBlueScreenEnabled() {
		return false;
	}

	public void setBlueScreenEnable(boolean b) {
		
	}

	//PC setting
	// 自动
	public boolean setVgaAutoAdjust(){
		return mTvManager.setVgaAutoAdjust();
	}
	//水平位置
	public boolean setVgaHPosition(char ucPosition){
		return mTvManager.setVgaHPosition(ucPosition);
	}
	
	public char getVgaHPosition(){
		return mTvManager.getVgaHPosition();
	}
	//竖直位置
	public boolean setVgaVPosition(char ucPosition){
		return mTvManager.setVgaVPosition(ucPosition);
	}
	
	public char getVgaVPosition(){
		return mTvManager.getVgaVPosition();
	}
	//相位调整
	public boolean setVgaPhase(char ucPosition){
		return mTvManager.setVgaPhase(ucPosition);
	}
	
	public char getVgaPhase(){
		return mTvManager.getVgaPhase();
	}
	//采样时钟
	public boolean setVgaClock(char value){
		return mTvManager.setVgaClock(value);
	}
	
	public char getVgaClock(){
		return mTvManager.getVgaClock();
	}
	//end PC setting
	// Setup
	public void setCountry(int value) {
		mTvManager.setCountryCode(value);
	}

	public int getTvLocation() {
		return mTvManager.getTVLocation();
	}

	public void setTvLocation(int location) {
		mTvManager.setTVLocation(location);
	}

	public void setPvrTimeShiftSize(int value) {
		
	}
	
	public int getPvrTimeShiftSize() {
		return PVR_TIME_SHIFT_SIZE_512M;
	}
	
	public void setPvrTimeShiftEnable(boolean enable) {
		
	}
	
	public boolean isPvrTimeShiftEnabled() {
		return false;
	}

	// Subtitle
	public List<String> getCurrentSubtitles() {
		List<String> list = new ArrayList<String>();
		int count = mTvManager.getDtvSubtitleIndexListCount();
		if (count > 0) {
			String str = mTvManager.getDtvSubtitleIndexList();
			String[] subs = str.split("\n");
			for (int i = 0; i < subs.length; i++) {
				list.add(subs[i]);
			}
		}
		return list;
	}
	
	public boolean isSubtitleEnabled() {
		return mTvManager.getSubtitleEnable();
	}
	
	public void setSubtitleEnable(boolean enable) {
		mTvManager.setSubtitleEnable(enable);
	}
	
	public int getCurrentSubtitleIndex() {
		return mTvManager.getCurDtvSubtitleIndex();
	}
	
	public int setCurrentSubtitle(int index) {
		if (mTvManager.setDtvSubtitleByIndex(index)) {
			return index;
		}
		return getCurrentSubtitleIndex();
	}

	// -------- ATV Manual Tuning --------
	
	// Atv Sound Std
	public int getCurrentAtvSoundStd() {
		return mTvManager.getCurAtvSoundStd();
	}
	
	/**
	 * 
	 * @param atvSoundStd See {@link TvManager#RT_ATV_SOUND_SYSTEM_M}
	 */
	public void setCurrentAtvSoundStd(int atvSoundStd) {
		mTvManager.setCurAtvSoundStd(atvSoundStd);
	}
	
	// Atv Color Std
	Future<?> mSetAtvColorTask;
	public void setCurrentAtvColorStd(final int colorStd) {
		// Do it asynchronously.
		if (mSetAtvColorTask != null) {
			mSetAtvColorTask.cancel(false);
		}
		Runnable r = new Runnable() {

			@Override
			public void run() {
				// This takes about 1.5 seconds.
				mTvManager.setCurAtvColorStd(colorStd);
			}

		};
		mSetAtvColorTask = mExecutor.submit(r);
	}
	
	public int getCurrentAtvColorStd() {
		return mTvManager.getCurAtvColorStd();
	}
	
	// Fine Tuning
	Future<?> mSetCurrentFrequencyOffset;
	public void setCurrentAtvFrequencyOffset(final int offset, final boolean perminant) {
		if (mSetCurrentFrequencyOffset != null) {
			mSetCurrentFrequencyOffset.cancel(false);
		}
		Runnable r = new Runnable() {

			@Override
			public void run() {
				mTvManager.fineTuneCurFrequency(offset, perminant);
			}

		};
		mSetCurrentFrequencyOffset = mExecutor.submit(r);
	}

	public int getCurrentAtvFrequencyOffset() {
		/*
		 * TODO: Doesn't work. Always return zero.
		 * RpcExecutorManager::Execute(): ??????????? ERROR: Cannot find function(getTvCurChFreqOffset)
		 * RpcClientThread::ThreadProc(): ????? Invalid result(-1)
		 */
		return mTvManager.getTvCurChFreqOffset();
	}
	
	// Skipped
	
	public void setCurrentAtvChannelSkipped(boolean skipped) {
		mTvManager.setCurChannelSkipped(skipped);
	}


    public void setChannelFav(int number,boolean Favorite) {
		mTvManager.setChannelFav(number,Favorite);
	}

	public boolean getChannelFav(int number) {
		return mTvManager.getChannelFav(number);
	}

   
   public boolean setChannelName(int number,String name) {
		   return mTvManager.setChannelName(number,name);
	   }
	
	/**
	 * Use {@link #getCurrentChannelInformation()} instead.
	 * @return
	 */
	@Deprecated
	public boolean isCurrentAtvChannelSkipped() {
		return mTvManager.getCurChannelSkipped();
	}

	// Channel Booster
	public void setAtvChannelBooster(int channelIndex, boolean enabled) {
		/* 
		 * TODO: Doesn't work.
		 * RpcExecutorManager::Execute(): ??????????? ERROR: Cannot find function(setChannelbooster)
		 * RpcClientThread::ThreadProc(): ????? Invalid result(-1)
		 * RpcExecutorManager::Execute(): ??????????? ERROR: Cannot find function(setNoiseReduction_impulse)
		 * RpcClientThread::ThreadProc(): ????? Invalid result(-1)
		 */
		mTvManager.setChannelbooster(channelIndex, enabled);
	}

	public boolean isAtvChannelBoosterEnabled(int channelIndex) {
		return mTvManager.getChannelbooster(channelIndex) == 1;
	}

	// ATV Frequency Table
	public static final int ATV_TYPE_CABLE = 0;
	public static final int ATV_TYPE_SATELLITE = 1;
	
	/**
	 * Get the size of frequency table.
	 * Note that this is only available when {@link #isAtvChannelBoosterEnabled(int)} is true.
	 * @return
	 */
	public int getAtvFrequencyTableSize() {
		return mTvManager.getChannelFreqCount();
	}
	
	/**
	 * Lookup the ATV frequency in the table.
	 * Note that this is only available when {@link #isAtvChannelBoosterEnabled(int)} is true.
	 * @param tableIndex zero-based index according to the table size returned by {@link #getAtvFrequencyTableSize()}
	 * @return
	 */
	public int getAtvFrequencyAt(int tableIndex) {
		return mTvManager.getChannelFreqByTableIndex(tableIndex);
	}
	
	/**
	 * Lookup the ATV frequency in the table.
	 * Note that this is only available when {@link #isAtvChannelBoosterEnabled(int)} is true.
	 * @param tableIndex zero-based index according to the table size returned by {@link #getAtvFrequencyTableSize()}
	 * @return
	 */
	private int getAtvBandwidthAt(int tableIndex) {
		return mTvManager.getChannelBandwidth(tableIndex);
	}
	
	public List<AtvScanEntry> getAtvScanEntries() {
		List<AtvScanEntry> list = new ArrayList<TvManagerHelper.AtvScanEntry>();
//		if (isAtvTableScanEnabled()) {
//			int count = getAtvFrequencyTableSize();
//			for (int i = 0; i < count; i++) {
//				AtvScanEntry f = new AtvScanEntry();
//				f.index = i;
//				f.skipped = true;
//				list.add(f);
//			}
//			List<ChannelInformation> info = getAtvChannelList();
//			for (ChannelInformation i : info) {
//				if (i.channelIndex < count) {
//					AtvScanEntry f = list.get(i.channelIndex);
//					f.skipped = i.skipped;
//					f.name = i.channelName;
//				}
//			}
//		} else {
			List<ChannelInformation> info = getAtvChannelList();
			int count = info.size();
			for (int i = 0; i < count; i++) {
				AtvScanEntry f = new AtvScanEntry();
				ChannelInformation c = info.get(i);
				f.index = c.channelIndex;
				f.skipped = c.skipped;
				f.name = c.channelName;
				list.add(f);
			}
//		}
		return list;
	}

	public int getAtvScanEntryCount() {
		if (isAtvTableScanEnabled()) {
			return getAtvFrequencyTableSize();
		}
		return getChannelCount();
	}
	
	public boolean isAtvTableScanEnabled() {
		return mTvManager.isAtvTableScan();
	}
	
	public void setAtvTableScanEnabled(boolean b) {
		mTvManager.setAtvTableScan(b);
	}
	
	// ATV Scanning
	/**
	 * Start ATV scanning and replace the current channel on finish!?
	 * 
	 * @param tableIdx
	 */
	public void startAtvTableScanning(int tableIdx) {
		int frequenct = getAtvFrequencyAt(tableIdx);
		int bandwidth = getAtvBandwidthAt(tableIdx);
		mTvManager.tvSeekScanStop();
		mTvManager.tvScanManualStart(frequenct, bandwidth, 0);
	}
	
	/**
	 * Start ATV scanning and replace the current channel on finish!?
	 * @param seekForward
	 */
	public void startAtvSeekScanning(boolean seekForward) {
		mTvManager.tvSeekScanStop();
		mTvManager.tvSeekScanStart(seekForward);
	}
	
	public void stopAtvScanning() {
		mTvManager.tvSeekScanStop();
	}

	// DTV Frequency table
	public int getDtvFrequencyTableSize() {
		return mTvManager.getChannelFreqCount();
	}

	public String getDtvFrequencyName(int index) {
		return mTvManager.getChannelNameByTableIndex(index);
	}

	public int getDtvFrequency(int index) {
		return mTvManager.getChannelFreqByTableIndex(index);
	}
	
	/**
	 * 
	 * @param index
	 * @return See {@link TvManager#TUNING_BANDWIDTH_8MHZ}
	 */
	public int getDtvBandwidth(int index) {
		return mTvManager.getChannelBandwidth(index);
	}
	
	public void stopDtvScanning() {
		mTvManager.tvScanManualStop();
	}

	/**
	 * 
	 * @param frequency Hz
	 * @param bandwidth MHz
	 * @param index
	 * @param source Useless
	 */
	public void startDtvScanning(int frequency, int bandwidth, int index, int source) {
		mTvManager.tvScanManualStart(frequency, bandwidth, index);
	}

	
	// Picture Setting
	public void setPictureMode(int value) {
		mTvManager.setPictureMode(value);
	}

	public void refresh3dModeAspectRatio() {
		mExecutor.execute(mRefresh3dAspectRatio);
	}
	
	private final Runnable mRefresh3dAspectRatio = new Runnable() {
		
		@Override
		public void run() {
			int aspect = 0;// mTvManager.getAspectRatio(getInputSource());
			int mode = mTvManager.get3dMode();
			int lastMode = mPreference.getInt(KEY_PREF_3DMODE, mode);
			
			// Restore 3D mode if it's been changed by others.
			if (lastMode != mode) {
				mTvManager.set3dModeAndChangeRatio(lastMode, false, aspect);
			} else {
				mTvManager.setAspectRatio(aspect);
			}
		}
	};
     public boolean queryTvStatus(int type)
      {      
		return mTvManager.queryTvStatus(type);

      }
	public boolean isSignalDisplayReady() {		
		return !mTvManager.getIsNoSignal();
	}
	
	public boolean GetIsNoSupportResolution()
		{	   
		  return mTvManager.GetIsNoSupportResolution();
	
		}

   	public boolean GetIsScrambled()
		{	   
		  return mTvManager.GetIsScrambled();
	
		}

	// Ginga
	public boolean isGingaExisted() {
		if (GINGA) {
			return mTvManager.getIsGingaExist();
		}
		return false;
	}

	public boolean isForwardKeyToGinga() {
		if (GINGA) {
			return mTvManager.getIsForwadKeyToGinga();
		}
		return false;
	}

	public void forwardKeyToGinga(int keyCode) {
		if (GINGA) {
			mTvManager.AndroidDfbKeyPress(keyCode);
		}
	}

	public void closeGinga() {
		if (GINGA) {
			mTvManager.closeGingaApp();
		}
	}

	// 3D Settings
	public static final int MODE_3D_AUTO = -1;
	public static final int MODE_3D_DISABLED = 0;
	public static final int MODE_3D_LEFT_RIGHT = 1;
	public static final int MODE_3D_UP_DOWN = 2;
	public static final int MODE_3D_2D_TO_3D = 3;
	
	public static final int MAX_3D_DEPTH = 100;
	
	public boolean isSupport3D() {
		return mTvManager.isSupport3D();
	}
	
	public void set3DMode(int mode) {
		assert mode != MODE_3D_AUTO;
		switch(mode) {
			case MODE_3D_DISABLED:
				mTvManager.set3dMode(TvManager.SLR_3DMODE_2D);
				break;
			case MODE_3D_LEFT_RIGHT:
				mTvManager.set3dMode(TvManager.SLR_3DMODE_3D_SBS);
				break;
			case MODE_3D_UP_DOWN:
				mTvManager.set3dMode(TvManager.SLR_3DMODE_3D_TB);
				break;
			case MODE_3D_2D_TO_3D:
				mTvManager.set3dMode(TvManager.SLR_3DMODE_2D_CVT_3D);
				break;
			default:
				break;
		}
		
		mPreference.edit().putInt(KEY_PREF_3DMODE, mTvManager.get3dMode()).commit();
	}
	
	public int get3DMode() {
		int mode = mTvManager.get3dMode();
		switch(mode) {
			case TvManager.SLR_3DMODE_2D:
				return MODE_3D_DISABLED;
			case TvManager.SLR_3DMODE_2D_CVT_3D:
				return MODE_3D_2D_TO_3D;
			case TvManager.SLR_3DMODE_3D_SBS:
			case TvManager.SLR_3DMODE_3D_SBS_CVT_2D:
				return MODE_3D_LEFT_RIGHT;
			case TvManager.SLR_3DMODE_3D_TB:
			case TvManager.SLR_3DMODE_3D_TB_CVT_2D:
				return MODE_3D_UP_DOWN;
			default:
			case TvManager.SLR_3DMODE_3D_AUTO:
			case TvManager.SLR_3DMODE_3D_AUTO_CVT_2D:
				return MODE_3D_AUTO;
		}
	}
	
	public static boolean is3DModeOptionEnabled(int mode) {
	    return mode != MODE_3D_AUTO;
    }
	
	public boolean is3Dto2DEnabled() {
	    return mTvManager.get3dCvrt2D();
    }
	
	public void set3Dto2DEnabled(boolean enabled) {
		mTvManager.set3dCvrt2D(enabled, enabled ? 1 : -1);
		
		mPreference.edit().putInt(KEY_PREF_3DMODE, mTvManager.get3dMode()).commit();
	}

	public static boolean is3Dto2DOptionEnabled(int mode) {
		return mode == MODE_3D_AUTO || mode == MODE_3D_LEFT_RIGHT || mode == MODE_3D_UP_DOWN;
    }
	
	public boolean is3DLeftRightSwapped() {
	    return mTvManager.get3dLRSwap();
    }
	
	public void set3DLeftRightSwapped(boolean enabled) {
		mTvManager.set3dLRSwap(enabled);
		
		mPreference.edit().putInt(KEY_PREF_3DMODE, mTvManager.get3dMode()).commit();
	}
	
	public static boolean is3DLeftRightSwappedOptionEnabled(int mode) {
		return mode == MODE_3D_AUTO || mode == MODE_3D_LEFT_RIGHT || mode == MODE_3D_UP_DOWN;
    }
	
	public int get3DDepth() {
		return mTvManager.get3dDeep();
	}
	
	public void set3DDepth(int deep) {
		mTvManager.set3dDeep(deep);
	}
	
	public static boolean is3DDepthOptionEnabled(int mode) {
		return mode == MODE_3D_LEFT_RIGHT || mode == MODE_3D_UP_DOWN;
    }

	public boolean is4K2KMode() {
	    return mTvManager.is4K2KMode();
    }



}
