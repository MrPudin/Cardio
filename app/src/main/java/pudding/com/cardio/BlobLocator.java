package pudding.com.cardio;


import android.util.Log;

import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class BlobLocator {
    private static String LOG_TAG = "Cardio.BlobLocator";

    //Detector
    FeatureDetector detector;
    Point blobLocation;
    double blobSize;

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
        this.blobMinArea = 10.0;
        this.blobMinCircularity = 0.8;
        this.blobMinConvexity = 0.7;
        this.blobMinInertia = 0.7;

        this.detector = FeatureDetector.create(FeatureDetector.SIMPLEBLOB);
        this.blobLocation = new Point(0.0, 0.0);
        this.blobSize = 0.0;
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
            this.blobSize = point.size;

            Log.d(LOG_TAG, "Detection Succeeded: Found blob of size" + this.blobSize);
            return true;
        }
        else
        {
            //Detection Failure
            Log.d(LOG_TAG, "Detection Failed: Could not find blob");
            return false;
        }
    }


    public void loadConfig()
    {
        File input;
        try
        {
            input = File.createTempFile("blob_config", "xml");
        }catch (IOException exception)
        {
            Log.e(BlobLocator.LOG_TAG, "Could not load configuration," +
                    "which means BlobLocator would not function correctly");
            Log.e(BlobLocator.LOG_TAG, exception.getLocalizedMessage());
            return;
        }

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
                "<maxArea>2000000.0</maxArea>\n" +
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

    //Utility Methods
    //TODO: Change to Private
    private Mat process(Mat mat)
    {
        Mat processMat = new Mat(mat.rows() / 4, mat.cols() / 4, mat.type());
        Mat tmpMat = new Mat(mat.rows() / 2, mat.cols() / 2, mat.type());

        Mat kernel = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, new Size(3,3));

        //Noise Reduction && Optimisation
        Imgproc.pyrDown(mat, tmpMat, tmpMat.size());
        Imgproc.pyrDown(tmpMat, processMat, processMat.size());

        //Morphological Opening
        Imgproc.erode(processMat, processMat, kernel);
        Imgproc.dilate(processMat, processMat, kernel);

        //Morphological Closing
        Imgproc.dilate(processMat, processMat, kernel);
        Imgproc.erode(processMat, processMat, kernel);


        return processMat;
    }

    //Setters - Filter Parameters
    public void setBlobColor(double blobColor) {
        this.blobColor = blobColor;
    }

    public void setBlobMinArea(double blobMinArea) {
        this.blobMinArea = blobMinArea;
    }

    public void setBlobMinCircularity(double blobMinCircularity) {
        this.blobMinCircularity = blobMinCircularity;
    }

    public void setBlobMinConvexity(double blobMinConvexity) {
        this.blobMinConvexity = blobMinConvexity;
    }

    public void setBlobMinInertia(double blobMinInertia) {
        this.blobMinInertia = blobMinInertia;
    }

    //Getter - Blob Information
    public Point getBlobLocation(){
        //Adjustment from Image Reduction Operation
        return new Point(blobLocation.x * 4.0, blobLocation.y * 4.0);
    }

    public double getBlobSize() {
        //Adjustment from Image Reduction Operation
        return this.blobSize * 4.0;
    }
}
