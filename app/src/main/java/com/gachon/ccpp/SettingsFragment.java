package com.gachon.ccpp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragmentCompat {

//    SharedPreferences prefs;
//
//    Preference keywordLanguagePreference;
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        addPreferencesFromResource(R.xml.settings_preference);
//        keywordLanguagePreference = findPreference("language_list");
//
//        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
//
//    }

    private static final String SETTING_NOTIFICATION = "notification_alarm";
    private static final String SETTING_ASSIGNMENT = "assignment_alarm";
    private static final String SETTING_COMMENT = "comment_alarm";
    private static final String SETTING_LANGUAGE = "language_list";
    SharedPreferences prefs;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings_preference);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.registerOnSharedPreferenceChangeListener(prefListener);
    }

    SharedPreferences.OnSharedPreferenceChangeListener prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(SETTING_NOTIFICATION)) {
                Log.d("TAG", key + "SELECTED");
            }
            if (key.equals(SETTING_ASSIGNMENT)) {
                Log.d("TAG", key + "SELECTED");
            }
            if (key.equals(SETTING_COMMENT)) {
                Log.d("TAG", key + "SELECTED");
            }
            if (key.equals(SETTING_LANGUAGE)) {
                Log.d("TAG", key + "SELECTED");
            }
        }
    };

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_setting, container, false);
//    }


}
