package pudding.com.cardio;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

public class ConfigFragment extends PreferenceFragment {

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
    public void onPause() {
        super.onPause();

        ((MainActivity)getActivity()).loadConfig();
    }
}
