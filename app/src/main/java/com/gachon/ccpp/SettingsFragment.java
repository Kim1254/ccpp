package com.gachon.ccpp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceScreen;

public class SettingsFragment extends PreferenceFragment {

    SharedPreferences prefs;

    ListPreference alarmPreference;
    ListPreference keywordLanguagePreference;
    PreferenceScreen keywordScreen;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings_preference);
        alarmPreference = (ListPreference) findPreference("language_list");
        keywordAlarmPreference = (ListPreference) findPreference("keyword_")
    }


}
