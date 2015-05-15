
package com.tsb.settings.fragment.menu.adtv;

import android.app.Fragment;
import android.os.Bundle;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.tsb.settings.R;
import com.tsb.settings.fragment.BaseFragment;
import com.tsb.settings.util.FragmentUtils;

public class ConfirmDialogFragment extends BaseFragment implements OnClickListener {
	private Button btnOk;
	private Button btnCancel;
	public static void show(Fragment parent, Class<? extends Fragment> targetFragment, Bundle targetArgs, int title, int message) {
		Bundle arg = new Bundle();
		arg.putInt("title", title);
		arg.putInt("message", message);
		arg.putString("targetClass", targetFragment.getName());
		arg.putBundle("targetArgs", targetArgs);
		FragmentUtils.showSubFragment(parent, ConfirmDialogFragment.class, arg);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Bundle args = getArguments();
		int title = args.getInt("title");
		int message = args.getInt("message");

		View v = inflater.inflate(R.layout.layout_fragment_base_dialog_confirm, container, false);
		btnOk = (Button)v.findViewById(R.id.button_ok);
		btnCancel = (Button)v.findViewById(R.id.button_cancel);
		btnOk.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		TextView textTitle = (TextView) v.findViewById(R.id.text_title);
		TextView textMessage = (TextView) v.findViewById(R.id.text_message);

		textTitle.setText(title);
		textMessage.setText(message);
		
		return v;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.button_ok:
				Bundle args = getArguments();
				String clazz = args.getString("targetClass");
				Bundle ta = args.getBundle("targetArgs");
				FragmentUtils.showSubFragment(this, clazz, ta, "autosearch");
				break;
			case R.id.button_cancel:
				FragmentUtils.popBackSubFragment(this);
				break;
			default:
				break;
		}
	}
	
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	String deviceName = InputDevice.getDevice(event.getDeviceId()).getName();
        if (("Smart_TV_Keypad".equals(deviceName))
        	&&(keyCode == KeyEvent.KEYCODE_TV_INPUT)) {
        	if(btnOk.isFocused())
        		btnOk.performClick();
        	else 
        		btnCancel.performClick();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }	

}
