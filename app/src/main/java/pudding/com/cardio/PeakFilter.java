package pudding.com.cardio;

import android.util.Log;

import java.util.ArrayList;

public class PeakFilter {

    //Filter Parameters
    private int logSize;
    private double peakThreshold;
    private ArrayList<Double> log;

    //Cache
    private boolean cacheMeanValid;
    private double cacheMean;
    private boolean cacheStandardDeviationValid;
    private double cacheStandardDeviation;

    public PeakFilter()
    {
        this.logSize = 3;
        this.peakThreshold = 1.0;
        this.log = new ArrayList<>();

        this.cacheMeanValid = false;
        this.cacheStandardDeviationValid = false;
        this.cacheMean = 0.0;
        this.cacheStandardDeviation = 0.0;
    }

    public void seed(double value)
    {
        //Invalidate Cache
        this.cacheMeanValid = false;
        this.cacheStandardDeviationValid = false;

        if(this.log.size() == logSize) this.log.remove(1);
        this.log.add(value);
    }

    public boolean determinePeak(double value)
    {
        if(this.log.size() > this.logSize)
        {
            double deviation =  value - this.computeMean();
            double zScore = deviation / this.computeStandardDeviation();

            Log.d("PeakFilter", "Found Peak");
            if(zScore > this.peakThreshold) return true; //Found Peak
        }

        Log.d("PeakFilter", "NO Peak");
        this.seed(value);
        return false;
    }


    //Utility Methods
    protected double computeMean()
    {
        if(cacheMeanValid == true) return this.cacheMean;

        double sum = 0.0;
        for(double value : this.log) sum += value;

        this.cacheMean = sum / ((double)this.log.size());
        this.cacheMeanValid = true;

        return this.cacheMean;
    }

    protected double computeStandardDeviation()
    {
        if(cacheStandardDeviationValid == true) return this.cacheStandardDeviation;

        double sum = 0.0;
        for(double value : this.log) sum += Math.pow((value - this.computeMean()), 2);

        this.cacheStandardDeviation = Math.sqrt((sum / ((double)this.log.size())));
        this.cacheStandardDeviationValid = true;

        return this.cacheStandardDeviation;
    }


    //Setters - Filter Parameters
    public void setLogSize(int logSize) {
        //Invalidate Cache
        this.cacheMeanValid = false;
        this.cacheStandardDeviationValid = false;

        this.logSize = logSize;
    }

    public void setPeakThreshold(double peakThreshold) {
        this.peakThreshold = peakThreshold;
    }
}
