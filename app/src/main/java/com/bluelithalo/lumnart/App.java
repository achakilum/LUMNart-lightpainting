package com.bluelithalo.lumnart;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;


public class App extends Application
{
    private static Application sApp;

    public static Application getApplication()
    {
        return sApp;
    }

    public static Context getContext()
    {
        return getApplication().getApplicationContext();
    }

    public void onCreate()
    {
        super.onCreate();
        sApp = this;
    }

    public static float getAspectRatio(boolean inverse)
    {
        DisplayMetrics displayInfo = getContext().getResources().getDisplayMetrics();

        int width = displayInfo.widthPixels;
        int height = displayInfo.heightPixels;

        float aspectRatio = 1.0f;

        if (inverse)
        {
            aspectRatio = Math.min(height, width) / (Math.max(height, width) * 1.0f);
        }
        else
        {
            aspectRatio = Math.max(width, height) / (Math.min(width, height) * 1.0f);
        }

        return aspectRatio;
    }

}
