package com.gachon.ccpp;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class LoadingDialog extends Dialog
{
    LottieAnimationView animationView;
    TextView loadingText;

    public LoadingDialog(Context context)
    {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_loading);

        animationView = findViewById(R.id.animation);
        loadingText = findViewById(R.id.loading_text);
    }

    public void show(String image, String text) {
        if (image != null) {
            animationView.setAnimation(image);
            animationView.loop(true);
            animationView.playAnimation();
            animationView.setVisibility(View.VISIBLE);
        }
        else
            animationView.setVisibility(View.INVISIBLE);

        if (text != null) {
            loadingText.setText(text);
            loadingText.setVisibility(View.VISIBLE);
        }
        else
            loadingText.setVisibility(View.INVISIBLE);

        super.show();
    }

    @Override
    public void show() {
        show(null, null);
    }

    public void setText(String text) {
        loadingText.setText(text);
    }
}