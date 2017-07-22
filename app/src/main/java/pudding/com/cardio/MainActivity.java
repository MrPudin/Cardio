package pudding.com.cardio;

import android.Manifest;
import android.content.DialogInterface;
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

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;


public class MainActivity
        extends AppCompatActivity
        implements CameraBridgeViewBase.CvCameraViewListener2
{
    private static String STATE_LAYOUT = "main_activity_main_state";
    private static int LAYOUT_DISPLAY = 0;
    private static int LAYOUT_CONFIG = 0;

    private static int PERMISSION_CAMERA_REQUEST_CODE = 1;

    private static String LOG_TAG = "Cardio.MainActivity";

    private int layout; //Current Layout


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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == MainActivity.PERMISSION_CAMERA_REQUEST_CODE) {
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

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(MainActivity.STATE_LAYOUT, this.layout);
    }

    //Menu
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

        }
        else if(item.getItemId() == R.id.menu_item_toggle)
        {

        }

        return true;
    }

    //Camera Methods
    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

    }

    @Override
    public void onCameraViewStopped() {
    }
}
