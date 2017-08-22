package com.example.mpandroid;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    ArrayList<String> mClickedLegends = new ArrayList<String>();
    ArrayList<Highlight> mHighlightlist = new ArrayList<Highlight>();

    private CheckBox mRedLegend;
    private CheckBox mYellowLegend;
    private CheckBox mGreenLegend;
    private CheckBox mOrangeLegend;
    private CheckBox mBlueLegend;
    private BarDataSet set[];
    private BarChart mCombinedChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ArrayList<CheckBox> buttonList = new ArrayList<>();

        mRedLegend = (CheckBox) findViewById(R.id.red_toggle);
        mYellowLegend = (CheckBox) findViewById(R.id.yellow_toggle);
        mGreenLegend = (CheckBox) findViewById(R.id.green_toggle);
        mOrangeLegend = (CheckBox) findViewById(R.id.orange_toggle);
        mBlueLegend = (CheckBox) findViewById(R.id.blue_toggle);


        buttonList.add(mRedLegend);
        buttonList.add(mYellowLegend);
        buttonList.add(mGreenLegend);
        buttonList.add(mOrangeLegend);
        buttonList.add(mBlueLegend);


        mCombinedChart = (BarChart) findViewById(R.id.chart);
        setUpGraph();

        final CompoundButton.OnCheckedChangeListener toggleListener = new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCombinedChart.clear();
                for (CheckBox button : buttonList) {
                    if (button == buttonView) {
                        String button_id = button.toString().split("/")[1].replace('}', ' ').trim();
                        if (buttonView.isChecked()) {
                            mClickedLegends.add(button_id);
                        } else {
                            mClickedLegends.remove(button_id);
                        }
                    }
                }
                mHighlightlist = new ArrayList<Highlight>();
                BarData data = generateBarData(mClickedLegends);
                createAverage(data);
                mCombinedChart.setData(data);
                mCombinedChart.notifyDataSetChanged();
                mCombinedChart.invalidate();
            }
        };
        for (CheckBox button : buttonList) {
            button.setOnCheckedChangeListener(toggleListener);
            button.setChecked(true);
        }

