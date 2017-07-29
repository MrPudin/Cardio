package pudding.com.cardio;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class MatFragment extends Fragment {
    private ImageView imageView;

    public static MatFragment newInstance() {
        
        Bundle args = new Bundle();
        
        MatFragment fragment = new MatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static void drawMarker(Mat mat, Point center, Size size)
    {
        Point pointUpperLeft = new Point(center.x - (size.width / 2.0),
                center.y - (size.height / 2.0));
        Point pointBottomLeft = new Point(center.x + (size.width / 2.0),
                center.y + (size.height / 2.0));

        Scalar color = new Scalar(127, 140, 141);

        Imgproc.rectangle(mat, pointUpperLeft, pointBottomLeft, color, 5);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mat, container, false);

        this.imageView = (ImageView)view.findViewById(R.id.view_image);

        return view;
    }

    public void putMat(Mat mat)
    {
        Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(),
                Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);

        final Bitmap bitmapRef = bitmap;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MatFragment.this.imageView.setImageBitmap(bitmapRef);
            }
        });
    }

}
