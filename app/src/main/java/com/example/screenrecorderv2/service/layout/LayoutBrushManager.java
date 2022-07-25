package com.example.screenrecorderv2.service.layout;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.screenrecorderv2.R;
import com.example.screenrecorderv2.base.rx.RxBusHelper;
import com.example.screenrecorderv2.service.base.BaseLayoutWindowManager;
import com.example.screenrecorderv2.ui.adapter.ColorAdapter;
import com.example.screenrecorderv2.utils.ScreenRecordHelper;
import com.raed.drawingview.BrushView;
import com.raed.drawingview.DrawingView;
import com.raed.drawingview.brushes.BrushSettings;
import com.raed.drawingview.brushes.Brushes;

import java.util.ArrayList;

public class LayoutBrushManager extends BaseLayoutWindowManager {
    private DrawingView drawingView;
    private ColorAdapter colorAdapter;

    public LayoutBrushManager(Context context) {
        super(context);
        initParams();
        addLayout();
    }

    private void initParams() {
        mParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        mParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected int getRootViewID() {
        return R.layout.layout_main_brush;
    }

    @Override
    protected void initLayout() {
        drawingView = rootView.findViewById(R.id.drawview);
        RecyclerView rcv = rootView.findViewById(R.id.rcv);
        ConstraintLayout containerColor = rootView.findViewById(R.id.container_color);
        LinearLayout layoutBrush = rootView.findViewById(R.id.layout_brush);
        ImageView imgClose = rootView.findViewById(R.id.imgClose);
        ImageView imvClose = rootView.findViewById(R.id.imv_close);
        ImageView imgCamera = rootView.findViewById(R.id.imgCamera);
        ImageView imgPaint = rootView.findViewById(R.id.imgPaint);
        CheckBox imgEraser = rootView.findViewById(R.id.imgEraser);
        CheckBox imgBrush = rootView.findViewById(R.id.imgBrush);
        SeekBar mSizeSeekBar = rootView.findViewById(R.id.size_seek_bar);

        // drawview
        BrushView brushView = rootView.findViewById(R.id.brush_view);
        brushView.setDrawingView(drawingView);
        BrushSettings brushSettings = drawingView.getBrushSettings();
        drawingView.setUndoAndRedoEnable(true);
        brushSettings.setColor(colorTransparent);
        imgEraser.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                imgBrush.setChecked(false);
                brushSettings.setSelectedBrush(Brushes.ERASER);
            } else {
                brushSettings.setColor(colorTransparent);
                brushSettings.setSelectedBrush(Brushes.PEN);
            }
        });
        imgBrush.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                imgEraser.setChecked(false);
                brushSettings.setSelectedBrush(Brushes.PEN);
                if (colorAdapter != null) {
                    brushSettings.setColor(colorAdapter.getItemCheck());
                }
            } else {
                brushSettings.setColor(colorTransparent);
                brushSettings.setSelectedBrush(Brushes.PEN);
            }
        });
        imgPaint.setOnClickListener(v -> {
            containerColor.setVisibility(View.VISIBLE);
            layoutBrush.setVisibility(View.GONE);
        });
        imgCamera.setOnClickListener(v -> {
            if (ScreenRecordHelper.STATE != ScreenRecordHelper.State.RECORDING) {
                layoutBrush.setVisibility(View.GONE);
                RxBusHelper.sendClickBrushScreenShot();
            }
        });

        mSizeSeekBar.setMax(100);
        mSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                brushSettings.setSelectedBrushSize(i / 100f);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                brushView.setVisibility(View.VISIBLE);
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                brushView.setVisibility(View.GONE);
            }
        });

        colorAdapter = new ColorAdapter(initColors(), context);
        colorAdapter.setCallBackAdapter(brushSettings::setColor);
        rcv.setAdapter(colorAdapter);
        rcv.setVisibility(View.VISIBLE);

        imgClose.setOnClickListener(v -> {
            removeLayout();
        });
        imvClose.setOnClickListener(v -> {
            containerColor.setVisibility(View.GONE);
            layoutBrush.setVisibility(View.VISIBLE);
        });
    }

    private ArrayList<Integer> initColors() {
        ArrayList<Integer> datas = new ArrayList<>();
        datas.add(Color.parseColor("#ffffff"));
        datas.add(Color.parseColor("#9E9E9E"));
        datas.add(Color.parseColor("#000000"));
        datas.add(Color.parseColor("#972929"));
        datas.add(Color.parseColor("#F82C1F"));
        datas.add(Color.parseColor("#F86D1F"));
        datas.add(Color.parseColor("#F8C81F"));
        datas.add(Color.parseColor("#53CB2C"));
        datas.add(Color.parseColor("#31D2BF"));
        datas.add(Color.parseColor("#3198D2"));
        datas.add(Color.parseColor("#2158E7"));
        datas.add(Color.parseColor("#8F21E7"));
        return datas;
    }

    private int colorTransparent = Color.parseColor("#00FFFFFF");
}
