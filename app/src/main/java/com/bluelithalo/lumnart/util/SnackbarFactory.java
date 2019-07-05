package com.bluelithalo.lumnart.util;

import android.view.View;
import android.widget.TextView;

import com.bluelithalo.lumnart.App;
import com.bluelithalo.lumnart.R;
import com.google.android.material.snackbar.Snackbar;

public class SnackbarFactory
{
    public static void showSnackbar(View view, String text, int length)
    {
        Snackbar sb = Snackbar.make(view, text, length);
        View sbView = sb.getView();

        sbView.setBackgroundColor(App.getContext().getResources().getColor(R.color.header_color));
        TextView tv = (TextView) sbView.findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setTextColor(App.getContext().getResources().getColor(R.color.header_text_color));

        sb.setAction("Action", null);
        sb.show();
    }
}
