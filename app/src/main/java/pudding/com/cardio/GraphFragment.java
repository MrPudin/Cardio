package pudding.com.cardio;

import android.app.Fragment;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidplot.Plot;
import com.androidplot.PlotListener;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GraphFragment extends Fragment {
    private static String STATE_DATA = "graph_fragment_state_data";

    private XYPlot graphView;

    private HashMap<String,ArrayList<Integer>> xData;
    private HashMap<String,ArrayList<Integer>> yData;
    private HashMap<String, Pair<Integer, Integer>> color;

    private Point cursor;

    private Lock lock;


    public static GraphFragment newInstance(@Nullable HashMap<String, ArrayList<Point>> data) {
        Bundle args = new Bundle();
        args.putSerializable(GraphFragment.STATE_DATA, data);

        GraphFragment fragment = new GraphFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public GraphFragment() {
        this.lock = new ReentrantLock();
        this.cursor = new Point(0, 0);

        this.xData = new HashMap<>();
        this.yData = new HashMap<>();

        this.color = new HashMap<>();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null)
        {
            HashMap<String, ArrayList<Point>> data =
                    ((HashMap<String, ArrayList<Point>>)
                            savedInstanceState.getSerializable(GraphFragment.STATE_DATA));
            this.splitData(data);
            this.updateCursor();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_graph, container, false);
        this.graphView = (XYPlot)view.findViewById(R.id.view_graph);
        this.graphView.addListener(new PlotListener() {
            @Override
            public void onBeforeDraw(Plot source, Canvas canvas) {
                GraphFragment.this.lock.lock();
            }

            @Override
            public void onAfterDraw(Plot source, Canvas canvas) {
                GraphFragment.this.lock.unlock();
            }
        });

        this.draw(); //Draw Predefined Data

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(GraphFragment.STATE_DATA, this.mergeData());
    }

    public void addGraph(String name, int colorGraph, int colorVertex)
    {
        this.lock.lock();
        this.xData.put(name , new ArrayList<Integer>());
        this.yData.put(name , new ArrayList<Integer>());
        this.color.put(name, new Pair<Integer, Integer>(colorGraph, colorVertex));
        this.lock.unlock();

        this.draw();
    }

    public void addPoint(String graphName, Point point)
    {
        this.lock.lock();
        //Split data
        this.xData.get(graphName).add(point.x);
        this.yData.get(graphName).add(point.y);

        //Update Cursor
        this.cursor.x = Math.max(this.cursor.x, point.x);
        this.cursor.y = Math.max(this.cursor.y, point.y);

        this.lock.unlock();

        this.draw();
    }

    //Utility Methods
    private void draw()
    {
        if(this.xData.size() > 0 && this.yData.size() > 0)
            if(this.graphView != null)
            {
                this.lock.lock();

                this.graphView.clear();

                //Load Data into Graph View
                for (String graphName : this.xData.keySet()) {
                    final XYSeries series =
                            new SimpleXYSeries(
                                    this.xData.get(graphName), this.yData.get(graphName), graphName);

                    final LineAndPointFormatter formatter =
                            new LineAndPointFormatter(this.color.get(graphName).first,
                                    this.color.get(graphName).second,
                                    Color.TRANSPARENT,
                                    null);

                    GraphFragment.this.graphView.addSeries(series, formatter);
                }

                this.lock.unlock();

                this.graphView.redraw();
            }
    }

    private void updateCursor()
    {
        if(this.cursor == null) this.cursor = new Point(0, 0);

        for(String graphName : this.xData.keySet()) {
            for(int i = 0; i < this.xData.size(); i ++)

            {
                this.cursor.x = Math.max(this.yData.get(graphName).get(i), this.cursor.x);
                this.cursor.y = Math.max(this.yData.get(graphName).get(i), this.cursor.y);
            }
        }
    }

    private void splitData(HashMap<String, ArrayList<Point>> data)
    {
        for(String graphName : data.keySet())
        {
            for(Point pt : data.get(graphName))
            {
                this.xData.get(graphName).add(pt.x);
                this.yData.get(graphName).add(pt.y);
            }
        }

    }

    private HashMap<String, ArrayList<Point>> mergeData()
    {
        HashMap<String, ArrayList<Point>> data = new HashMap<>();
        for(String graphName : this.xData.keySet())
        {
            data.put(graphName, new ArrayList<Point>());

            for(int i = 0; i < this.xData.get(graphName).size(); i ++) {
                Point point =
                        new Point(this.xData.get(graphName).get(i), this.yData.get(graphName).get(i));

                data.get(graphName).set(i , point);
            }
        }

        return data;
    }
}
