package com.algebratech.pulse_wellness.utils;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.algebratech.pulse_wellness.R;
import com.wang.avi.AVLoadingIndicatorView;


/**
 * Created by Akash Patel on 11/22/2016.
 */
public class CustomDialogClass extends Dialog
{
    public Activity c;
    public Dialog d;
    AVLoadingIndicatorView loader;

    public CustomDialogClass(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_loader_view);
        View v = getWindow().getDecorView();
        v.setBackgroundResource(android.R.color.transparent);

        loader = (AVLoadingIndicatorView) findViewById(R.id.indicator);
        loader.show();
    }

    @Override
    public boolean isShowing()
    {
        return super.isShowing();
    }

    @Override
    public void dismiss() {
        super.dismiss();

    }
}
