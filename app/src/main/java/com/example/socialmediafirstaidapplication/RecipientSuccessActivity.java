package com.example.socialmediafirstaidapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.os.Bundle;

import com.baoyachi.stepview.HorizontalStepView;
import com.baoyachi.stepview.VerticalStepView;
import com.baoyachi.stepview.bean.StepBean;

import java.util.ArrayList;
import java.util.List;

public class RecipientSuccessActivity extends AppCompatActivity {
    private HorizontalStepView horizontalStepView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipient_success);
        setupUIViews();
    }

    private void setupUIViews(){
        horizontalStepView = (HorizontalStepView)findViewById(R.id.statusStepView);

        List<StepBean> status = new ArrayList();
        status.add(new StepBean("Submitted",1));
        status.add(new StepBean("Accepted",0));
        status.add(new StepBean("Resolved",-1));

        horizontalStepView
                .setStepViewTexts(status)
                .setTextSize(12)
                .setStepsViewIndicatorCompletedLineColor(Color.parseColor("#212121"))
                .setStepViewComplectedTextColor(Color.parseColor("##E91E63"))
                .setStepViewUnComplectedTextColor(Color.parseColor("#212121"))
                .setStepsViewIndicatorUnCompletedLineColor(Color.parseColor("#757575"))
                .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(this, R.drawable.complted))
                .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(this, R.drawable.attention))
                .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(this, R.drawable.default_icon));

    }
}
