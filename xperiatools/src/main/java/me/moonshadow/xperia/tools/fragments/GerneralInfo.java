package me.moonshadow.xperia.tools.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import me.moonshadow.xperia.tools.MainActivity;
import me.moonshadow.xperia.tools.R;
import me.moonshadow.xperia.tools.helpers.ShellHelper;


public class GerneralInfo extends Fragment {

    private View rootView;

    private boolean mVisible = true;
    private Handler mRefreshHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            if (msg.what >= 1) {
                if (isVisible() && mVisible) {
                    updateAll();
                    mVisible = true;
                } else {

                }

            }
        }
    };
    private RefreshThread mRefreshThread = new RefreshThread();

    public GerneralInfo() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!mRefreshThread.isAlive()) {
            mRefreshThread.start();
            mRefreshThread.setPriority(Thread.MIN_PRIORITY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_gerneral_info, container, false);
        ImageView imageView1 = (ImageView)rootView.findViewById(R.id.imageView1);
        imageView1.setImageDrawable(getResources().getDrawable(MainActivity.mPictureArray[MainActivity.getRandomPicture()]));
        TextView textViewKernelInfo = (TextView) rootView.findViewById(R.id.textViewKernelInfo);
        textViewKernelInfo.setText(ShellHelper.getKernel());
        TextView textViewMemory = (TextView) rootView.findViewById(R.id.textView_memory);
        textViewMemory.setText(ShellHelper.getMemory("/proc/meminfo"));
        TextView textViewCpuFreq = (TextView) rootView.findViewById(R.id.textView_cpu_freq);
        String cpu_freq = "";
        if (ShellHelper.fileExists("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq")) {
            cpu_freq += " " + ShellHelper.getInfo("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
        } else {
            cpu_freq += " Offline";
        }
        if (ShellHelper.fileExists("/sys/devices/system/cpu/cpu1/cpufreq/scaling_cur_freq")) {
            cpu_freq += " " + ShellHelper.getInfo("/sys/devices/system/cpu/cpu1/cpufreq/scaling_cur_freq");
        } else {
            cpu_freq += " Offline";
        }
        if (ShellHelper.fileExists("/sys/devices/system/cpu/cpu2/cpufreq/scaling_cur_freq")) {
            cpu_freq += " " + ShellHelper.getInfo("/sys/devices/system/cpu/cpu2/cpufreq/scaling_cur_freq");
        } else {
            cpu_freq += " Offline";
        }
        if (ShellHelper.fileExists("/sys/devices/system/cpu/cpu3/cpufreq/scaling_cur_freq")) {
            cpu_freq += " " + ShellHelper.getInfo("/sys/devices/system/cpu/cpu3/cpufreq/scaling_cur_freq");
        } else {
            cpu_freq += " Offline";
        }
        textViewCpuFreq.setText(cpu_freq);
        TextView textViewGpuFreq = (TextView) rootView.findViewById(R.id.textView_gpu_freq);
        textViewGpuFreq.setText(ShellHelper.getInfo("/sys/devices/platform/kgsl-3d0.0/kgsl/kgsl-3d0/gpuclk"));
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        mVisible = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        mVisible = true;
    }

    ;

    private void updateAll() {
        View rootView = this.getView();
        TextView textViewMemory = (TextView) rootView.findViewById(R.id.textView_memory);
        textViewMemory.setText(ShellHelper.getMemory("/proc/meminfo"));
        TextView textViewCpuFreq = (TextView) rootView.findViewById(R.id.textView_cpu_freq);
        String cpu_freq = "";
        if (ShellHelper.fileExists("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq")) {
            cpu_freq += " " + ShellHelper.getInfo("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
        } else {
            cpu_freq += " Offline";
        }
        if (ShellHelper.fileExists("/sys/devices/system/cpu/cpu1/cpufreq/scaling_cur_freq")) {
            cpu_freq += " " + ShellHelper.getInfo("/sys/devices/system/cpu/cpu1/cpufreq/scaling_cur_freq");
        } else {
            cpu_freq += " Offline";
        }
        if (ShellHelper.fileExists("/sys/devices/system/cpu/cpu2/cpufreq/scaling_cur_freq")) {
            cpu_freq += " " + ShellHelper.getInfo("/sys/devices/system/cpu/cpu2/cpufreq/scaling_cur_freq");
        } else {
            cpu_freq += " Offline";
        }
        if (ShellHelper.fileExists("/sys/devices/system/cpu/cpu3/cpufreq/scaling_cur_freq")) {
            cpu_freq += " " + ShellHelper.getInfo("/sys/devices/system/cpu/cpu3/cpufreq/scaling_cur_freq");
        } else {
            cpu_freq += " Offline";
        }
        textViewCpuFreq.setText(cpu_freq);
        TextView textViewGpuFreq = (TextView) rootView.findViewById(R.id.textView_gpu_freq);
        textViewGpuFreq.setText(ShellHelper.getInfo("/sys/devices/platform/kgsl-3d0.0/kgsl/kgsl-3d0/gpuclk"));
    }

    private class RefreshThread extends Thread {

        private boolean mInterrupt = false;

        public void interrupt() {
            mInterrupt = true;
        }

        @Override
        public void run() {
            try {
                while (!mInterrupt) {
                    sleep(1000);
                    mRefreshHandler.sendEmptyMessage(1);
                }
            } catch (InterruptedException e) {

            }
        }
    }

}
