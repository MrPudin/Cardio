package pudding.com.cardio;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

public class DisplayFragment extends Fragment {
    private TextView paceTextView;
    private TextView statusTextView;

    public static DisplayFragment newInstance() {
        Bundle args = new Bundle();

        DisplayFragment fragment = new DisplayFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_display, container, false);

        this.paceTextView = (TextView)view.findViewById(R.id.view_display_text_pace);
        this.statusTextView = (TextView)view.findViewById(R.id.view_display_text_status);

        return view;
    }

    public void putPace(final double pace)
    {
        if(this.paceTextView != null)
        {
            final double paceRef = pace;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(pace != -1.0) DisplayFragment.this.paceTextView.
                            setText(String.format(Locale.getDefault(), "%.1f", paceRef));
                }
            });
        }
    }


    public void putStatus(boolean status)
    {
        if(this.statusTextView != null) {
            final boolean statusRef = status;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (statusRef == true) {
                        DisplayFragment.this.statusTextView.setText(R.string.display_status_computing);
                        DisplayFragment.this.statusTextView.setBackgroundColor(
                                ContextCompat.getColor(getActivity(),
                                        R.color.view_display_status_compute_color_background));
                    }
                    else
                    {
                        DisplayFragment.this.statusTextView.setText(R.string.display_status_locating);
                        DisplayFragment.this.statusTextView.setBackgroundColor(
                                ContextCompat.getColor(getActivity(),
                                        R.color.view_display_status_locate_color_background));

                        DisplayFragment.this.paceTextView.setText("...");
                    }
                }
            });
        }
    }

}
