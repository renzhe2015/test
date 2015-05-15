package com.tsb.settings.fragment.menu.systemSetting;

import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tsb.settings.R;
import com.tsb.settings.fragment.menu.BaseMenuFragment;
import com.tsb.settings.menu.MenuItem;
import com.tsb.settings.util.FocusAnimator;
import com.tsb.settings.util.FragmentUtils;


public class commonLanguageSetting extends BaseMenuFragment implements OnClickListener{
	private static final int CHINISE = 0;
	private static final int ENGLISH = 1;
	private int mLanguageSelect = 0;
	private ImageView focusFrame;
	private Button bntSure;
	private Button bntCancel;
	private TextView txtChinise;
	private TextView txtEnglish;

	@Override
	protected View onInflateLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.language_setting, container, false);
		
        txtChinise = (TextView)v.findViewById(R.id.language_ch); 
        txtEnglish = (TextView)v.findViewById(R.id.language_en); 
        bntSure = (Button)v.findViewById(R.id.language_sure);
        bntCancel = (Button)v.findViewById(R.id.language_cancel);
        focusFrame = (ImageView)v.findViewById(R.id.language_foucs);
        
        mLanguageSelect = OSDLanguage.getCurOsdLanguage(getActivity());
	     
        if(mLanguageSelect == CHINISE)
        {
        	txtChinise.setBackgroundResource(R.drawable.item_normal);
        	txtEnglish.setBackgroundResource(R.drawable.edit_normal);
        }
        else
        {
        	txtChinise.setBackgroundResource(R.drawable.edit_normal);
        	txtEnglish.setBackgroundResource(R.drawable.item_normal);   
        }
        bntSure.setBackgroundResource(R.drawable.item_focus);
        
		txtChinise.setOnClickListener(onClick);
		txtEnglish.setOnClickListener(onClick);
		bntSure.setOnClickListener(onClick);
		bntCancel.setOnClickListener(onClick);   
        
		return v;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = onInflateLayout(inflater, container, savedInstanceState);
        
        txtChinise.setOnFocusChangeListener(mLanguageChangeListener);
        txtEnglish.setOnFocusChangeListener(mLanguageChangeListener);
        bntSure.setOnFocusChangeListener(mLanguageChangeListener);
        bntCancel.setOnFocusChangeListener(mLanguageChangeListener);
 		
		return view;
	}	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    
	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
	
    
    View.OnFocusChangeListener mLanguageChangeListener = new View.OnFocusChangeListener(){
        public void onFocusChange(View v,boolean hasFocus)
        {      
    		switch(v.getId())
    		{
    			case R.id.language_ch:
    			case R.id.language_en:
    			{    				
    	            int[] location = new  int[2] ;
    	            TextView mtext = (TextView)v.findViewById(v.getId());

    	            if(true == hasFocus)
    	            {
    	            	focusFrame.setVisibility(View.VISIBLE);
    	            	mtext.getLocationOnScreen(location);
    	                FocusAnimator focusAnimator = new FocusAnimator();
    	                if(v.getId() == R.id.language_ch)
    	                	focusAnimator.flyFoucsFrame(focusFrame, mtext.getWidth(), mtext.getHeight(), location[0], location[1]);
    	                else
    	                	focusAnimator.flyFoucsFrame(focusFrame, mtext.getWidth(), mtext.getHeight(), location[0], location[1]);    	                   	                   	                
    	                bntSure.setBackgroundResource(R.drawable.item_normal);
    	                bntCancel.setBackgroundResource(R.drawable.item_normal);
    	            }
    	            break;
    			}   				
        		case R.id.language_sure:
        			if(true == hasFocus) 
        			{
        				focusFrame.setVisibility(View.GONE);
        				bntSure.setBackgroundResource(R.drawable.item_focus); 
        				bntCancel.setBackgroundResource(R.drawable.item_normal); 
        			}
        			break;
        		case R.id.language_cancel:
        			if(true == hasFocus)  
        			{
        				focusFrame.setVisibility(View.GONE);
        				bntCancel.setBackgroundResource(R.drawable.item_focus);
        				bntSure.setBackgroundResource(R.drawable.item_normal); 
        			}
        			break;        			
    			default:
    				break;
    		}            
        }
    };
    
    
    View.OnClickListener onClick = new View.OnClickListener()
    {
        public void onClick(View v)
        {        	
            switch (v.getId())
            {
	    		case R.id.language_ch:
	    			mLanguageSelect = CHINISE;
	    			txtEnglish.setBackgroundResource(R.drawable.edit_normal);
	    			txtChinise.setBackgroundResource(R.drawable.item_normal);	    			
	    			break;
	    		case R.id.language_en:
	    			mLanguageSelect = ENGLISH;
	    			txtChinise.setBackgroundResource(R.drawable.edit_normal);
	    			txtEnglish.setBackgroundResource(R.drawable.item_normal);
	    			break;
	    		case R.id.language_sure:	    			
					try {
						OSDLanguage.setOSDLanguage(mLanguageSelect, getActivity());
					} catch (Exception e) {
						Log.i("Exception", e.toString());
					}	
					exitLanguageSetting();
	    			break;
	    		case R.id.language_cancel:
	    			exitLanguageSetting();
	    			break;            
            }
        }
    };
    
    private void exitLanguageSetting()
    {
    	FragmentUtils.popBackSubFragment(this);
    }
    
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCreateMenuItems(List<MenuItem> items) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getTitle() {
		// TODO Auto-generated method stub
		return 0;
	}
}
