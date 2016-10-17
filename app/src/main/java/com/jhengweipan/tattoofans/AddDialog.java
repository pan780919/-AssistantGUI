package com.jhengweipan.tattoofans;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Created by kiddchen on 10/21/15.
 */
public class AddDialog extends Dialog {

    static final float[] DIMENSIONS_LANDSCAPE = {460, 260};
    static final float[] DIMENSIONS_PORTRAIT = {280, 420};
    static final FrameLayout.LayoutParams FILL =
            new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);

    private ProgressDialog mSpinner;
    private WebView mWebView;
    private LinearLayout mContent;

    public AddDialog(Context context) {
        super(context);
    }

    public WebView getWebView() {
        return mWebView;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSpinner = new ProgressDialog(getContext());
        mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSpinner.setMessage("Loading...");

        mContent = new LinearLayout(getContext());
        mContent.setOrientation(LinearLayout.VERTICAL);
        setUpTitle();
        setUpWebView();
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        final float scale = getContext().getResources().getDisplayMetrics().density;
//        float[] dimensions = ; < display.getHeight() ?
//                DIMENSIONS_PORTRAIT : DIMENSIONS_LANDSCAPE;
        Point p = new Point();
        display.getSize(p);
        addContentView(mContent, new FrameLayout.LayoutParams(
                p.x,
                (int)(p.y - 30 * scale)));
    }

    private void setUpTitle() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    protected void setUpWebView() {
        mWebView = new WebView(getContext());
        mWebView.setLayoutParams(FILL);
        mContent.addView(mWebView);
    }
}
