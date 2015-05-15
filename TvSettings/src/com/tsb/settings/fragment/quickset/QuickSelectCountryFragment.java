package com.tsb.settings.fragment.quickset;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.tsb.settings.R;
import com.tsb.settings.TvManagerHelper;
import com.tsb.settings.fragment.menu.BaseMenuFragment;
import com.tsb.settings.menu.MenuItem;
import com.tsb.settings.util.FragmentUtils;

import java.util.List;

@Deprecated
public class QuickSelectCountryFragment extends BaseMenuFragment implements OnClickListener {
    
    private static final int[] VALUES_COUNTRY = {
        TvManagerHelper.COUNTRY_BAHRAIN,
        TvManagerHelper.COUNTRY_INDIA,
        TvManagerHelper.COUNTRY_INDONESIA,
        TvManagerHelper.COUNTRY_ISRAEL,
        TvManagerHelper.COUNTRY_KUWAIT,
        TvManagerHelper.COUNTRY_MALAYSIA,
        TvManagerHelper.COUNTRY_PHILIPPINES,
        TvManagerHelper.COUNTRY_QATAR,
        TvManagerHelper.COUNTRY_SAUDI_ARABIA,
        TvManagerHelper.COUNTRY_SINGAPORE,
        TvManagerHelper.COUNTRY_THAILAND,
        TvManagerHelper.COUNTRY_UNITED_ARAB_EMIRATES,
        TvManagerHelper.COUNTRY_VIETNAM };

    @Override
    public void onCreateMenuItems(List<MenuItem> items) {
        final TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
    }
    
    @Override
    protected View onInflateLayout(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_fragment_quick_select_country, container, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        v.findViewById(R.id.button_next).setOnClickListener(this);
        return v;
    }

    @Override
    public int getTitle() {
        return R.string.STRING_COUNTRY;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_next:
                FragmentUtils.showSubFragment(this, QuickSelectLocationFragment.class);
                break;
            default:
                break;
        }
    }

}
