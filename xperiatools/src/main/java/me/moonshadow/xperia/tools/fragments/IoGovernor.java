package me.moonshadow.xperia.tools.fragments;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import me.moonshadow.xperia.tools.R;
import me.moonshadow.xperia.tools.helpers.ShellHelper;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class IoGovernor extends Fragment {

    private Context context;

    public IoGovernor(Context context) {
        // Required empty public constructor
        this.context = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_io_governor, container, false);
        Spinner spinner_io = (Spinner) rootView.findViewById(R.id.spinner1);
        if (ShellHelper.fileExists("/sys/block/mmcblk0/queue/scheduler")) {
            String[] str_ios = ShellHelper.getInfoArray("/sys/block/mmcblk0/queue/scheduler", 0, 1);
            String str_io = ShellHelper.getInfoString(ShellHelper.getInfo("/sys/block/mmcblk0/queue/scheduler"));
            int index_io = 0;
            for (int i = 0; i < str_ios.length; i++) {
                if (str_ios[i].equals(str_io)) {
                    index_io = i;
                }
            }
            ArrayAdapter<String> aa = new ArrayAdapter<String>(context,
                    android.R.layout.simple_spinner_item, str_ios);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_io.setAdapter(aa);
            spinner_io.setPrompt(getString(R.string.io_scheduler));
            spinner_io.setSelection(index_io);
            spinner_io.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    String io_set = parent.getItemAtPosition(position).toString();
                    ShellHelper.setRootInfo(io_set, "/sys/block/mmcblk0/queue/scheduler");
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
            spinner_io.setAdapter(aa);
            spinner_io.setEnabled(false);
        }
        return rootView;
    }


}
