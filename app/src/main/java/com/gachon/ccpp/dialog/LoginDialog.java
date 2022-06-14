package com.gachon.ccpp.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.gachon.ccpp.R;

public class LoginDialog extends Dialog
{
    LottieAnimationView animationView;
    TextView loadingText;

    public LoginDialog(Context context)
    {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login_dialog);

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

        this.setText(text);

        super.show();
    }

    @Override
    public void show() {
        show(null, null);
    }

    public void setText(String text) {
        if (text != null) {
            loadingText.setText(text);
            loadingText.setVisibility(View.VISIBLE);
        }
        else
            loadingText.setVisibility(View.INVISIBLE);
    }
}