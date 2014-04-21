package me.moonshadow.xperia.tools.fragments;


import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.echo.holographlibrary.Bar;
import com.echo.holographlibrary.BarGraph;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import me.moonshadow.xperia.tools.R;
import me.moonshadow.xperia.tools.adapter.StatisticAdapter;
import me.moonshadow.xperia.tools.adapter.statisticInit;
import me.moonshadow.xperia.tools.helpers.ShellHelper;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class CpuStatusBar extends Fragment {

    private static final String[] color_code = {
            "#1abc9c", /* Turquoise */
            "#FF8800", /* Orange */
            "#2c3e50", /* Midnight Blue */
            "#2980b9", /* Nephritis */
            "#CF000F", /* Monza */
            "#8e44ad", /* Wisteria */
            "#3498db", /* Peter River */
            "#e74c3c", /* Pomegrante */
    };
    private static final String TIME_IN_STATE_PATH = "/sys/devices/system/cpu/cpu0/cpufreq/stats/time_in_state";
    private static final String OFFSET_STAT = "/data/data/me.moonshadow.xperia.tools/files/offset_stat";
    private final static Typeface font = Typeface.create("sans-serif-condensed", Typeface.NORMAL);
    private int mIndex = 0;
    private String[] data;
    private ListView statisticView;
    private BarGraph bg;
    private TextView txtFreq;
    private TextView txtPercentage;
    private TextView txtTime;
    private ArrayList<Long> cpuTime = new ArrayList<Long>();
    private ArrayList<Long> cpuOverallTime = new ArrayList<Long>();
    private ArrayList<Long> cpuFreq = new ArrayList<Long>();
    private ArrayList<Long> cpuPercentage = new ArrayList<Long>();
    private ArrayList<Long> cpuResetTime;
    private statisticInit[] mResult = new statisticInit[0];
    private int mColorIndex = 0;
    private View rootView;
    private double mCompleteTime = 0;


    public CpuStatusBar() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_cpu_status_bar, container, false);
        clearUI();
        mIndex = 0;
        mColorIndex = 0;
        loadResetState();
        loadUI(true);
        return rootView;
    }

    private final void clearUI() {

        /*
         * Cleanup the whole UI.
         * Notice: PieGraph and data might be cleaned anyway,
         * clearing cpuTime/cpuFreq/cpuPercentage AND mResult
         * is _really_ necessary:
         */

        if (data != null)
            data = new String[0];

        if (cpuTime != null)
            cpuTime.clear();

        if (cpuOverallTime != null)
            cpuOverallTime.clear();

        if (cpuFreq != null)
            cpuFreq.clear();

        if (cpuPercentage != null)
            cpuPercentage.clear();

        if (statisticView != null) {
            mResult = new statisticInit[0];
        }
    }

    private final void loadUI(boolean firstView) {

        final ArrayList<String> cpuGraphValues = new ArrayList<String>();
        Long[] cpuFreqArray;
        double a;
        int cpuData = getCpuData();
        mCompleteTime = 0;
        bg = (BarGraph) rootView.findViewById(R.id.graph);

        // Handle no cpu data found;
        if (cpuData == 0) {
            rootView.findViewById(R.id.noCpuData).setVisibility(View.VISIBLE);
        } else
            rootView.findViewById(R.id.noCpuData).setVisibility(View.GONE);

        for (int k = 0; k < cpuData; k++) {
            String b = data[k];
            String[] c = b.split(" ");
            if (k == 0) {
                a = Integer.parseInt(c[0]);
            } else {
                a = Integer.parseInt(c[1]);
            }
            cpuOverallTime.add((long) a);

            mCompleteTime = mCompleteTime + a;
        }
        cpuOverallTime.add((long) mCompleteTime);

        // Handle Uptime here, maybe we don't want to reset it anyway...
        if (cpuResetTime != null) {
            String resetUptime = (ShellHelper.getInfoArray(OFFSET_STAT, 0, 0))[(ShellHelper.getInfoArray(OFFSET_STAT, 0, 0)).length - 1];
            long mResetTime = Long.parseLong(resetUptime);
            mCompleteTime = mCompleteTime - mResetTime;
        }

        for (int i = 0, j = 0; i < cpuData; i++) {

            String b = data[i];
            String[] c = b.split(" ");

            // Color change;
            if (j == 8)
                j = 0;

            /*
             * Handle deepsleep, if statistics are resetted hook into the calculation process;
             */
            if (i == 0) {
                cpuFreq.add((long) 0);
                if (cpuResetTime != null) {
                    cpuTime.add((long) Integer.parseInt(c[0]) - Long.parseLong(ShellHelper.getInfoArray(OFFSET_STAT, 0, 0)[i]));
                } else {
                    cpuTime.add((long) Integer.parseInt(c[0]));
                }
            } else {
                cpuFreq.add((long) Integer.parseInt(c[0]));
                if (cpuResetTime != null) {
                    cpuTime.add((long) Integer.parseInt(c[1]) - Long.parseLong(ShellHelper.getInfoArray(OFFSET_STAT, 0, 0)[i]));
                } else {
                    cpuTime.add((long) Integer.parseInt(c[1]));
                }
            }

        }

        cpuFreqArray = cpuFreq.toArray(new Long[0]);

        int i = 0;
        int j = 0;

        ArrayList<Bar> bar = new ArrayList<Bar>();
        for (long g : cpuTime) {

            String frequency, time_in_state;
            int percentage;

            // Color change;
            if (j == 8)
                j = 0;

            if (cpuFreqArray[i] == 0)
                frequency = "DeepSleep";
            else
                frequency = ShellHelper.toMHz(cpuFreqArray[i].toString());

            time_in_state = convertTime(g);
            percentage = (int) Math.round((g / mCompleteTime) * 100);
            // Safe all percentages in our array;
            cpuPercentage.add((long) percentage);

            if (g != 0 && percentage >= 1) {

                Bar tmpBar = new Bar();
                cpuGraphValues.add(frequency + " " + time_in_state + " " + percentage + "%");

                tmpBar.setValue((float) percentage / 100);
                tmpBar.setName(frequency);
                tmpBar.setColor(Color.parseColor(color_code[j]));
                bar.add(tmpBar);
                j++;
            }
            i++;
        }
        bg.setBars(bar);
        bg.setUnit(" ");
        // Fill our listview with final values and load TextViews;
        createList(cpuFreq, cpuTime, cpuPercentage);
        if (firstView)
            handleOnClick(cpuGraphValues);

        bg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                    handleOnClick(cpuGraphValues);
                    return true;
                }

                return false;
            }
        });

    }

    private final void handleOnClick(ArrayList<String> list) {

        final String[] valueArray = list.toArray(new String[0]);

        for (String a : valueArray) {

            int arrayLength = valueArray.length;

            if (mIndex == arrayLength) {
                mIndex = 0;
                mColorIndex = 0;
            }

            /*
             * Fix exception;
             */
            if (mColorIndex >= 8)
                mColorIndex = 0;

            String currentRow = valueArray[mIndex];
            String[] tmp = currentRow.split(" ");

            txtFreq = (TextView) rootView.findViewById(R.id.statisticFreq);
            txtTime = (TextView) rootView.findViewById(R.id.statisticTime);
            txtPercentage = (TextView) rootView.findViewById(R.id.statisticPercentage);

            if (tmp[1].contains("MHz")) {
                tmp[0] = tmp[0] + " MHz";
                tmp[1] = tmp[2];
                tmp[2] = tmp[3];
            }
            txtFreq.setText(tmp[0]);
            txtTime.setText(tmp[1]);
            txtPercentage.setText(tmp[2]);

            txtFreq.setTypeface(font);
            txtTime.setTypeface(font);
            txtPercentage.setTypeface(font);

            txtFreq.setTextColor(Color.parseColor(color_code[mColorIndex]));
            txtTime.setTextColor(Color.parseColor(color_code[mColorIndex]));
            txtPercentage.setTextColor(Color.parseColor(color_code[mColorIndex]));
        }
        mColorIndex++;
        mIndex++;
    }

    private final void createList(ArrayList<Long> cpuFreq, ArrayList<Long> cpuTime, ArrayList<Long> cpuPercentage) {

        // Add Complete Uptime;
        cpuFreq.add((long) 1);
        cpuTime.add((long) mCompleteTime);
        cpuPercentage.add((long) 100);

        // Get Data;
        Long[] freq = cpuFreq.toArray(new Long[0]);
        Long[] time = cpuTime.toArray(new Long[0]);
        Long[] percentage = cpuPercentage.toArray(new Long[0]);

        ArrayDataLoader adl = new ArrayDataLoader();
        adl.loadSingleEntry(freq, time, percentage);

        statisticView = (ListView) rootView.findViewById(R.id.statisticListView);

        StatisticAdapter adapter = new StatisticAdapter(getActivity(),
                R.layout.statistic_layout, mResult);

        statisticView.setAdapter(adapter);

    }

    private final String convertTime(long msTime) {

        msTime = msTime * 10;

        return String.format("%02dh:%02dm:%02ds",
                TimeUnit.MILLISECONDS.toHours(msTime),
                TimeUnit.MILLISECONDS.toMinutes(msTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(msTime)),
                TimeUnit.MILLISECONDS.toSeconds(msTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(msTime))
        );
    }

    private void loadResetState() {

        /*
         * First Case; user resetted statistics, but closed app
         * Second Case; user resetted statistics once, but rebooted
         */

        File a = new File(OFFSET_STAT);

        // Handle First case;
        if (a.exists() && cpuResetTime == null) {
            cpuResetTime = new ArrayList<Long>();
            String[] array = ShellHelper.getInfoArray(OFFSET_STAT, 0, 0);

            for (String b : array) {
                cpuResetTime.add(Long.parseLong(b));
            }

            //Handle second case here;
            if (Long.parseLong(array[array.length - 1]) > (SystemClock.elapsedRealtime() / 10)) {
                a.delete();
                cpuResetTime = null;
            }
        }

    }

    private final int getCpuData() {

        File cpu_stats = new File(TIME_IN_STATE_PATH);

        if (!cpu_stats.exists())
            return 0;

        data = ShellHelper.getInfo(TIME_IN_STATE_PATH, true);

        return data.length;
    }

    private final class ArrayDataLoader {

        public final void loadSingleEntry(Long[] freq, Long[] time, Long[] percentage) {

            int length = freq.length;

            for (int j = 0; j < length; j++) {

                // Doing the percentage check here again;
                if (percentage[j] != 0 && percentage[j] >= 1) {
                    String convertedFreq = ShellHelper.toMHz(freq[j] + "");

                    // Small UI-Tweak;
                    if (convertedFreq.length() < 8)
                        convertedFreq = convertedFreq + "\t";
                    else if (convertedFreq.length() < 7)
                        convertedFreq = convertedFreq + "\t\t";

                    // Handle Deepsleep
                    if (j == 0)
                        loadArray(mResult, new statisticInit("Deepsleep", convertTime(time[j]) + "", percentage[j] + "%"));
                    else if (j == length - 1)
                        loadArray(mResult, new statisticInit("Uptime   ", convertTime(time[j]) + "", percentage[j] + "%"));
                    else
                        loadArray(mResult, new statisticInit(convertedFreq, convertTime(time[j]) + "", percentage[j] + "%"));
                }
            }

        }

        /*
         * Just a wrapper;
         */
        private final void loadArray(statisticInit[] resultSet, statisticInit data) {

            mResult = fillArray(resultSet, data);
        }

        private final statisticInit[] fillArray(statisticInit[] resultSet, statisticInit data) {

            statisticInit[] result = Arrays.copyOf(resultSet, resultSet.length + 1);
            result[resultSet.length] = data;

            return result;
        }
    }


}
