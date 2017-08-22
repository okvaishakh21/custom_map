package com.example.mpandroid;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by indu on 13/7/17.
 */

public class LineActivity extends AppCompatActivity {

    private LineChart mChart;
    ArrayList<String> mClickedLegends = new ArrayList<String>();
    private LineDataSet set[];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_linechart);

        final ArrayList<CheckBox> buttonList = new ArrayList<>();

        buttonList.add((CheckBox) findViewById(R.id.red_toggle));
        buttonList.add((CheckBox) findViewById(R.id.yellow_toggle));
        buttonList.add((CheckBox) findViewById(R.id.green_toggle));
        buttonList.add((CheckBox) findViewById(R.id.orange_toggle));
        buttonList.add((CheckBox) findViewById(R.id.blue_toggle));

        mChart = (LineChart) findViewById(R.id.chart1);
        setUpGraph();

        final CompoundButton.OnCheckedChangeListener toggleListener = new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mChart.clear();
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
                LineData data = generateLineData(mClickedLegends);
                mChart.setData(data);
                mChart.notifyDataSetChanged();
                mChart.invalidate();

            }
        };
        String param = getIntent().getStringExtra("parameter");

        for (CheckBox button : buttonList) {
            button.setOnCheckedChangeListener(toggleListener);
            String buttonText = button.getTag().toString();
            if (buttonText.equalsIgnoreCase(param)) {
                button.setChecked(true);
            }
        }

    }

    private void setUpGraph() {
        mChart.getDescription().setEnabled(false);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.WHITE);

        /*LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        // add empty data
        mChart.setData(data);*/

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();
l.setEnabled(false);
        // modify the legend ...
    //    l.setForm(Legend.LegendForm.LINE);
        // l.setTypeface(mTfLight);
    //    l.setTextColor(Color.WHITE);

        XAxis xl = mChart.getXAxis();
        //.setTypeface(mTfLight);
        xl.setTextColor(Color.GRAY);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis = mChart.getAxisLeft();
        //.setTypeface(mTfLight);
        leftAxis.setTextColor(Color.GRAY);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setDrawZeroLine(true);
        mChart.getAxisLeft().setSpaceTop(0f);
        mChart.getAxisLeft().setSpaceBottom(0f);
        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    private LineData generateLineData(ArrayList<String> mClickedLegends) {

        LineData d;
        List<ArrayList<Entry>> lists = new ArrayList<>();
        ArrayList<String> colors = new ArrayList();
        for (int i = 0; i < mClickedLegends.size(); i++) {
            List<Entry> list = new ArrayList<Entry>();
            for (int index = 1; index <= 24; index++) {
                float rnd = (float) (Math.random() * 100);
                list.add(new Entry(index, rnd));
            }

            colors.add(mClickedLegends.get(i));
            lists.add((ArrayList<Entry>) list);
        }
        set = createSet(lists, colors);
        d = addSet(mClickedLegends, set);
        return d;
    }

    private LineDataSet[] createSet(List<ArrayList<Entry>> lists, ArrayList<String> colors) {

        LineDataSet[] set = new LineDataSet[lists.size()];
        for (int i = 0; i < lists.size(); i++) {
            LineDataSet sets = new LineDataSet(lists.get(i), "frequency");
            //sets.setStackLabels(new String[]{"frequency", "frequency"});
            sets.setValueTextColor(Color.rgb(61, 165, 255));
            sets.setValueTextSize(10f);
            sets.setLineWidth(2f);
            sets.setColor(ColorTemplate.getHoloBlue());
            sets.setHighlightEnabled(true);
            //sets.setHighLightAlpha(1000);
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
        //
        return set;
    }

    private LineData addSet(ArrayList<String> mClickedLegends, LineDataSet[] set) {

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();

        switch (set.length) {
            case 1:
                dataSets.add(set[0]);
                break;

            case 2:
                dataSets.add(set[0]);
                dataSets.add(set[1]);
                break;

            case 3:
                dataSets.add(set[0]);
                dataSets.add(set[1]);
                dataSets.add(set[2]);
                break;

            case 4:
                dataSets.add(set[0]);
                dataSets.add(set[1]);
                dataSets.add(set[2]);
                dataSets.add(set[3]);
                break;

            case 5:
                dataSets.add(set[0]);
                dataSets.add(set[1]);
                dataSets.add(set[2]);
                dataSets.add(set[3]);
                dataSets.add(set[4]);
                break;
        }
        LineData data = new LineData(dataSets);
        return data;
    }
}
