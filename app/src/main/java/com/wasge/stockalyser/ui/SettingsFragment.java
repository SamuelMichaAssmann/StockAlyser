package com.wasge.stockalyser.ui;

import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;
import com.wasge.stockalyser.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }


}