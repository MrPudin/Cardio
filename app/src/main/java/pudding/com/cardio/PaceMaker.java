package pudding.com.cardio;

import java.util.ArrayList;

public class PaceMaker {
    //Pacemaker Parameters
    private int logSize;
    private int seedDuration; //Seed Duration in milliseconds
    private int period;

    private ArrayList<Double> log;
    private long seedBegin;
    private int seedCount;

    public PaceMaker()
    {
        this.logSize = 3;
        this.log = new ArrayList<>();

        this.seedDuration = 2500;
        this.seedBegin = -1;

        this.period = 60 * 1000; //Default period: 1 minute
    }

    public void seed()
    {
        if(System.currentTimeMillis() - this.seedBegin >= this.seedDuration) //Seeding Finished
        {
            this.pace();

            this.seedBegin = System.currentTimeMillis();
            this.seedCount = 0;
        }
        else if(this.seedBegin == -1) //Seeding has not begun
        {
            this.seedBegin = System.currentTimeMillis();
            this.seedCount = 0;
        }
        else //Seeding in Process
        {
            this.seedCount ++;
        }

    }

    public double pace()
    {
        if (this.seedBegin == -1 || System.currentTimeMillis() - this.seedBegin < this.seedDuration)
            return -1.0; //Invalid state to compute pace

        //Compute pace in seed per minute
        int duration = (int) (System.currentTimeMillis() - this.seedBegin);
        double pace = (this.seedCount / this.seedDuration) * (this.seedDuration) / ((double)period);

        //Maintain Log
        if(log.size() == logSize) this.log.remove(0);
        this.log.add(pace);

        return this.logMean();
    }

    //Utility Methods
    private double logMean()
    {
        double sum = 0.0;
        for(double value : this.log)
        {
            sum += value;
        }

        return sum / ((double) this.log.size());
    }

    //Setters - Pacemaker parameters
    public void setLogSize(int logSize) {
        this.logSize = logSize;

    }

    public void setSeedDuration(int seedDuration) {
        this.seedBegin = -1; //Restart seeding
        this.seedDuration = seedDuration;
    }

    public void setPeriod(int period) {
        this.period = period;
    }
}
