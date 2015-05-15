package com.tsb.settings.fragment.menu.systemSetting;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.TvManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.talents.providers.TableDataAccessInterface;
import com.tsb.settings.R;
import com.tsb.settings.TvClientTypeList;
import com.tsb.settings.fragment.menu.BaseMenuFragment;
import com.tsb.settings.menu.MenuItem;

public class AboutTvFragment extends BaseMenuFragment{
	private TextView[] txt = new TextView[4]; 
	private static final String FILENAME_PROC_VERSION = "/proc/version";
	private static final String LOG_TAG = "aboutTV_Activity";
	private TvManager mTvManager;
	private String clientType;
	@Override
	protected View onInflateLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		mTvManager = (TvManager) getActivity().getSystemService(Context.TV_SERVICE);
		clientType=mTvManager.getClientType();
		TableDataAccessInterface tdai = new TableDataAccessInterface(getActivity());
		View v ;
		if(clientType.equals(TvClientTypeList.HaierClient)){
			v = inflater.inflate(R.layout.layout_activity_about_tv_haier, container, false);
			txt[0] = (TextView)v.findViewById(R.id.txt_android_version);
//			txt[1] = (TextView)v.findViewById(R.id.txt_compile_version);
			txt[2] = (TextView)v.findViewById(R.id.txt_software_version);
			txt[0].setText(Build.VERSION.RELEASE);
//			txt[1].setText(getFormattedKernelVersion());
			txt[2].setText(mTvManager.getProductVersionID());
		}
		else {
			v = inflater.inflate(R.layout.layout_activity_about_tv, container, false);
			txt[0] = (TextView)v.findViewById(R.id.txt_serial);
			txt[1] = (TextView)v.findViewById(R.id.txt_android_version);
			txt[2] = (TextView)v.findViewById(R.id.txt_software_version);
			txt[3] = (TextView)v.findViewById(R.id.txt_MAC_address);
			txt[0].setText( tdai.getDnum(getActivity().getContentResolver()));
			txt[1].setText(Build.VERSION.RELEASE);
			txt[2].setText(mTvManager.getProductVersionID());
			txt[3].setText(mTvManager.getMacAddress());
		}  
        return v;
	}
	
	@Override
	public void onCreateMenuItems(List<MenuItem> items) {
		// TODO Auto-generated method stub
		
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    }
    
    
    
    /**
     * Reads a line from the specified file.
     * @param filename the file to read from
     * @return the first line, if any.
     * @throws IOException if the file couldn't be read
     */
    private static String readLine(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename), 256);
        try {
            return reader.readLine();
        } finally {
            reader.close();
        }
    }
    
    public static String getFormattedKernelVersion() {
        try {
            return formatKernelVersion(readLine(FILENAME_PROC_VERSION));

        } catch (IOException e) {
            Log.e(LOG_TAG,
                "IO Exception when getting kernel version for Device Info screen",
                e);

            return "Unavailable";
        }
    }

    public static String formatKernelVersion(String rawKernelVersion) {
        // Example (see tests for more):
        // Linux version 3.0.31-g6fb96c9 (android-build@xxx.xxx.xxx.xxx.com) \
        //     (gcc version 4.6.x-xxx 20120106 (prerelease) (GCC) ) #1 SMP PREEMPT \
        //     Thu Jun 28 11:02:39 PDT 2012

        final String PROC_VERSION_REGEX =
            "Linux version (\\S+) " + /* group 1: "3.0.31-g6fb96c9" */
            "\\((\\S+?)\\) " +        /* group 2: "x@y.com" (kernel builder) */
            "(?:\\(gcc.+? \\)) " +    /* ignore: GCC version information */
            "(#\\d+) " +              /* group 3: "#1" */
            "(?:.*?)?" +              /* ignore: optional SMP, PREEMPT, and any CONFIG_FLAGS */
            "((Sun|Mon|Tue|Wed|Thu|Fri|Sat).+)"; /* group 4: "Thu Jun 28 11:02:39 PDT 2012" */

        Matcher m = Pattern.compile(PROC_VERSION_REGEX).matcher(rawKernelVersion);
        if (!m.matches()) {
            Log.e(LOG_TAG, "Regex did not match on /proc/version: " + rawKernelVersion);
            return "Unavailable";
        } else if (m.groupCount() < 4) {
            Log.e(LOG_TAG, "Regex match on /proc/version only returned " + m.groupCount()
                    + " groups");
            return "Unavailable";
        }
      return m.group(1) +                 // 3.0.31-g6fb96c9
      m.group(2) + " " + m.group(3);       // x@y.com #1        
//        return m.group(1) + "\n" +                 // 3.0.31-g6fb96c9
//            m.group(2) + " " + m.group(3) + "\n" + // x@y.com #1
//            m.group(4);                            // Thu Jun 28 11:02:39 PDT 2012
    }

	

	@Override
	public int getTitle() {
		// TODO Auto-generated method stub
		return 0;
	}
    
}
