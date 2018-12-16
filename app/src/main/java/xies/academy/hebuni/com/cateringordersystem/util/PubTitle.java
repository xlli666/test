package xies.academy.hebuni.com.cateringordersystem.util;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import xies.academy.hebuni.com.cateringordersystem.activity.R;

public class PubTitle extends LinearLayout {
    public PubTitle(Context context, AttributeSet attrs){
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.commen_title, this);
        TextView tvBack = findViewById(R.id.left);
        tvBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)getContext()).finish();
            }
        });
    }
}
