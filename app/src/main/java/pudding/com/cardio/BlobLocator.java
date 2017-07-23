package pudding.com.cardio;


import android.os.Environment;
import android.util.Log;

import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileWriter;

public class BlobLocator {
    private static String LOG_TAG = "Cardio.BlobLocator";

    //Detector
    FeatureDetector detector;
    Point blobLocation;

    //Detector Parameters
    private double blobColor;
    private double blobMinArea;
    private double blobMinCircularity;
    private double blobMinConvexity;
    private double blobMinInertia;

    public BlobLocator()
    {
        //Detector Default Parameters
        this.blobColor = 255.0;
        this.blobMinArea = 100.0;
        this.blobMinCircularity = 0.8;
        this.blobMinConvexity = 0.7;
        this.blobMinInertia = 0.7;

        this.detector = FeatureDetector.create(FeatureDetector.SIMPLEBLOB);
        this.loadConfig();

    }

    public boolean locate(Mat mat)
    {
        mat = this.process(mat);
        MatOfKeyPoint detectPoints = new MatOfKeyPoint();
        this.detector.detect(mat, detectPoints);

        if(detectPoints.toList().size() == 1) //Detection Success
        {
            KeyPoint point = detectPoints.toList().get(0);
            this.blobLocation = new Point(point.pt.x, point.pt.y);

            Log.d(LOG_TAG, "Detection Succeeded: Found blob");
            return true;
        }
        else
        {
            //Detection Failure
            Log.d(LOG_TAG, "Detection Failed: Could not find blob");
            return false;
        }
    }

    //Utility Methods
    //TODO: Change to Private
    protected Mat process(Mat mat)
    {
        //Convert to Grayscale
        Mat processMat = new Mat(mat.rows(), mat.cols(), mat.type());
        Imgproc.cvtColor(mat, processMat, Imgproc.COLOR_RGBA2GRAY, processMat.channels());

        //Noise Reduction
        Imgproc.dilate(processMat, processMat, null, null, 2);
        Imgproc.erode(processMat, processMat, null, null, 2);
        Imgproc.erode(processMat, processMat, null, null, 2);
        Imgproc.dilate(processMat, processMat, null, null, 2);

        return processMat;
    }


    private void loadConfig()
    {
        File input =
                new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/bd.xml");
        String detectorConfig = "<?xml version=\"1.0\"?>\n" +
                "<opencv_storage>\n" +
                "<thresholdStep>20.</thresholdStep>\n" +
                "<minThreshold>25.</minThreshold>\n" +
                "<maxThreshold>225.</maxThreshold>\n" +
                "<minRepeatability>2</minRepeatability>\n" +
                "<filterByColor>1</filterByColor>\n" +
                "<blobColor>"+this.blobColor +"</blobColor>\n" +
                "<filterByArea>1</filterByArea>\n" +
                "<minArea>"+this.blobMinArea +"</minArea>\n" +
                "<maxArea>1000000000.0</maxArea>\n" +
                "<filterByCircularity>1</filterByCircularity>\n" +
                "<minCircularity>"+this.blobMinCircularity +"</minCircularity>\n" +
                "<maxCircularity>1.0</maxCircularity>\n" +
                "<filterByConvexity></filterByConvexity>\n" +
                "<minConvexity>"+this.blobMinConvexity +"</minConvexity>\n" +
                "<maxConvexity>1.0</maxConvexity>\n" +
                "<filterByInertia>1</filterByInertia>\n" +
                "<minInertiaRatio>"+this.blobMinInertia +"</minInertiaRatio>\n" +
                "<maxInertiaRatio>1.0</maxInertiaRatio>\n" +
                "</opencv_storage>";
        try
        {
            FileWriter writer = new FileWriter(input);
            writer.append(detectorConfig);
            writer.flush();
            writer.close();
        }catch(Exception exp){
            Log.e(BlobLocator.LOG_TAG, exp.getLocalizedMessage());
        };

        this.detector.read(input.getPath());
    }

    //Setters - Filter Parameters
    public void setBlobColor(double blobColor) {
        this.blobColor = blobColor;
        this.loadConfig();
    }

    public void setBlobMinArea(double blobMinArea) {
        this.blobMinArea = blobMinArea;
        this.loadConfig();
    }

    public void setBlobMinCircularity(double blobMinCircularity) {
        this.blobMinCircularity = blobMinCircularity;
        this.loadConfig();
    }

    public void setBlobMinConvexity(double blobMinConvexity) {
        this.blobMinConvexity = blobMinConvexity;
        this.loadConfig();
    }

    public void setBlobMinInertia(double blobMinInertia) {
        this.blobMinInertia = blobMinInertia;
        this.loadConfig();
    }

    //Getter - Blob Location
    public Point getBlobLocation() {
        return blobLocation;
    }
}
