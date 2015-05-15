package com.tsb.settings.fragment.quickset;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.tsb.settings.R;
import com.tsb.settings.TvManagerHelper;
import com.tsb.settings.fragment.BaseFragment;
import com.tsb.settings.util.FragmentUtils;

public class QuickSelectLocationFragment extends BaseFragment implements OnClickListener {
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_fragment_quick_select_location, container, false);
        v.findViewById(R.id.button_store).setOnClickListener(this);
        v.findViewById(R.id.button_home).setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
        switch (v.getId()) {
            case R.id.button_store:
                tm.setTvLocation(TvManagerHelper.TV_LOCATION_STORE);
                break;
            case R.id.button_home:
                tm.setTvLocation(TvManagerHelper.TV_LOCATION_HOME);
                break;
            default:
                break;
        }
        FragmentUtils.showSubFragment(this, QuickSelectAtvTableScanFragment.class);
    }

}
