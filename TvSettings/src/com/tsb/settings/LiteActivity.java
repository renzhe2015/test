package com.tsb.settings;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.tsb.settings.fragment.menu.BaseMenuFragment;
import com.tsb.settings.fragment.menu.PictureMenuFragment;
import com.tsb.settings.fragment.menu.SoundMenuFragment;
import com.tsb.settings.fragment.menu.pic.Tv3DSettingFragment;

/**
 * Activity for other application to launch specific settings.
 * @author Jason Lin
 *
 */
public class LiteActivity extends Activity {

	@Override
    protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    Intent intent = getIntent();
	    
		if (intent == null) {
	    	finish();
	    } else {
	    	handleIntent(intent);
	    }
		
    }
	
	

	@Override
    protected void onNewIntent(Intent intent) {
	    super.onNewIntent(intent);
    }

	private void handleIntent(Intent intent) {
	    Class<? extends Fragment> clazz = null;
	    Bundle arg = new Bundle();
	    
	    String setting = intent.getStringExtra("setting");
	    if ("picture".equals(setting)) {
	    	clazz = PictureMenuFragment.class;
	    } else if ("sound".equals(setting)) {
	    	clazz = SoundMenuFragment.class;
	    } else if ("3d".equals(setting)) {
	    	clazz = Tv3DSettingFragment.class;
	    }
	    
	    // Argument not defined yet.
	    if (clazz == null) {
	    	return;
	    }
	    
    	FragmentManager fm = getFragmentManager();
    	Fragment prev = fm.findFragmentById(android.R.id.content);
    	FragmentTransaction ft = fm.beginTransaction();
    	
    	// Already shown.
    	if (prev != null && prev.getClass().equals(clazz)) {
    		return;
    	}
    	
    	// Remove previous 
    	if (prev != null) {
    		ft.remove(prev);
    	}

    	// Show new fragment
    	arg.putInt(BaseMenuFragment.ARG_LAYOUT_ID, R.layout.layout_fragment_base_menu_dialog);
    	Fragment fragment = Fragment.instantiate(this, clazz.getName(), arg);
		getFragmentManager().beginTransaction().add(android.R.id.content, fragment).commit();
	}


	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
	    super.onPostCreate(savedInstanceState);
//	    View buttonConfirm = findViewById(R.id.button_close);
//		buttonConfirm.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				finish();
//			}
//		});
    }
	
}
