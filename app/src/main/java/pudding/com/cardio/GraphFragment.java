package pudding.com.cardio;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.HashMap;

public class GraphFragment extends Fragment {
    private static String STATE_DATA = "graph_fragment_state_data";

    private XYPlot graphView;

    private HashMap<String,ArrayList<Double>> xData;
    private HashMap<String,ArrayList<Double>> yData;
    private Point offset;
    private HashMap<String, Pair<Integer, Integer>> color;

    private int graphWidth = 2;

    public static GraphFragment newInstance(@Nullable HashMap<String, ArrayList<Point>> data) {
        Bundle args = new Bundle();
        args.putSerializable(GraphFragment.STATE_DATA, data);

        GraphFragment fragment = new GraphFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public GraphFragment() {
        this.offset = new Point(0, 0);

        this.xData = new HashMap<>();
        this.yData = new HashMap<>();

        this.color = new HashMap<>();

        this.graphWidth = 75;
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
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_graph, container, false);
        this.graphView = (XYPlot)view.findViewById(R.id.view_graph);
        this.setupGraph(this.graphView);

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

        this.xData.put(name , new ArrayList<Double>());
        this.yData.put(name , new ArrayList<Double>());
        this.color.put(name, new Pair<Integer, Integer>(colorGraph, colorVertex));


        this.draw();
    }

    public void addPoint(String graphName, Point point)
    {
        //Adjust Data
        Point adjustPoint = new Point(point.x - this.offset.x, point.y - this.offset.y);

        //Maintain Data
        if(this.xData.get(graphName).size() >= this.graphWidth)
        {
            this.xData.get(graphName).remove(0);
            this.yData.get(graphName).remove(0);
        }

        //Split data
        this.xData.get(graphName).add(adjustPoint.x);
        this.yData.get(graphName).add(adjustPoint.y);


        this.draw();
    }

    //Utility Methods
    private void draw()
    {
        if(this.xData.size() > 0 && this.yData.size() > 0)
            if(this.graphView != null)
            {

                this.graphView.clear();

                //Load Data into Graph View
                for (String graphName : this.xData.keySet()) {
                    if(this.xData.get(graphName) != null && this.xData.get(graphName).size() > 0
                            && this.yData.get(graphName) != null &&
                            this.yData.get(graphName).size() > 0) {
                        final XYSeries series =
                                new SimpleXYSeries(
                                        this.xData.get(graphName), this.yData.get(graphName), graphName);

                        final LineAndPointFormatter formatter =
                                new LineAndPointFormatter(this.color.get(graphName).first,
                                        this.color.get(graphName).second,
                                        Color.TRANSPARENT,
                                        null);
                        formatter.getLinePaint().setStrokeWidth((float) 1.0);
                        formatter.getVertexPaint().setStrokeWidth((float) 0.0);
                        formatter.getFillPaint().setColor(this.color.get(graphName).first);

                        GraphFragment.this.graphView.addSeries(series, formatter);
                    }
                }


                this.graphView.redraw();
            }
    }


    private void splitData(HashMap<String, ArrayList<Point>> data)
    {
        for(String graphName : data.keySet())
        {
            for(Point pt : data.get(graphName))
            {
                if(this.xData.get(graphName) == null)
                    this.xData.put(graphName, new ArrayList<Double>());
                if(this.yData.get(graphName) == null)
                    this.yData.put(graphName, new ArrayList<Double>());

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
                    Point point = new Point(this.xData.get(graphName).get(i),
                                    this.yData.get(graphName).get(i));
                    data.get(graphName).add(point);
                }
        }


        return data;
    }

    private void setupGraph(XYPlot graphView)
    {
        graphView.getGraph().getDomainGridLinePaint().setColor(Color.TRANSPARENT);
        graphView.getGraph().getDomainSubGridLinePaint().setColor(Color.TRANSPARENT);
        graphView.getGraph().getRangeGridLinePaint().setColor(Color.TRANSPARENT);
        graphView.getGraph().getRangeSubGridLinePaint().setColor(Color.TRANSPARENT);

        graphView.getGraph().getGridBackgroundPaint().setColor(ContextCompat.getColor(getActivity(),
                R.color.view_graph_color_background));

    }

    public Point getOffset() {
        return offset;
    }

    public void setOffset(Point offset) {
        this.offset = offset;
    }

    public void setGraphWidth(int graphWidth) {
        this.graphWidth = graphWidth;
    }
}
