package pudding.com.cardio;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

public class ConfigFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener{

    public static ConfigFragment newInstance() {

        Bundle args = new Bundle();

        ConfigFragment fragment = new ConfigFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName(MainActivity.SHARED_PREFERENCE_FILE_NAME);

        this.addPreferencesFromResource(R.xml.fragment_config);
    }

    @Override
    public void onResume() {
        super.onResume();
        //Listen for Preference Changes
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        //Listen for Preference Changes
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //Load Configuration
        ((MainActivity)getActivity()).loadConfig();
    }
}
