package com.tsb.settings.fragment.menu.net;

import com.tsb.settings.R;
import com.tsb.settings.fragment.MainMenuFragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;

public class BaseNetMenuFragment extends Fragment implements OnKeyListener{
	protected View mView;
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(event.getAction()==KeyEvent.ACTION_DOWN){
			if((keyCode==KeyEvent.KEYCODE_BACK||keyCode==KeyEvent.KEYCODE_DPAD_LEFT))
			{
				MainMenuFragment.fromNetSetting=false;
				FragmentManager fm = MainMenuFragment.fm;
				FragmentTransaction ft = fm.beginTransaction();
				ft.detach(this);
				ft.commit();
				mView.getRootView().findViewById(R.id.layout_third_menu).setVisibility(View.GONE);
				mView.getRootView().findViewById(MainMenuFragment.mLastSelectionId2).requestFocus();
				return true;
			}else if(keyCode==KeyEvent.KEYCODE_DPAD_DOWN&&(v.getId()==R.id.btn_speed_test_start||v.getId()==R.id.btn_save_ip_config)){

				return true;
				
			}else if(keyCode==KeyEvent.KEYCODE_DPAD_UP&&(v.getId()==R.id.eth_manuel_ip||v.getId()==R.id.btn_speed_test_start)){
				return true;
			}
		}
		return false;
	}

}
