package com.example.vadym.movieapp.util;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.vadym.movieapp.R;
import com.example.vadym.movieapp.activities.OnBottomSheetListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vadym on 06.03.2018.
 */

public class BottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {

    @BindView(R.id.runtimeEditText)
    EditText runtimeEditText;
    @BindView(R.id.yearEditText)
    EditText yearEditText;
    @BindView(R.id.voteCountEditText)
    EditText voteCountEditText;
    @BindView(R.id.submitBtn)
    Button submitBtn;

    private String runtimeText;
    private String yearText;
    private String voteCountText;
    private OnBottomSheetListener listener;

    public BottomSheet() {
        runtimeText = String.valueOf(120);
        yearText = String.valueOf(2017);
        voteCountText = String.valueOf(7.1);
    }

    public void setBottomListener(OnBottomSheetListener listener) {
        this.listener = listener;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(inflater.getContext()).inflate(R.layout.bottom_sheet, null, false);
        ButterKnife.bind(this, view);

        if (submitBtn != null)
            submitBtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onClick(View view) {
        if (listener != null) {
            getValue();
            dismiss();
            listener.submit();
        }
    }

    private void getValue() {
        if (!"".equals(runtimeEditText.getText().toString())) {
            runtimeText = runtimeEditText.getText().toString();
        }
        if (!"".equals(yearEditText.getText().toString())) {
            yearText = yearEditText.getText().toString();
        }
        if (!"".equals(voteCountEditText.getText().toString())) {
            voteCountText = voteCountEditText.getText().toString();
        }
    }

    public String getRuntimeText() {
        return runtimeText;
    }

    public String getYearText() {
        return yearText;
    }

    public String getVoteCountText() {
        return voteCountText;
    }

}