/*Highlight h = mChart.getHighlightByTouchPoint(e.getX(), e.getY());
        performHighlight(h, e);*/

        mCombinedChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            String previous_parameter = new String();

            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (e != null || h != null) {
                    String parameter = mCombinedChart.getBarData().getDataSetByIndex(h.getDataSetIndex()).getLabel();
                    previous_parameter = parameter;
                    Log.i("indu", "onValueSelected: " + parameter+"e "+e);
                    if (mCombinedChart.getBarData().getDataSetByIndex(h.getDataSetIndex()).getYMax() > h.getY()) {
                        Toast.makeText(MainActivity.this, "selected outside " + h.getX(), Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(MainActivity.this, "selected inside " + h.getX(), Toast.LENGTH_SHORT).show();

                    }
                    drawLineGraph(parameter);
                } else {
                    drawLineGraph(previous_parameter);
                }
            }

            @Override
            public void onNothingSelected() {
                Log.i("indu", "onNothingSelected: ");
            }
        });
    }

    private void drawLineGraph(String parameter) {
        Intent i = new Intent(this, LineActivity.class);
        i.putExtra("parameter", parameter);
        startActivity(i);
    }

    private void setUpGraph() {
        mCombinedChart.getXAxis().setAxisMinimum(0);
        mCombinedChart.getXAxis().setAxisMaximum(24);

        mCombinedChart.getAxisLeft().setAxisMinimum(0);
        mCombinedChart.getAxisLeft().setAxisMaximum(100);
        mCombinedChart.getAxisRight().setAxisMinimum(0);
        mCombinedChart.getAxisRight().setAxisMaximum(10000);

        mCombinedChart.getAxisLeft().setDrawGridLines(false); // disable grid lines for the left YAxis
        mCombinedChart.getAxisRight().setDrawGridLines(false);
        mCombinedChart.getXAxis().setGranularityEnabled(false);
        mCombinedChart.setHighlightPerDragEnabled(false);
        mCombinedChart.zoom(10f, 1f, 4f, 40);
        mCombinedChart.moveViewToX(0.5f);
        Description desc = null;
        mCombinedChart.setDescription(desc);
        Legend l = mCombinedChart.getLegend();
        l.setEnabled(false);
        XAxis xAxis = mCombinedChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        xAxis.setLabelCount(0);
        YAxis axisRight = mCombinedChart.getAxisRight();
        axisRight.setLabelCount(10);
        axisRight.setTextColor(Color.RED);
        axisRight.setDrawGridLines(true);
        YAxis axisLeft = mCombinedChart.getAxisLeft();
        axisLeft.setLabelCount(10);
        mCombinedChart.getViewPortHandler().setMaximumScaleX(70f);
        mCombinedChart.getViewPortHandler().setMaximumScaleY(70f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                final HashMap<Integer, String> numMap = new HashMap<>();
                numMap.put(0, "");
                numMap.put(1, "1am");
                numMap.put(2, "2am");
                numMap.put(3, "3am");
                numMap.put(4, "4am");
                numMap.put(5, "5am");
                numMap.put(6, "6am");
                numMap.put(7, "7am");
                numMap.put(8, "8am");
                numMap.put(9, "9am");
                numMap.put(10, "10am");
                numMap.put(11, "11am");
                numMap.put(12, "12pm");
                numMap.put(13, "1pm");
                numMap.put(14, "2pm");
                numMap.put(15, "3pm");
                numMap.put(16, "4pm");
                numMap.put(17, "5pm");
                numMap.put(18, "6pm");
                numMap.put(19, "7pm");
                numMap.put(20, "8pm");
                numMap.put(21, "9pm");
                numMap.put(22, "10pm");
                numMap.put(23, "11pm");
                numMap.put(24, "12am");
                numMap.put(25, "");
                return numMap.get((int)value);
            }
        });
       /* final HashMap<Integer, String> numMap = new HashMap<>();
        numMap.put(1, "1am");
        numMap.put(2, "2am");
        numMap.put(3, "3am");
        numMap.put(4, "4am");
        numMap.put(5, "5am");
        numMap.put(6, "6am");
        numMap.put(7, "7am");
        numMap.put(8, "8am");
        numMap.put(9, "9am");
        numMap.put(10, "10am");
        numMap.put(11, "11am");
        numMap.put(12, "12pm");
        numMap.put(13, "1pm");
        numMap.put(14, "2pm");
        numMap.put(15, "3pm");
        numMap.put(16, "4pm");
        numMap.put(17, "5pm");
        numMap.put(18, "6pm");
        numMap.put(19, "7pm");
        numMap.put(20, "8pm");
        numMap.put(21, "9pm");
        numMap.put(22, "10pm");
        numMap.put(21, "11pm");
        numMap.put(22, "12am");
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return numMap.get((int)value);
            }
        });*/
    }

    private BarData generateBarData(ArrayList<String> mClickedLegends) {

        BarData d;
        List<ArrayList<BarEntry>> lists = new ArrayList<>();
        ArrayList<String> colors = new ArrayList();
        for (int i = 0; i < mClickedLegends.size(); i++) {
            ArrayList<BarEntry> list = new ArrayList<BarEntry>();
            for (int index = 1; index <= 10; index++) {
                float rnd = (float) (Math.random() * 100);
                list.add(new BarEntry(index, rnd));
            }

            colors.add(mClickedLegends.get(i));
            lists.add(list);
        }
        set = createSet(lists, colors);
        d = addSet(mClickedLegends, set);
        return d;
    }

    private BarData addSet(ArrayList<String> mClickedLegends, BarDataSet... set) {
        float groupSpace;
        float barSpace;
        float barWidth;

        switch (set.length) {
            case 1:
                BarData d1 = new BarData(set[0]);
                barWidth = 0.1f;
                d1.setBarWidth(barWidth);
                return d1;
            case 2:
                BarData d2 = new BarData(set[0], set[1]);
                groupSpace = 0.8f;
                barSpace = 0.0f;
                barWidth = 0.1f;
                d2.setBarWidth(barWidth);
                d2.groupBars(0.5f, groupSpace, barSpace);
                return d2;
            case 3:
                BarData d3 = new BarData(set[0], set[1], set[2]);
                groupSpace = 0.7f;
                barSpace = 0.0f;
                barWidth = 0.1f;
                d3.setBarWidth(barWidth);
                d3.groupBars(0.5f, groupSpace, barSpace);
                return d3;
            case 4:
                BarData d4 = new BarData(set[0], set[1], set[2], set[3]);
                groupSpace = 0.6f;
                barSpace = 0.0f;
                barWidth = 0.1f;
                d4.setBarWidth(barWidth);
                d4.groupBars(0.5f, groupSpace, barSpace);
                return d4;
            case 5:
                BarData d5 = new BarData(set[0], set[1], set[2], set[3], set[4]);
                groupSpace = 0.5f;
                barSpace = 0.0f;
                barWidth = 0.1f;
                d5.setBarWidth(barWidth);
                d5.groupBars(0.5f, groupSpace, barSpace);
                return d5;
        }
        return new BarData();
    }

    //here's where we add data
    private BarDataSet[] createSet(List<ArrayList<BarEntry>> lists, ArrayList<String> colors) {
        BarDataSet set[] = new BarDataSet[lists.size()];
        for (int i = 0; i < lists.size(); i++) {
            BarDataSet sets = new BarDataSet(lists.get(i), "frequency");
            sets.setStackLabels(new String[]{"frequency", "frequency"});
            sets.setValueTextColor(Color.rgb(61, 165, 255));
            sets.setValueTextSize(10f);
            sets.setHighlightEnabled(true);
            sets.setHighLightAlpha(1000);
            switch (colors.get(i)) {
                case "red_toggle":
                    sets.setLabel("frequency");
                    sets.setColor(Color.rgb(204, 0, 0));
                    sets.setAxisDependency(YAxis.AxisDependency.LEFT);

                    break;
                case "yellow_toggle":
                    sets.setLabel("temperature");
                    sets.setColor(Color.rgb(255, 216, 0));
                    sets.setAxisDependency(YAxis.AxisDependency.LEFT);

                    break;
                case "green_toggle":
                    sets.setLabel("humidity");
                    sets.setColor(Color.rgb(0, 170, 36));
                    sets.setAxisDependency(YAxis.AxisDependency.LEFT);

                    break;
                case "orange_toggle":
                    sets.setLabel("sound");
                    sets.setColor(Color.rgb(255, 102, 0));
                    sets.setAxisDependency(YAxis.AxisDependency.LEFT);

                    break;
                case "blue_toggle":
                    sets.setLabel("vibration");
                    sets.setColor(Color.rgb(0, 192, 255));
                    sets.setAxisDependency(YAxis.AxisDependency.LEFT);

                    break;
            }
            set[i] = sets;
        }
        return set;
    }

    private void createAverage(BarData data) {
        List<IBarDataSet> chartset = data.getDataSets();
        Highlight[] highFrequency, highTemperature, highHumidity, highSound, highVibration;
        ArrayList<Highlight[]> myHighlightList = new ArrayList<Highlight[]>();
        for (int i = 0; i < chartset.size(); i++) {

            switch (chartset.get(i).getLabel()) {
                case "frequency":
                    highFrequency = getFrequencyAverages(i);
                    myHighlightList.add(highFrequency);
                    break;

                case "temperature":
                    highTemperature = getTemperatureAverages(i);
                    myHighlightList.add(highTemperature);

                    break;

                case "humidity":
                    highHumidity = getHumidityAverages(i);
                    myHighlightList.add(highHumidity);
                    break;

                case "sound":
                    highSound = getSoundAverages(i);
                    myHighlightList.add(highSound);
                    break;

                case "vibration":
                    highVibration = getVibrationAverages(i);
                    myHighlightList.add(highVibration);
                    break;
            }

        }
        Highlight[] myHighlightArray = new Highlight[myHighlightList.size()];
        Highlight[] mHighlightArray = mHighlightlist.toArray(myHighlightArray);

        mCombinedChart.highlightValues(mHighlightArray);
    }

    private Highlight[] getVibrationAverages(int index) {
        Highlight h1 = new Highlight(10f, 10f, 45f, 55f, index, YAxis.AxisDependency.LEFT); // 1st value to highlight
        Highlight h2 = new Highlight(5f, 15f, 55f, 65f, index, YAxis.AxisDependency.LEFT); // 2nd value to highlight
        Highlight h3 = new Highlight(4f, 20f, 75f, 85f, index, YAxis.AxisDependency.LEFT);
        Highlight h4 = new Highlight(3f, 25f, 95f, 105f, index, YAxis.AxisDependency.LEFT);
        Highlight h5 = new Highlight(2f, 30f, 115f, 125f, index, YAxis.AxisDependency.LEFT);
        mHighlightlist.addAll(Arrays.asList(h1, h2, h3, h4, h5));
        Highlight[] mHighlightArray = new Highlight[mHighlightlist.size()];
        mHighlightArray = mHighlightlist.toArray(mHighlightArray);
        return mHighlightArray;
    }

    private Highlight[] getSoundAverages(int index) {
        Highlight h1 = new Highlight(10f, 10f, 45f, 55f, index, YAxis.AxisDependency.LEFT); // 1st value to highlight
        Highlight h2 = new Highlight(5f, 14f, 55f, 65f, index, YAxis.AxisDependency.LEFT); // 2nd value to highlight
        Highlight h3 = new Highlight(4f, 29f, 75f, 85f, index, YAxis.AxisDependency.LEFT);
        Highlight h4 = new Highlight(3f, 26f, 95f, 105f, index, YAxis.AxisDependency.LEFT);
        Highlight h5 = new Highlight(2f, 38f, 115f, 125f, index, YAxis.AxisDependency.LEFT);
        mHighlightlist.addAll(Arrays.asList(h1, h2, h3, h4, h5));
        Highlight[] mHighlightArray = new Highlight[mHighlightlist.size()];
        mHighlightArray = mHighlightlist.toArray(mHighlightArray);
        return mHighlightArray;
    }

    private Highlight[] getFrequencyAverages(int index) {
        Highlight h1 = new Highlight(1f, 10f, 45f, 55f, index, YAxis.AxisDependency.RIGHT); // 1st value to highlight
        Highlight h2 = new Highlight(5f, 90f, 55f, 65f, index, YAxis.AxisDependency.RIGHT); // 2nd value to highlight
        Highlight h3 = new Highlight(4f, 12f, 75f, 85f, index, YAxis.AxisDependency.RIGHT);
        Highlight h4 = new Highlight(3f, 18f, 95f, 105f, index, YAxis.AxisDependency.RIGHT);
        Highlight h5 = new Highlight(2f, 20f, 115f, 125f, index, YAxis.AxisDependency.RIGHT);
        mHighlightlist.addAll(Arrays.asList(h1, h2, h3, h4, h5));
        Highlight[] mHighlightArray = new Highlight[mHighlightlist.size()];
        mHighlightArray = mHighlightlist.toArray(mHighlightArray);

        return mHighlightArray;
    }

    private Highlight[] getTemperatureAverages(int index) {
        Highlight h1 = new Highlight(10f, 10f, 45f, 55f, index, YAxis.AxisDependency.LEFT); // 1st value to highlight
        Highlight h2 = new Highlight(5f, 20f, 55f, 65f, index, YAxis.AxisDependency.LEFT); // 2nd value to highlight
        Highlight h3 = new Highlight(4f, 30f, 75f, 85f, index, YAxis.AxisDependency.LEFT);
        Highlight h4 = new Highlight(3f, 45f, 95f, 105f, index, YAxis.AxisDependency.LEFT);
        Highlight h5 = new Highlight(2f, 80f, 115f, 125f, index, YAxis.AxisDependency.LEFT);
        mHighlightlist.addAll(Arrays.asList(h1, h2, h3, h4, h5));
        Highlight[] mHighlightArray = new Highlight[mHighlightlist.size()];
        mHighlightArray = mHighlightlist.toArray(mHighlightArray);
        return mHighlightArray;
    }

    private Highlight[] getHumidityAverages(int index) {
        Highlight h1 = new Highlight(1f, 70f, 0, 0, index, YAxis.AxisDependency.LEFT); // 1st value to highlight
        Highlight h2 = new Highlight(2f, 30f, 0, 0, index, YAxis.AxisDependency.LEFT); // 2nd value to highlight
        Highlight h3 = new Highlight(3f, 80f, 0, 0, index, YAxis.AxisDependency.LEFT);
        Highlight h4 = new Highlight(4f, 35f, 0, 0, index, YAxis.AxisDependency.LEFT);
        Highlight h5 = new Highlight(5f, 90f, 0, 0, index, YAxis.AxisDependency.LEFT);
        Highlight h6 = new Highlight(6f, 20f, 0, 0, index, YAxis.AxisDependency.LEFT); // 1st value to highlight
        Highlight h7 = new Highlight(7f, 80f, 0, 0, index, YAxis.AxisDependency.LEFT); // 2nd value to highlight
        Highlight h8 = new Highlight(8f, 10f, 0, 0, index, YAxis.AxisDependency.LEFT);
        Highlight h9 = new Highlight(9f, 55f, 0, 0, index, YAxis.AxisDependency.LEFT);
        Highlight h10 = new Highlight(10f, 99f, 0, 0, index, YAxis.AxisDependency.LEFT);

        mHighlightlist.addAll(Arrays.asList(h1, h2, h3, h4, h5, h6, h7, h8, h9, h10));
        Highlight[] mHighlightArray = new Highlight[mHighlightlist.size()];
        mHighlightArray = mHighlightlist.toArray(mHighlightArray);

        return mHighlightArray;
    }
}