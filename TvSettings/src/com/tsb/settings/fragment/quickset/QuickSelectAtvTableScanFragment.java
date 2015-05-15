package com.tsb.settings.fragment.quickset;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.tsb.settings.R;
import com.tsb.settings.TvManagerHelper;
import com.tsb.settings.fragment.BaseFragment;
import com.tsb.settings.fragment.menu.adtv.AutoTuningFragment;
import com.tsb.settings.util.FragmentUtils;

public class QuickSelectAtvTableScanFragment extends BaseFragment implements OnClickListener {
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_fragment_quick_select_atv_tablescan, container, false);
        v.findViewById(R.id.button_seek_scan).setOnClickListener(this);
        v.findViewById(R.id.button_table_scan).setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
        switch (v.getId()) {
            case R.id.button_seek_scan:
                tm.setAtvTableScanEnabled(false);
                break;
            case R.id.button_table_scan:
            	tm.setAtvTableScanEnabled(true);
                break;
            default:
                break;
        }
        FragmentUtils.showSubFragment(this, AutoTuningFragment.class);
    }

}
