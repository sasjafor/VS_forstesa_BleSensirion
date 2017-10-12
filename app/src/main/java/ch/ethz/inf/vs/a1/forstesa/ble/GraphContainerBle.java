package ch.ethz.inf.vs.a1.forstesa.ble;

import android.graphics.Color;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GraphContainerBle{

    GraphContainerBle(GraphView graph_view){
        graph = graph_view;
        GridLabelRenderer gridLabel = graph_view.getGridLabelRenderer();
        gridLabel.setHorizontalAxisTitle("timestamp");
        ser0 = new LineGraphSeries<>();
        ser1 = new LineGraphSeries<>();
        ser0.setColor(Color.BLUE);
        ser1.setColor(Color.RED);
        ser0.setTitle("Humidity in %");
        ser1.setTitle("Temperature in °C");
        graph.addSeries(ser0);
        graph.addSeries(ser1);
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
    }

    public void addValue(double xIndex, float value, int series_nr){
        if(series_nr == 0){
            ser0.appendData(new DataPoint(xIndex,value),true,100);
        } else if (series_nr == 1){
            ser1.appendData(new DataPoint(xIndex,value),true,100);
        }
    }

    private GraphView graph;
    private LineGraphSeries<DataPoint> ser0,ser1;
}
