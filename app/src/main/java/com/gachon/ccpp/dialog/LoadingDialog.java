package com.gachon.ccpp.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import com.gachon.ccpp.R;

public class LoadingDialog extends Dialog {
    public LoadingDialog(Context context){
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.loading_dialog);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }
}
