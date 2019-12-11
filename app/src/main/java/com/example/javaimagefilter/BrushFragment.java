package com.example.javaimagefilter;


import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.ToggleButton;

import com.example.javaimagefilter.Adapter.ColorAdapter;
import com.example.javaimagefilter.Interface.BrushFragmentListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class BrushFragment extends BottomSheetDialogFragment implements ColorAdapter.ColorAdapterListener {

    SeekBar seekBar_brush_size, seekBar_opacity_size;
    RecyclerView recyclerView_color;
    ToggleButton btn_brush_state;
    ColorAdapter colorAdapter;

    BrushFragmentListener brushFragmentListener;

    static BrushFragment brushFragmentInstance;

    public static BrushFragment getInstance(){
        if(brushFragmentInstance == null){
            brushFragmentInstance = new BrushFragment();
        }
        return brushFragmentInstance;
    }

    public void setBrushFragmentListener(BrushFragmentListener brushFragmentListener) {
        this.brushFragmentListener = brushFragmentListener;
    }

    public BrushFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView =  inflater.inflate(R.layout.fragment_brush, container, false);
        seekBar_brush_size = (SeekBar) itemView.findViewById(R.id.seekbar_brush_size);
        seekBar_opacity_size = (SeekBar) itemView.findViewById(R.id.seekbar_brush_opacity);
        btn_brush_state = (ToggleButton) itemView.findViewById(R.id.btn_brush_state);
        recyclerView_color = (RecyclerView) itemView.findViewById(R.id.recycler_color);
        recyclerView_color.setHasFixedSize(true);
        recyclerView_color.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        colorAdapter = new ColorAdapter(getContext(), getColorList(), this);
        recyclerView_color.setAdapter(colorAdapter);

        seekBar_brush_size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                brushFragmentListener.onBrushSizeChangedListener(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar_opacity_size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                brushFragmentListener.onBrushOpacityChangedListener(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btn_brush_state.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                brushFragmentListener.onBrushStateChangedListener(isChecked);
            }
        });

        return itemView;
    }

    private List<Integer> getColorList() {
        List<Integer> colorList = new ArrayList<>();

        colorList.add(Color.parseColor("#05464f"));
        colorList.add(Color.parseColor("#133a30"));
        colorList.add(Color.parseColor("#dd0000"));
        colorList.add(Color.parseColor("#006994"));
        colorList.add(Color.parseColor("#00aae4"));
        colorList.add(Color.parseColor("#1e90ff"));
        colorList.add(Color.parseColor("#0079c1"));

        colorList.add(Color.parseColor("#3eb489"));
        colorList.add(Color.parseColor("#50c878"));
        colorList.add(Color.parseColor("#7851a9"));
        colorList.add(Color.parseColor("#4b61d1"));
        colorList.add(Color.parseColor("#800020"));
        colorList.add(Color.parseColor("#ff9966"));
        colorList.add(Color.parseColor("#ff7538"));

        colorList.add(Color.parseColor("#9f1d35"));
        colorList.add(Color.parseColor("#3e8ede"));
        colorList.add(Color.parseColor("#007fff"));
        colorList.add(Color.parseColor("#c32148"));
        colorList.add(Color.parseColor("#39ca74"));
        colorList.add(Color.parseColor("#933028"));
        colorList.add(Color.parseColor("#f26b43"));

        colorList.add(Color.parseColor("#ef5927"));
        colorList.add(Color.parseColor("#e5e5cc"));
        colorList.add(Color.parseColor("#0067a5"));
        colorList.add(Color.parseColor("#023352"));
        colorList.add(Color.parseColor("#0d5572"));
        colorList.add(Color.parseColor("#b7504c"));
        colorList.add(Color.parseColor("#9c3bf3"));

        colorList.add(Color.parseColor("#0f4c81"));
        colorList.add(Color.parseColor("#a64ca6"));
        colorList.add(Color.parseColor("#b7504c"));
        colorList.add(Color.parseColor("#8d0d0d"));
        colorList.add(Color.parseColor("#ffa891"));
        colorList.add(Color.parseColor("#8b0000"));
        colorList.add(Color.parseColor("#990000"));

        colorList.add(Color.parseColor("#dfb63f"));
        colorList.add(Color.parseColor("#e1ae1b"));
        colorList.add(Color.parseColor("#c6962c"));
        colorList.add(Color.parseColor("#b18628"));
        colorList.add(Color.parseColor("#7a3ee8"));
        colorList.add(Color.parseColor("#819bb4"));
        colorList.add(Color.parseColor("#2c89c4"));


        return colorList;
    }

    @Override
    public void onColorSelected(int color) {
        brushFragmentListener.onBrushColorChangedListener(color);
    }
}
