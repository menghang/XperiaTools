package me.moonshadow.xperia.tools.fragments;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import me.moonshadow.xperia.tools.MainActivity;
import me.moonshadow.xperia.tools.R;
import me.moonshadow.xperia.tools.helpers.RootHelper;
import me.moonshadow.xperia.tools.helpers.ShellHelper;


public class CpuSettings extends Fragment {

    private Context context;
    private View rootView;

    public CpuSettings(Context context) {
        // Required empty public constructor
        this.context = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_cpu_settings, container, false);
        ImageView imageView1 = (ImageView)rootView.findViewById(R.id.imageView1);
        imageView1.setImageResource(MainActivity.mPictureArray[MainActivity.getRandomPicture()]);
        TextView textViewCpu = (TextView) rootView.findViewById(R.id.textView_cpu);
        textViewCpu.setText("CPU 0");
        Spinner spinner_cpu_max_freq = (Spinner) rootView.findViewById(R.id.spinner1);
        Spinner spinner_cpu_min_freq = (Spinner) rootView.findViewById(R.id.spinner2);
        if (RootHelper.isDeviceRooted()
                && ShellHelper.fileExists("/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies")) {
            String[] freqs = ShellHelper.getInfoArray("/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies", 0, 0);
            if (ShellHelper.fileExists("/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq")
                    && ShellHelper.fileExists("/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq")) {
                String max_freq = ShellHelper.getRootInfo("/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq");
                String min_freq = ShellHelper.getRootInfo("/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq");
                int index_max = 0, index_min = 0;
                for (int i = 0; i < freqs.length; i++) {
                    if (freqs[i].equals(max_freq)) {
                        index_max = i;
                    }
                    if (freqs[i].equals(min_freq)) {
                        index_min = i;
                    }
                }
                ArrayAdapter<String> aa = new ArrayAdapter<String>(context,
                        android.R.layout.simple_spinner_item, freqs);
                aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_cpu_max_freq.setAdapter(aa);
                spinner_cpu_max_freq.setPrompt(getString(R.string.cpu_max_freq));
                spinner_cpu_max_freq.setSelection(index_max);
                spinner_cpu_max_freq.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int position, long id) {
                        String freq = parent.getItemAtPosition(position).toString();
                        ShellHelper.setRootInfo(freq, "/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq");
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // TODO Auto-generated method stub
                    }
                });
                spinner_cpu_min_freq.setAdapter(aa);
                spinner_cpu_min_freq.setPrompt(getString(R.string.cpu_min_freq));
                spinner_cpu_min_freq.setSelection(index_min);
                spinner_cpu_min_freq.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int position, long id) {
                        String freq = parent.getItemAtPosition(position).toString();
                        ShellHelper.setRootInfo(freq, "/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq");
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // TODO Auto-generated method stub
                    }
                });
            } else {
                String[] strs = {"Unavailable!"};
                if (ShellHelper.fileExists("/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq")) {
                    strs[0] = ShellHelper.getRootInfo("/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq");
                }
                ArrayAdapter<String> aa = new ArrayAdapter<String>(context,
                        android.R.layout.simple_spinner_item, strs);
                spinner_cpu_max_freq.setAdapter(aa);
                spinner_cpu_max_freq.setEnabled(false);

                if (ShellHelper.fileExists("/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq")) {
                    strs[0] = ShellHelper.getRootInfo("/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq");
                }
                ArrayAdapter<String> aa2 = new ArrayAdapter<String>(context,
                        android.R.layout.simple_spinner_item, strs);
                spinner_cpu_min_freq.setAdapter(aa2);
                spinner_cpu_min_freq.setEnabled(false);
            }
        } else {
            String[] strs = {"Unavailable!"};
            ArrayAdapter<String> aa = new ArrayAdapter<String>(context,
                    android.R.layout.simple_spinner_item, strs);
            spinner_cpu_min_freq.setAdapter(aa);
            spinner_cpu_max_freq.setAdapter(aa);
            spinner_cpu_max_freq.setEnabled(false);
            spinner_cpu_min_freq.setEnabled(false);
        }

        Spinner spinner_cpu_gov = (Spinner) rootView.findViewById(R.id.spinner3);
        if (ShellHelper.fileExists("/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_governors")) {
            String[] str_cpu_govs = ShellHelper.getInfoArray("/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_governors", 0, 1);
            String str_cpu_gov = ShellHelper.getInfoString(ShellHelper.getInfo("/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor"));
            int index_cpu_gov = 0;
            for (int i = 0; i < str_cpu_govs.length; i++) {
                if (str_cpu_govs[i].equals(str_cpu_gov)) {
                    index_cpu_gov = i;
                }
            }
            ArrayAdapter<String> aa = new ArrayAdapter<String>(context,
                    android.R.layout.simple_spinner_item, str_cpu_govs);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_cpu_gov.setAdapter(aa);
            spinner_cpu_gov.setPrompt(getString(R.string.cpu_governor));
            spinner_cpu_gov.setSelection(index_cpu_gov);
            spinner_cpu_gov.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    String cpu_gov_set = parent.getItemAtPosition(position).toString();
                    ShellHelper.setRootInfo(cpu_gov_set, "/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor");
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // TODO Auto-generated method stub
                }
            });
        } else {
            String[] strs = {"Unavailable!"};
            ArrayAdapter<String> aa = new ArrayAdapter<String>(context,
                    android.R.layout.simple_spinner_item, strs);
            spinner_cpu_gov.setAdapter(aa);
            spinner_cpu_gov.setEnabled(false);
        }
        return rootView;
    }


}
