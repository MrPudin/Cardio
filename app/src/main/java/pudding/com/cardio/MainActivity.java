package pudding.com.cardio;

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;


public class MainActivity
        extends AppCompatActivity
        implements CameraBridgeViewBase.CvCameraViewListener2
{
    public static String SHARED_PREFERENCE_FILE_NAME = "pudding.com.cardio.config";

    private static String STATE_LAYOUT = "main_activity_main_state";
    private static int LAYOUT_DISPLAY = 0;
    private static int LAYOUT_CONFIG = 1;
    private static int PERMISSION_CAMERA_REQUEST_CODE = 1;
    private static String LOG_TAG = "Cardio.MainActivity";

    private int layout; //Current Layout
    private boolean layoutConfig; //True - Camera View shown, False - Graph View Shown

    private BlobLocator locator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Load OpenCV
        System.loadLibrary(getString(R.string.library_opencv_name));

        //Request Camera Permission
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                MainActivity.PERMISSION_CAMERA_REQUEST_CODE);

        //Determine Layout
        this.layout = MainActivity.LAYOUT_DISPLAY;
        if(savedInstanceState != null)
            this.layout = savedInstanceState.getInt(MainActivity.STATE_LAYOUT);

        //Setup Utility Objects
        this.locator = new BlobLocator();
        this.loadConfig();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == MainActivity.PERMISSION_CAMERA_REQUEST_CODE) {
            //Camera Permission Rejected
            if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                //Terminate due to Lack of Camera Permissions
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                dialogBuilder.setMessage(R.string.dialog_camera_message);
                dialogBuilder.setCancelable(false); //Block Back Button
                dialogBuilder.setPositiveButton(R.string.dialog_camera_button_title,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity.this.finish(); //Die Gracefully
                            }
                        });

                dialogBuilder.create().show();
            }
        }

        //Camera Permission Accepted
        this.setupLayout(this.layout);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(MainActivity.STATE_LAYOUT, this.layout);
    }

    //Menu Methods
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if(this.layout == MainActivity.LAYOUT_DISPLAY)
            getMenuInflater().inflate(R.menu.menu_display, menu);
        else if(this.layout == MainActivity.LAYOUT_CONFIG)
            getMenuInflater().inflate(R.menu.menu_config, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.menu_item_config)
        {
            this.setupLayout(MainActivity.LAYOUT_CONFIG);
        }
        else if(item.getItemId() == R.id.menu_item_toggle)
        {
            this.toggleConfigUI();
        }

        return true;
    }

    //Camera Methods
    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        //Locate LED
        Mat frame = inputFrame.gray();

        return frame;
    }

    @Override
    public void onCameraViewStopped() {
    }

    private void setupCameraView()
    {
        CameraBridgeViewBase cameraView = (CameraBridgeViewBase) findViewById(R.id.view_cv_camera);
        cameraView.setCvCameraViewListener(this);
        cameraView.enableView();
        cameraView.setVisibility(SurfaceView.VISIBLE);
    }

    //UI Methods
    private void setupLayout(int layout)
    {
        if(layout == MainActivity.LAYOUT_CONFIG)
        {
            this.setupCameraView();
            this.setContentView(R.layout.activity_main_configuration);
            this.showGraphFragment();
        }
        else //Display Layout
        {
            this.setupCameraView();
            this.setContentView(R.layout.activity_main_display);
            this.showGraphFragment();
            this.showConfigFragment();
        }
    }

    private void showGraphFragment()
    {
        GraphFragment graphFragment =
                (GraphFragment)getFragmentManager().
                        findFragmentById(R.id.frame_fragment_display);

        if(graphFragment == null)
        {
            graphFragment = GraphFragment.newInstance(null);
            getFragmentManager().beginTransaction().add(R.id.frame_fragment_graph, graphFragment);
        }

        if(layout == MainActivity.LAYOUT_CONFIG)
        {
            graphFragment.addGraph(getString(R.string.graph_signal_name),
                    ContextCompat.getColor(this, R.color.view_graph_color_signal),
                    ContextCompat.getColor(this, R.color.view_graph_color_signal));

            graphFragment.addGraph(getString(R.string.graph_mean_name),
                    ContextCompat.getColor(this, R.color.view_graph_color_mean),
                    ContextCompat.getColor(this, R.color.view_graph_color_mean));

            graphFragment.addGraph(getString(R.string.graph_standard_deviation_name),
                    ContextCompat.getColor(this, R.color.view_graph_color_standard_deviation),
                    ContextCompat.getColor(this, R.color.view_graph_color_standard_deviation));
        }
        else //Display Layout
        {
            graphFragment.addGraph(getString(R.string.graph_beat_name),
                    ContextCompat.getColor(this, R.color.view_graph_color_beat),
                    ContextCompat.getColor(this, R.color.view_graph_color_beat));
        }
    }

    private void showConfigFragment()
    {
        if(this.layout == MainActivity.LAYOUT_CONFIG) {
            ConfigFragment configFragment =
                    (ConfigFragment)
                            getFragmentManager().findFragmentById(R.id.frame_fragment_config);

            if (configFragment == null) {
                configFragment = ConfigFragment.newInstance();
                getFragmentManager().beginTransaction().
                        add(R.id.frame_fragment_config, configFragment);
            }
        }

    }

    private void toggleConfigUI()
    {
        if(this.layoutConfig == true)
        {
            //Camera View Shown currently
            getFragmentManager().findFragmentById(R.id.frame_fragment_graph).getView().
                    bringToFront();
        }
        else
        {
            //Graph View Shown currently
            findViewById(R.id.view_cv_camera).bringToFront();
        }
    }

    //Utility Methods
    public void loadConfig() {
        SharedPreferences preferences =
                getSharedPreferences(MainActivity.SHARED_PREFERENCE_FILE_NAME, 0);

        this.locator.setBlobColor(
                Float.parseFloat(
                        preferences.getString("blob_color", "255.0")));
        this.locator.setBlobMinArea(
                Float.parseFloat(
                        preferences.getString("blob_min_area", "100.0")));
        this.locator.setBlobMinCircularity(
                Float.parseFloat(
                        preferences.getString("blob_min_circularity", "0.8")));
        this.locator.setBlobMinConvexity(
                Float.parseFloat(
                        preferences.getString("blob_min_convexity", "0.7")));
        this.locator.setBlobMinInertia(
                Float.parseFloat(
                        preferences.getString("blob_min_inertia", "0.7")));
        this.locator.loadConfig();
    }
}
