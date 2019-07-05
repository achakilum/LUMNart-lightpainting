package com.bluelithalo.lumnart;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bluelithalo.lumnart.pattern.Pattern;
import com.bluelithalo.lumnart.util.GLESTBAM;
import com.bluelithalo.lumnart.util.ShaderUtils;
import com.bluelithalo.lumnart.util.SnackbarFactory;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;

public class GLTestActivity extends AppCompatActivity
{

    public static int patternId = 1;
    private GLTestView lView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent glTestIntent = getIntent();
        Pattern testPattern = null;

        try
        {
            String filepath = glTestIntent.getStringExtra("patternFilepath");
            //Log.i("GLTestActivity", filepath);

            File patternFile = new File(filepath);
            FileInputStream inStream = new FileInputStream(patternFile);
            int ch = 0;

            StringBuffer jsonStringBuffer = new StringBuffer("");
            while ((ch = inStream.read()) != -1)
            {
                jsonStringBuffer.append((char) ch);
            }

            String jsonString = new String(jsonStringBuffer);
            JSONObject jsonObject = new JSONObject(jsonString);
            testPattern = new Pattern(jsonObject);

            /*
            String logMessage = jsonObject.toString();
            for (int i = 0; i <= logMessage.length() / 1000; i++)
            {
                int start = i * 1000;
                int end = (i+1) * 1000;
                end = Math.min(end, logMessage.length());
                //Log.i("PatternJson", logMessage.substring(start, end));
            }
            */

            //Log.i("GLTestActivity", jsonObject.toString());
        }
        catch (Exception e)
        {
            //Log.e("GLTestActivity", e.toString());
            Toast.makeText(App.getContext(), "Not a valid Pattern file.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        testPattern.unhideAllComponents(true);
        lView = new GLTestView(this, testPattern, false);
        lView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setContentView(lView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gltest, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onDestroy()
    {
        super.onDestroy();

        GLESTBAM.deinitialize();
        ShaderUtils.freeShaderProgram();
    }
}
