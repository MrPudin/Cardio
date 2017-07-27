package pudding.com.cardio;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

    public void putPace(double pace)
    {
        if(this.paceTextView != null)
        {
            if(pace <= -1.0)
            {
                this.statusTextView.setText(R.string.display_status_computing);
                this.paceTextView.setText("...");
            }
            else this.paceTextView.setText(String.format(Locale.getDefault(), "%.1f", pace));
        }
    }


    public void putStatus(boolean status)
    {
        if(status == true) this.statusTextView.setText(R.string.display_status_computing);
        else this.statusTextView.setText(R.string.display_status_locating);
    }

}
